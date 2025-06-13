package com.example.facesach.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.facesach.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationBar();
    }

    private void setupNavigationBar() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            if (destId == R.id.homeFragment || destId == R.id.searchFragment || destId == R.id.profileFragment || destId == R.id.settingsFragment) {
                bottomNavigationView.setVisibility(BottomNavigationView.VISIBLE);
                bottomNavigationView.getMenu().findItem(destId).setChecked(true);
            }
            else if (destId == R.id.cartFragment) {
                bottomNavigationView.setVisibility(BottomNavigationView.VISIBLE);
                bottomNavigationView.getMenu().findItem(R.id.homeFragment).setChecked(true);
            }
            else if (destId == R.id.productDetailFragment) {
                bottomNavigationView.setVisibility(BottomNavigationView.VISIBLE);
                bottomNavigationView.getMenu().findItem(R.id.homeFragment).setChecked(true);
            }
            else {
                bottomNavigationView.setVisibility(BottomNavigationView.GONE);
            }
        });
    }

}
