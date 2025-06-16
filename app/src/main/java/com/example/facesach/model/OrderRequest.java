package com.example.facesach.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("cartItems")
    private List<CartItemRequest> cartItems;

    public OrderRequest(int userId, List<CartItemRequest> cartItems) {
        this.userId = userId;
        this.cartItems = cartItems;
    }

    public static class CartItemRequest {
        @SerializedName("product_id")
        private int productId;

        private int quantity;

        @SerializedName("unit_price")
        private double unitPrice;

        public CartItemRequest(int productId, int quantity, double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}


