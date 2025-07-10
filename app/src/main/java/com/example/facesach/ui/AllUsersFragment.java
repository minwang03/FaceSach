package com.example.facesach.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facesach.R;
import com.example.facesach.ui.UserAdapter;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    List<User> userList = new ArrayList<>();
    private ApiService apiService;

    public AllUsersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackUsers);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchAllUsers();
        setupSwipeToDelete();

        return view;
    }

    private void fetchAllUsers() {
        apiService.getAllUsers().enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<User>>> call, @NonNull Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body().getData());
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<User>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải danh sách người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();

                if (position == RecyclerView.NO_POSITION) return;

                User user = userList.get(position);

                apiService.deleteUser(user.getUser_id()).enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) {
                            userList.remove(position);
                            userAdapter.notifyItemRemoved(position);
                            Toast.makeText(getContext(), "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            userAdapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                        userAdapter.notifyItemChanged(position);
                        Toast.makeText(getContext(), "Lỗi mạng khi xóa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
