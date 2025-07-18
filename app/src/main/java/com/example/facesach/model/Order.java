package com.example.facesach.model;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Order {
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("total_price")
    private double totalPrice;

    private String status;

    @SerializedName("order_date")
    private Date orderDate;

    // Getters & Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
}
