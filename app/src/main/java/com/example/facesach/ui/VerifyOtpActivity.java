package com.example.facesach.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText[] otpFields;
    private Button btnVerifyOtp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        email = getIntent().getStringExtra("email");

        otpFields = new EditText[]{
                findViewById(R.id.otp_1),
                findViewById(R.id.otp_2),
                findViewById(R.id.otp_3),
                findViewById(R.id.otp_4),
                findViewById(R.id.otp_5),
                findViewById(R.id.otp_6)
        };

        btnVerifyOtp = findViewById(R.id.btn_verify_otp);

        setupOtpInputs();

        btnVerifyOtp.setOnClickListener(v -> {
            StringBuilder otpBuilder = new StringBuilder();
            for (EditText et : otpFields) {
                String digit = et.getText().toString().trim();
                if (digit.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                otpBuilder.append(digit);
            }

            verifyOtp(email, otpBuilder.toString());
        });
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.toString().isEmpty() && index > 0 && before > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }

                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void verifyOtp(String email, String otp) {
        ApiClient.getClient().create(ApiService.class).verifyOtp(email, otp).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
