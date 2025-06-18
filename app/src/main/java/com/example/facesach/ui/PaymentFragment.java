package com.example.facesach.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.facesach.R;

public class PaymentFragment extends Fragment {

    private int orderId;
    private String clientSecret;
    private long amount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getInt("orderId");
            clientSecret = bundle.getString("clientSecret");
            amount = bundle.getLong("amount");

            Log.d("PaymentFragment", "orderId = " + orderId);
            Log.d("PaymentFragment", "clientSecret = " + clientSecret);
            Log.d("PaymentFragment", "amount = " + amount);
        }

        return view;
    }
}
