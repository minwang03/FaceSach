package com.example.facesach.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getProduct().getName());
        holder.tvQuantity.setText("Số lượng: " + item.getQuantity());
        holder.tvTotal.setText("Tổng: " + String.format("%,d VND", item.getTotalPrice()));
        Glide.with(holder.itemView.getContext())
                .load(item.getProduct().getImage())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .into(holder.ivProduct);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvTotal;
        ImageView ivProduct;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            tvTotal = itemView.findViewById(R.id.tvCartItemTotal);
            ivProduct = itemView.findViewById(R.id.ivCartItemImage);
        }
    }
}
