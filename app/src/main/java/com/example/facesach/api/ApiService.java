package com.example.facesach.api;

import com.example.facesach.model.ApiResponse;
import com.example.facesach.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("users")
    Call<ApiResponse<List<User>>> getAllUsers();

    @POST("users/login")
    Call<ApiResponse<User>> login(@Body User userRequest);
}

