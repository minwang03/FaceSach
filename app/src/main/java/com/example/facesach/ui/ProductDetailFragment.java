package com.example.facesach.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.api.ApiClient;
import com.example.facesach.api.ApiService;
import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.CartItem;
import com.example.facesach.model.CartStorage;
import com.example.facesach.model.Comment;
import com.example.facesach.model.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_PRODUCT = "arg_product";

    private Product product;

    ImageView ivProductImage;
    TextView tvProductName, tvProductPrice, tvProductDescription, tvQuantity;
    Button btnIncrease, btnDecrease, btnAddToCart, btnBack, btnSubmitComment;
    EditText etComment;
    RecyclerView rvComments;
    CommentAdapter commentAdapter;
    List<Comment> commentList = new ArrayList<>();


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
        etComment = view.findViewById(R.id.etComment);
        btnSubmitComment = view.findViewById(R.id.btnSubmitComment);
        rvComments = view.findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setAdapter(commentAdapter);

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

        loadComments();

        btnSubmitComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                return;
            }

            submitComment(commentText);
        });

        return view;
    }

    private void loadComments() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getCommentsByProduct(product.getProductId()).enqueue(new Callback<ApiResponse<List<Comment>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Comment>>> call, @NonNull Response<ApiResponse<List<Comment>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentList.clear();
                    commentList.addAll(response.body().getData());
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Comment>>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi tải bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitComment(String content) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("user_data", null);
        if (json == null) {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId;
        try {
            JSONObject userObj = new JSONObject(json);
            userId = userObj.getInt("user_id");
        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Dữ liệu người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment();
        comment.setUser_id(userId);
        comment.setProduct_id(product.getProductId());
        comment.setRating(5);
        comment.setComment(content);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.addComment(comment).enqueue(new Callback<ApiResponse<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Comment>> call, @NonNull Response<ApiResponse<Comment>> response) {
                if (response.isSuccessful()) {
                    etComment.setText("");
                    loadComments();
                    Toast.makeText(requireContext(), "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Comment>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
