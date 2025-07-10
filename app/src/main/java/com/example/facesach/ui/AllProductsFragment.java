package com.example.facesach.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.facesach.R;

public class AllProductsFragment extends Fragment {

    public AllProductsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_products, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackProducts);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}
