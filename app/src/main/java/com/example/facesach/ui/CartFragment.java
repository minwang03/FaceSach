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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.CartItem;
import com.example.facesach.model.CartStorage;
import com.example.facesach.model.OrderData;
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
        OrderRequest orderRequest = prepareOrderRequest(user);
        Log.d("OrderRequest_JSON", gson.toJson(orderRequest));

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createOrder(orderRequest).enqueue(new Callback<ApiResponse<OrderData>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<OrderData>> call, @NonNull Response<ApiResponse<OrderData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    OrderData orderData = response.body().getData();

                    NavController navController = Navigation.findNavController(requireView());
                    Bundle bundle = new Bundle();
                    bundle.putInt("orderId", orderData.getOrderId());
                    bundle.putString("clientSecret", orderData.getClientSecret());
                    bundle.putLong("amount", orderData.getAmount());

                    navController.navigate(R.id.action_cartFragment_to_paymentFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Thêm sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<OrderData>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
