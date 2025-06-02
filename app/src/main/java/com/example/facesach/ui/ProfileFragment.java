package com.example.facesach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.User;
import com.google.gson.Gson;

import android.content.SharedPreferences;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private ImageView profileImage;
    private Button editBtn, changePasswordBtn, logoutBtn;

    public ProfileFragment() {
        // Constructor trống
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.profile_name);
        tvEmail = view.findViewById(R.id.profile_email);
        profileImage = view.findViewById(R.id.profile_image);
        editBtn = view.findViewById(R.id.btn_edit_profile);
        changePasswordBtn = view.findViewById(R.id.btn_change_password);
        logoutBtn = view.findViewById(R.id.btn_logout);

        loadUserProfile();

        editBtn.setOnClickListener(v -> {
            // Mở màn hình chỉnh sửa thông tin (nếu có)
        });

        changePasswordBtn.setOnClickListener(v -> {
            // Xử lý đổi mật khẩu (nếu có)
        });

        logoutBtn.setOnClickListener(v -> {
            clearUserData();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("user_data", null);

        if (json != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(json, User.class);

            if (user != null) {
                tvName.setText(user.getName() != null ? user.getName() : "Chưa cập nhật");
                tvEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa cập nhật");

                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    Glide.with(this)
                            .load(user.getAvatar())
                            .placeholder(R.drawable.ic_avatar_placeholder)
                            .error(R.drawable.ic_avatar_placeholder)
                            .circleCrop()
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_avatar_placeholder);
                }

                editBtn.setVisibility(View.VISIBLE);
                changePasswordBtn.setVisibility(View.VISIBLE);
                logoutBtn.setText(getString(R.string.logout));
                logoutBtn.setOnClickListener(v -> {
                    clearUserData();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }
        } else {
            tvName.setText(getString(R.string.guest_name));
            tvEmail.setVisibility(View.GONE);
            profileImage.setImageResource(R.drawable.ic_avatar_placeholder);
            editBtn.setVisibility(View.GONE);
            changePasswordBtn.setVisibility(View.GONE);
            logoutBtn.setText(getString(R.string.login));
            logoutBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_login));
            logoutBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }
    }

    private void clearUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user_data");
        editor.apply();
    }
}
