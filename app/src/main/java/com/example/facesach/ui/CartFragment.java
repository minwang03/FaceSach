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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.CartItem;
import com.example.facesach.model.CartStorage;
import com.example.facesach.model.OrderRequest;
import com.example.facesach.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Button btnBackCart, btnPurchase;
    private TextView tvTotalPrice;
    private List<CartItem> cartItems;
    private final Gson gson = new Gson();

    public CartFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        btnBackCart = view.findViewById(R.id.btnBackCart);
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnPurchase = view.findViewById(R.id.btnPurchase);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartItems = CartStorage.loadCart(requireContext());
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        updateTotalPrice();

        btnBackCart.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        btnPurchase.setOnClickListener(v -> handlePurchase());

        return view;
    }

    private void updateTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        tvTotalPrice.setText(String.format("Tổng tiền: %,d VND", total));
    }

    private OrderRequest prepareOrderRequest(User user) {
        List<OrderRequest.CartItemRequest> cartItemRequests = new ArrayList<>();
        for (CartItem item : cartItems) {
            cartItemRequests.add(new OrderRequest.CartItemRequest(
                    item.getProduct().getProductId(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
            ));
        }
        return new OrderRequest(user.getUser_id(), cartItemRequests);
    }

    private void handlePurchase() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("user_data", null);

        if (json == null) {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = gson.fromJson(json, User.class);
        if (user == null || user.getUser_id() == 0) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderRequest orderRequest = prepareOrderRequest(user);
        Log.d("OrderRequest_JSON", gson.toJson(orderRequest));

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createOrder(orderRequest).enqueue(new Callback<ApiResponse<Void>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    CartStorage.clearCart(requireContext());
                    cartItems.clear();
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                } else {
                    Log.e("OrderError", "Lỗi response: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("OrderError", "Chi tiết lỗi: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.e("OrderError", "Lỗi kết nối server", t);
                Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
