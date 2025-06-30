package com.example.facesach.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.CartStorage;
import com.example.facesach.model.StatusUpdateRequest;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    private String clientSecret;
    private int orderId;
    private PaymentSheet paymentSheet;
    Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            clientSecret = bundle.getString("clientSecret");
        };

        // Khởi tạo Stripe SDK
        PaymentConfiguration.init(
                requireContext(),
                "pk_test_51RbA1MQZLl2Hl0hwQGOhh1n5NDdsNF629DxOsUH0fzSff5dP4Y0gtLbaAdhmC4AO0xXizVhPrhggHr5lMLiKWdkC00seTmqwDz"
        );

        // Khởi tạo PaymentSheet
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        // Nút thanh toán
        Button btnPay = view.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(v -> presentPaymentSheet());

        // Nút hủy và quay về trang card
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.cartFragment);
        });

        return view;
    }

    private void presentPaymentSheet() {
        if (clientSecret == null) {
            Log.e("PaymentFragment", "Client secret is null");
            return;
        }

        paymentSheet.presentWithPaymentIntent(
                clientSecret,
                new PaymentSheet.Configuration("Facesach Store")
        );
    }

    private void onPaymentResult(@NonNull PaymentSheetResult result) {
        NavController navController = NavHostFragment.findNavController(this);

        if (result instanceof PaymentSheetResult.Completed) {
            Toast.makeText(requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            Bundle bundle = getArguments();
            if (bundle != null) {
                orderId = bundle.getInt("orderId");
            }
            updateOrderStatus(orderId);
            CartStorage.clearCart(requireContext());
            navController.navigate(R.id.homeFragment);

        } else if (result instanceof PaymentSheetResult.Failed) {
            Toast.makeText(requireContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.cartFragment);
        }
    }

    private void updateOrderStatus(int orderId) {
        StatusUpdateRequest request = new StatusUpdateRequest("processing");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateOrderStatus(orderId, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d("ORDER", "Đã cập nhật trạng thái thành: " + "processing");
                } else {
                    Log.e("ORDER", "Cập nhật thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.e("ORDER", "Lỗi khi cập nhật trạng thái: " + t.getMessage());
            }
        });
    }


}
