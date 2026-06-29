package com.example.biruse_kolco.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.admin_panel.AdminActivity;
import com.example.biruse_kolco.model.MainViewModel;
import com.example.biruse_kolco.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    private MainViewModel viewModel;
    private Button btnResetProgress;
    private int adminTapCount = 0;
    private long lastAdminTapTime = 0L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnResetProgress = view.findViewById(R.id.btn_reset_progress);
        TextView tvAppName = view.findViewById(R.id.tv_app_name);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        tvAppName.setOnClickListener(v -> registerAdminTap());

        btnResetProgress.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Сброс прогресса")
                    .setMessage(R.string.settings_reset_confirm)
                    .setPositiveButton("Сбросить", (dialog, which) -> {
                        // 1. Сбрасываем Room базу данных (через ViewModel)
                        viewModel.resetProgress();

                        // 2. Сбрасываем SharedPreferences для ProgressStore (trjwm модуль)
                        SharedPreferences prefs = requireContext().getSharedPreferences("district_progress", Context.MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        // 3. Показываем уведомление
                        Snackbar.make(v, "✅ Прогресс успешно сброшен!", Snackbar.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void registerAdminTap() {
        long now = System.currentTimeMillis();
        if (now - lastAdminTapTime > Constants.ADMIN_TAP_RESET_MS) {
            adminTapCount = 0;
        }
        lastAdminTapTime = now;
        adminTapCount++;

        if (adminTapCount >= Constants.ADMIN_SECRET_TAPS) {
            adminTapCount = 0;
            startActivity(new Intent(requireContext(), AdminActivity.class));
        }
    }
}
