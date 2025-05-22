package com.example.facesach;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationBar();
    }

    private void setupNavigationBar() {
        // Tìm "NavHostFragment" trong layout. Đây là khung dùng để chứa các màn hình (fragments).
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // Nếu không tìm thấy thì thoát luôn (tránh lỗi app bị crash).
        if (navHostFragment == null) return;

        // Lấy NavController – cái này giúp điều hướng giữa các màn hình (fragments).
        NavController navController = navHostFragment.getNavController();

        // Tìm thanh điều hướng dưới (BottomNavigationView) từ layout
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Gắn BottomNavigationView với NavController
        // => để khi bấm vào icon thì nó tự chuyển sang màn hình tương ứng
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}
