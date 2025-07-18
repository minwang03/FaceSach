package com.example.facesach.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facesach.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        View btnAllProducts = view.findViewById(R.id.btnAllProducts);
        View btnAllUsers = view.findViewById(R.id.btnAllUsers);
        View btnUserOrders = view.findViewById(R.id.btnUserOrders);

        btnAllProducts.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_allProductsFragment));
        btnAllUsers.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_allUsersFragment));
        btnUserOrders.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_userOrdersFragment));
        return view;
    }

}
