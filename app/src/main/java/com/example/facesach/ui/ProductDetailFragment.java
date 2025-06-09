package com.example.facesach.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.Product;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT = "arg_product";

    private Product product;

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductDescription, tvQuantity;
    private Button btnIncrease, btnDecrease, btnAddToCart;

    private int quantity = 1;

    public ProductDetailFragment() {
    }

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductPrice = view.findViewById(R.id.tvProductPrice);
        tvProductDescription = view.findViewById(R.id.tvProductDescription);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);

        if (product != null) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(String.format("%,d VND", product.getPrice()));
            tvProductDescription.setText(product.getDescription() != null ? product.getDescription() : "");
            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(ivProductImage);
        }

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
