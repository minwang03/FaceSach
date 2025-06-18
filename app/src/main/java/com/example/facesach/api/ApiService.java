package com.example.facesach.api;

import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.Category;
import com.example.facesach.model.OrderData;
import com.example.facesach.model.OrderRequest;
import com.example.facesach.model.Product;
import com.example.facesach.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("users")
    Call<ApiResponse<List<User>>> getAllUsers();

    @POST("users/login")
    Call<ApiResponse<User>> login(@Body User userRequest);

    @GET("category")
    Call<ApiResponse<List<Category>>> getAllCategories();

    @GET("products")
    Call<ApiResponse<List<Product>>> getAllProducts();

    @GET("products/search")
    Call<ApiResponse<List<Product>>> searchProductsByName(@Query("name") String name);

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") int productId);

    @POST("orders")
    Call<ApiResponse<OrderData>> createOrder(@Body OrderRequest orderRequest);

}

