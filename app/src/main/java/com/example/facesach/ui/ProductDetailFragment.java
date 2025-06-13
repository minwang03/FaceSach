package com.example.facesach.ui;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.CartItem;
import com.example.facesach.model.CartStorage;
import com.example.facesach.model.Product;

import java.util.List;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT = "arg_product";

    private Product product;

    ImageView ivProductImage;
    TextView tvProductName, tvProductPrice, tvProductDescription, tvQuantity;
    Button btnIncrease, btnDecrease, btnAddToCart, btnBack;

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

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductPrice = view.findViewById(R.id.tvProductPrice);
        tvProductDescription = view.findViewById(R.id.tvProductDescription);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);

        btnBack = view.findViewById(R.id.btnBack);

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
            List<CartItem> currentCart = CartStorage.loadCart(requireContext());
            boolean found = false;
            for (CartItem item : currentCart) {
                if (item.getProduct().getProductId() == product.getProductId()) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                currentCart.add(new CartItem(product, quantity));
            }

            CartStorage.saveCart(requireContext(), currentCart);

            StringBuilder cartSummary = new StringBuilder("Giỏ hàng:\n");
            for (CartItem item : currentCart) {
                cartSummary.append("- ")
                        .append(item.getProduct().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" Tổng tiền: ")
                        .append(item.getTotalPrice())
                        .append("\n");
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Giỏ hàng hiện tại")
                    .setMessage(cartSummary.toString())
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Xóa giỏ hàng", (dialog, which) -> {
                        CartStorage.clearCart(requireContext());
                        Toast.makeText(getContext(), "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                    })
                    .show();

        });


        btnBack.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return view;
    }
}
