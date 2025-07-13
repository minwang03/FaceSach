package com.example.facesach.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> productList;
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDelete(int productId);
        void onEdit(Product product);
    }

    public ProductAdapter(List<Product> productList, OnItemActionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.txtName.setText(product.getName());
        holder.txtPrice.setText("Giá: " + product.getPrice() + " đ");

        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .into(holder.ivImage);

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(product.getProductId()));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice;
        Button btnDelete, btnEdit;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tvProductName);
            txtPrice = itemView.findViewById(R.id.tvProductPrice);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            ivImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
