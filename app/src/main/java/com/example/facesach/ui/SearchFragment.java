package com.example.facesach.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText edtMessage;
    Button btnSend;
    private MessageAdapter adapter;
    List<Message> messageList = new ArrayList<>();

    private Socket socket;
    int currentUserId;
    int receiverId = 12;
    private String room;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("user_data", null);
        if (json != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(json, User.class);
            currentUserId = user.getUser_id();
        }

        recyclerView = view.findViewById(R.id.recyclerChat);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);

        adapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        room = generateRoomId(currentUserId, receiverId);
        initSocket();
        fetchMessagesFromApi();

        btnSend.setOnClickListener(v -> {
            String text = edtMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                Message msg = new Message();
                msg.setSender_id(currentUserId);
                msg.setRoom(room);
                msg.setMessage(text);

                ApiService api = ApiClient.getClient().create(ApiService.class);
                api.sendMessage(msg).enqueue(new Callback<ApiResponse<Message>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Message>> call, @NonNull Response<ApiResponse<Message>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            socket.emit("sendMessage", response.body().getData());
                            messageList.add(response.body().getData());
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                            edtMessage.setText("");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Message>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        return view;
    }

    private String generateRoomId(int user1, int user2) {
        return (user1 < user2) ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    private void initSocket() {
        try {
            socket = IO.socket("http://10.0.2.2:3001");
            socket.connect();
            socket.emit("joinRoom", room);
            socket.on("newMessage", onNewMessage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onNewMessage = args -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                Gson gson = new Gson();
                Message newMsg = gson.fromJson(data.toString(), Message.class);
                Log.d("SocketMessageRaw", data.toString());

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socket.disconnect();
        socket.off("newMessage", onNewMessage);
    }
}
