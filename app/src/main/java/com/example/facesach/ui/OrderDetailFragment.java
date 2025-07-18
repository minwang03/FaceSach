package com.example.facesach.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.OrderItem;
import com.example.facesach.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderItemAdapter adapter;
    private int orderId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        recyclerView = view.findViewById(R.id.recyclerOrderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null && args.containsKey("orderId")) {
            orderId = args.getInt("orderId");
            loadOrderDetails(orderId);
        } else {
            Toast.makeText(getContext(), "Không có ID đơn hàng", Toast.LENGTH_SHORT).show();
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void loadOrderDetails(int orderId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getOrderDetails(orderId).enqueue(new Callback<ApiResponse<List<OrderItem>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<OrderItem>>> call, @NonNull Response<ApiResponse<List<OrderItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<OrderItem> orderItems = response.body().getData();
                    adapter.updateItems(orderItems);
                } else {
                    Toast.makeText(getContext(), "Không có dữ liệu chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<OrderItem>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
