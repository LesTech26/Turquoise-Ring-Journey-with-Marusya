package com.example.biruse_kolco.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.model.MainViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    private MainViewModel viewModel;
    private MaterialButton btnResetProgress;

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

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

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
}