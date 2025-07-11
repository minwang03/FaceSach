package com.example.facesach.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.Message;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText edtMessage;
    private Button btnSend;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    private Socket socket;
    private int currentUserId;
    private int receiverId;
    private String currentRoom = null;

    private Spinner spinnerUsers;
    private List<User> userList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private Map<String, Integer> userNameToId = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Lấy user hiện tại từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("user_data", null);
        if (json != null) {
            User user = new Gson().fromJson(json, User.class);
            currentUserId = user.getUser_id();
            Log.d("DEBUG", "CurrentUserId = " + currentUserId);
        }

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.recyclerChat);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        spinnerUsers = view.findViewById(R.id.spinnerUsers);

        // Setup RecyclerView
        adapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Setup Spinner
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(spinnerAdapter);

        // Khởi tạo socket
        initSocket();

        // Gửi tin nhắn
        btnSend.setOnClickListener(v -> {
            String text = edtMessage.getText().toString().trim();
            if (!text.isEmpty() && currentRoom != null) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sender_id", currentUserId);
                    jsonObject.put("room", currentRoom);
                    jsonObject.put("message", text);

                    socket.emit("sendMessage", jsonObject);
                    edtMessage.setText("");
                    Log.d("SocketEmit", "Emitted sendMessage: " + jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fetchUserList();

        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) parent.getItemAtPosition(position);
                receiverId = userNameToId.get(selectedName);
                String newRoom = generateRoomId(currentUserId, receiverId);

                // Rời room cũ
                if (socket != null && currentRoom != null && !currentRoom.equals(newRoom)) {
                    socket.emit("leaveRoom", currentRoom);
                    Log.d("SocketStatus", "Left room: " + currentRoom);
                }

                // Tham gia room mới
                currentRoom = newRoom;
                socket.emit("joinRoom", currentRoom);
                Log.d("SocketStatus", "Joined room: " + currentRoom);

                // Lấy tin nhắn cũ
                fetchMessagesFromApi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void fetchUserList() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllUsers().enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<User>>> call, @NonNull Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> userNames = new ArrayList<>();
                    userList.clear();
                    userList.addAll(response.body().getData());
                    userNameToId.clear();

                    for (User user : userList) {
                        if (user.getUser_id() != currentUserId) {
                            userNames.add(user.getName());
                            userNameToId.put(user.getName(), user.getUser_id());
                        }
                    }

                    spinnerAdapter.clear();
                    spinnerAdapter.addAll(userNames);
                    spinnerAdapter.notifyDataSetChanged();

                    if (!userNames.isEmpty()) {
                        spinnerUsers.setSelection(0);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<User>>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String generateRoomId(int user1, int user2) {
        return (user1 < user2) ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    private void initSocket() {
        try {
            socket = IO.socket("http://10.7.66.211:3000");

            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("SocketStatus", "Socket connected");
                if (currentRoom != null) {
                    socket.emit("joinRoom", currentRoom);
                    Log.d("SocketStatus", "Emitted joinRoom: " + currentRoom);
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.e("SocketStatus", "Connect error"));
            socket.on(Socket.EVENT_DISCONNECT, args -> Log.d("SocketStatus", "Socket disconnected"));
            socket.on("newMessage", onNewMessage);

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (socket != null) {
            socket.disconnect();
            socket.off("newMessage", onNewMessage);
        }
    }

    private final Emitter.Listener onNewMessage = args -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                Log.d("SocketRawData", "Received newMessage: " + data.toString());
                Message newMsg = new Gson().fromJson(data.toString(), Message.class);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        messageList.add(newMsg);
                        adapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void fetchMessagesFromApi() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getPrivateMessages(currentUserId, receiverId).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Message>>> call, @NonNull Response<ApiResponse<List<Message>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Message>>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
