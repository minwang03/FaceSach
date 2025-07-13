package com.example.facesach.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.Category;
import com.example.facesach.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private LinearLayout categoryContainer;
    private LinearLayout productContainer;
    private List<Product> allProducts;
    ImageView ivCart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        categoryContainer = view.findViewById(R.id.categoryContainer);
        productContainer = view.findViewById(R.id.productContainer);
        ivCart = view.findViewById(R.id.ivCart);
        loadCategories();
        loadProducts();

        ivCart.setOnClickListener(v -> {
            NavController navController = androidx.navigation.Navigation.findNavController(v);
            navController.navigate(R.id.action_homeFragment_to_cartFragment);
        });

        return view;
    }

    private void loadCategories() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showCategories(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("HomeFragment", "Context is null while showing toast");
                }
            }
        });
    }

    private void showCategories(List<Category> categories) {
        Context context = getContext();
        if (context == null) {
            Log.e("HomeFragment", "Context null in showCategories");
            return;
        }

        for (Category category : categories) {
            Button btn = new Button(context);
            btn.setText(category.getName());
            btn.setAllCaps(false);

            Log.d("HomeFragment", "Category ID: " + category.getCategoryId() + ", Name: " + category.getName());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(16);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                filterProductsByCategory(category.getCategoryId());
            });

            categoryContainer.addView(btn);
        }
    }

    private void loadProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allProducts = response.body().getData();
                    showProducts(allProducts);
                } else {
                    Toast.makeText(getContext(), "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("HomeFragment", "Context is null while showing toast");
                }
            }
        });
    }

    private void filterProductsByCategory(int categoryId) {
        if (allProducts == null) return;

        List<Product> filtered = new java.util.ArrayList<>();
        for (Product p : allProducts) {
            if (p.getCategoryId() == categoryId) {
                filtered.add(p);
            }
        }
        showProducts(filtered);
    }

    private void showProducts(List<Product> products) {
        productContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (Product product : products) {
            View productView = inflater.inflate(R.layout.item_product, productContainer, false);

            TextView tvName = productView.findViewById(R.id.tvProductName);
            TextView tvPrice = productView.findViewById(R.id.tvProductPrice);
            ImageView ivImage = productView.findViewById(R.id.ivProductImage);

            tvName.setText(product.getName());
            tvPrice.setText(String.format("%,d VND", product.getPrice()));

            Glide.with(this)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(ivImage);

            productView.setOnClickListener(v -> {
                NavController navController = androidx.navigation.Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putSerializable("arg_product", product);
                navController.navigate(R.id.action_homeFragment_to_productDetailFragment, bundle);
            });

            productContainer.addView(productView);
        }
    }
}

