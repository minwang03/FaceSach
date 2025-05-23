package com.example.facesach.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.facesach.R;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Bắt buộc phải có constructor trống
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button editBtn = view.findViewById(R.id.btn_edit_profile);
        Button logoutBtn = view.findViewById(R.id.btn_logout);

        editBtn.setOnClickListener(v -> {
            // Mở màn hình chỉnh sửa thông tin
        });

        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa back stack
            startActivity(intent);
        });

        return view;
    }

}
