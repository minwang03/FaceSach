package com.example.facesach.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
    private static final int PICK_IMAGE_REQUEST = 1;

    private Product product;
    private Integer selectedCategoryId;
    private Uri selectedImageUri;

    private ImageView imgPreview;

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
        if (product == null) {
            product = new Product();
        }

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_product, null);

        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);
        EditText edtDesc = view.findViewById(R.id.edtProductDescription);
        EditText edtStock = view.findViewById(R.id.edtProductStock);
        imgPreview = view.findViewById(R.id.imgPreview);
        AutoCompleteTextView dropdownCategory = view.findViewById(R.id.dropdownCategory);
        Button btnSave = view.findViewById(R.id.btnSaveProduct);
        Button btnPickImage = view.findViewById(R.id.btnPickImage);

        setupCategoryDropdown(dropdownCategory);

        if (product.getProductId() != 0) {
            edtName.setText(product.getName());
            edtPrice.setText(String.valueOf(product.getPrice()));
            edtDesc.setText(product.getDescription());
            edtStock.setText(String.valueOf(product.getStockQuantity()));

            if (product.getImage() != null) {
                Glide.with(requireContext())
                        .load(Uri.parse(product.getImage()))
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(imgPreview);
            }
        }

        btnPickImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String description = edtDesc.getText().toString().trim();
            String stockStr = edtStock.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(getContext(), "Tên và giá không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(stockStr)) {
                stockStr = "0";
            }

            try {
                int price = Integer.parseInt(priceStr);
                int stock = Integer.parseInt(stockStr);

                if (selectedCategoryId == null && product.getCategoryId() != null) {
                    selectedCategoryId = product.getCategoryId();
                }

                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setStockQuantity(stock);
                product.setCategoryId(selectedCategoryId);

                if (selectedImageUri != null) {
                    product.setImage(selectedImageUri.toString());
                }

                if (product.getProductId() == 0) {
                    createProduct(product);
                } else {
                    updateProduct(product);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá và tồn kho phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dropdownCategory.setDropDownHeight(600);
        dropdownCategory.setDropDownVerticalOffset(10);
        dropdownCategory.setOnClickListener(v -> dropdownCategory.showDropDown());

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(product.getProductId() == 0 ? "Thêm sản phẩm" : "Chỉnh sửa sản phẩm")
                .create();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(requireContext())
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .into(imgPreview);
        }
    }

    private void updateProduct(Product product) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateProduct(product.getProductId(), product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Product>> call, @NonNull Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().setFragmentResult("product_updated", new Bundle());
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Product>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createProduct(Product product) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createProduct(product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Product>> call, @NonNull Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().setFragmentResult("product_updated", new Bundle());
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
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
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Category> categories = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    Map<String, Integer> nameToIdMap = new HashMap<>();

                    for (Category category : categories) {
                        categoryNames.add(category.getName());
                        nameToIdMap.put(category.getName(), category.getCategoryId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
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
