package com.example.facesach.api;

import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.Category;
import com.example.facesach.model.Message;
import com.example.facesach.model.Order;
import com.example.facesach.model.OrderData;
import com.example.facesach.model.OrderItem;
import com.example.facesach.model.OrderRequest;
import com.example.facesach.model.Product;
import com.example.facesach.model.StatusUpdateRequest;
import com.example.facesach.model.User;
import com.example.facesach.model.Comment;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") int productId);

    @POST("orders")
    Call<ApiResponse<OrderData>> createOrder(@Body OrderRequest orderRequest);

    @PUT("orders/{orderId}/status")
    Call<ApiResponse<Void>> updateOrderStatus(@Path("orderId") int orderId, @Body StatusUpdateRequest statusUpdateRequest);

    @POST("users/login-google")
    Call<ApiResponse<User>> loginWithGoogle(@Body User user);

    @POST("comments")
    Call<ApiResponse<Comment>> addComment(@Body Comment comment);

    @GET("comments/product/{product_id}")
    Call<ApiResponse<List<Comment>>> getCommentsByProduct(@Path("product_id") int productId);

    @DELETE("comments/{comment_id}")
    Call<ApiResponse<Void>> deleteComment(@Path("comment_id") int commentId);

    @GET("messages/private/{user1}/{user2}")
    Call<ApiResponse<List<Message>>> getPrivateMessages(@Path("user1") int user1, @Path("user2") int user2);

    @POST("messages")
    Call<ApiResponse<Message>> sendMessage(@Body Message message);

    @DELETE("users/{id}")
    Call<ApiResponse<Void>> deleteUser(@Path("id") int userId);

    @DELETE("products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") int productId);

    @PUT("products/{id}")
    Call<ApiResponse<Product>> updateProduct(@Path("id") int productId, @Body Product product);

    @GET("products/search")
    Call<ApiResponse<List<Product>>> searchProducts(@Query("q") String keyword);

    @GET("orders/users/{userId}")
    Call<ApiResponse<List<Order>>> getOrdersByUserId(@Path("userId") int userId);

    @GET("orders/detail/{orderId}")
    Call<ApiResponse<List<OrderItem>>> getOrderDetails(@Path("orderId") int orderId);

}

