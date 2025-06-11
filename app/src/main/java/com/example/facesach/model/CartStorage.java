package com.example.facesach.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartStorage {
    private static final String PREF_NAME = "cart_prefs";
    private static final String KEY_CART = "cart_items";

    public static void saveCart(Context context, List<CartItem> cartItems) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(cartItems);
        editor.putString(KEY_CART, json);
        editor.apply();
    }

    public static List<CartItem> loadCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CART, null);
        if (json != null) {
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void clearCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_CART).apply();
    }
}
