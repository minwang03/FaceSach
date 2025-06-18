package com.example.facesach.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.facesach.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class PaymentFragment extends Fragment {

    private String clientSecret;
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
        }

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
            navController.navigate(R.id.homeFragment);
        } else if (result instanceof PaymentSheetResult.Failed) {
            navController.navigate(R.id.cartFragment);
        }
    }
}
