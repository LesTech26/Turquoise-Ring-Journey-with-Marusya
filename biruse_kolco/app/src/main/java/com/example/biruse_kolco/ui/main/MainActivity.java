package com.example.biruse_kolco.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.biruse_kolco.R; // ИСПРАВЛЕНО
import com.example.biruse_kolco.admin_panel.AdminActivity;
import com.example.biruse_kolco.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private int adminTapCount = 0;
    private long lastAdminTapTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (view, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(bottomNav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.settingsFragment) {
                registerSettingsTap();
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        bottomNav.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.settingsFragment) {
                registerSettingsTap();
            }
        });
    }

    private void registerSettingsTap() {
        long now = System.currentTimeMillis();
        if (now - lastAdminTapTime > Constants.ADMIN_TAP_RESET_MS) {
            adminTapCount = 0;
        }
        lastAdminTapTime = now;
        adminTapCount++;

        if (adminTapCount >= Constants.ADMIN_SECRET_TAPS) {
            adminTapCount = 0;
            startActivity(new Intent(this, AdminActivity.class));
            return;
        }

        int tapsLeft = Constants.ADMIN_SECRET_TAPS - adminTapCount;
        if (tapsLeft <= Constants.ADMIN_ACCESS_HINT_THRESHOLD) {
            Toast.makeText(this,
                    "До входа в админ-панель: " + tapsLeft,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }
}