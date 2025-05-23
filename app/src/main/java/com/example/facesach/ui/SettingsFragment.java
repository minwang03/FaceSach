package com.example.facesach.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesach.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Bắt buộc phải có constructor trống
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
