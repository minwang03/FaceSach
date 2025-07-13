package com.example.facesach.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.Category;
import com.example.facesach.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT = "product";
    private Product product;
    private Integer selectedCategoryId;

    public static EditProductDialogFragment newInstance(Product product) {
        EditProductDialogFragment fragment = new EditProductDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
        }

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_product, null);

        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);
        EditText edtDesc = view.findViewById(R.id.edtProductDescription);
        EditText edtStock = view.findViewById(R.id.edtProductStock);
        EditText edtImage = view.findViewById(R.id.edtProductImage);
        ImageView imgPreview = view.findViewById(R.id.imgPreview);
        AutoCompleteTextView dropdownCategory = view.findViewById(R.id.dropdownCategory);
        Button btnSave = view.findViewById(R.id.btnSaveProduct);

        setupCategoryDropdown(dropdownCategory);

        if (product != null) {
            edtName.setText(product.getName());
            edtPrice.setText(String.valueOf(product.getPrice()));
            edtDesc.setText(product.getDescription());
            edtStock.setText(String.valueOf(product.getStockQuantity()));
            edtImage.setText(product.getImage());

            Glide.with(requireContext())
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(imgPreview);
        }

        edtImage.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String url = edtImage.getText().toString().trim();
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.ic_avatar_placeholder)
                            .error(R.drawable.ic_avatar_placeholder)
                            .into(imgPreview);
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String description = edtDesc.getText().toString().trim();
            String stockStr = edtStock.getText().toString().trim();
            String image = edtImage.getText().toString().trim();

            // 👇 log input
            Log.d("UPDATE", "User input -> name: " + name + ", price: " + priceStr + ", stock: " + stockStr + ", image: " + image);

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(getContext(), "Tên và giá không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(stockStr)) {
                stockStr = "0"; // fallback nếu để trống thì lấy 0
            }

            try {
                int price = Integer.parseInt(priceStr);
                int stock = Integer.parseInt(stockStr);

                // Fallback category nếu chưa chọn
                if (selectedCategoryId == null && product.getCategoryId() != null) {
                    selectedCategoryId = product.getCategoryId();
                }

                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setStockQuantity(stock);
                product.setImage(image);
                product.setCategoryId(selectedCategoryId);

                Log.d("UPDATE", "Updating product: " + product.getProductId() + ", category_id: " + selectedCategoryId);

                updateProduct(product);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá và tồn kho phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dropdownCategory.setDropDownHeight(600);
        dropdownCategory.setDropDownVerticalOffset(10);
        dropdownCategory.setOnClickListener(v -> dropdownCategory.showDropDown());

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle("Chỉnh sửa sản phẩm")
                .create();
    }

    private void updateProduct(Product product) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateProduct(product.getProductId(), product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Product>> call, @NonNull Response<ApiResponse<Product>> response) {
                Log.d("UPDATE", "API response code: " + response.code());
                if (response.errorBody() != null) {
                    try {
                        Log.e("UPDATE", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thất bại: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật (status): " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Product>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoryDropdown(AutoCompleteTextView dropdownCategory) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call,
                                   @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Category> categories = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    Map<String, Integer> nameToIdMap = new HashMap<>();

                    for (Category category : categories) {
                        categoryNames.add(category.getName());
                        nameToIdMap.put(category.getName(), category.getCategoryId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            categoryNames
                    );
                    dropdownCategory.setAdapter(adapter);

                    dropdownCategory.setOnItemClickListener((parent, view1, position, id) -> {
                        String selected = parent.getItemAtPosition(position).toString();
                        selectedCategoryId = nameToIdMap.get(selected);
                    });

                    if (product != null && product.getCategoryId() != null) {
                        for (Category c : categories) {
                            if (c.getCategoryId() == product.getCategoryId()) {
                                dropdownCategory.setText(c.getName(), false);
                                selectedCategoryId = c.getCategoryId();
                                break;
                            }
                        }

                        if (selectedCategoryId == null) {
                            selectedCategoryId = product.getCategoryId();
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải danh mục: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
