package com.example.facesach.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;

    public AllProductsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_products, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackProducts);
        recyclerView = view.findViewById(R.id.recyclerProducts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new ProductAdapter(productList, this::deleteProduct);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Response<ApiResponse<List<Product>>> response) {

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    productList.clear();
                    productList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải sản phẩm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct(int productId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.deleteProduct(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    fetchProducts();
                } else {
                    Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi xóa: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
