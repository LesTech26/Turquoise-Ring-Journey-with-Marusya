package com.example.biruse_kolco.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trjwm.R;
import com.example.biruse_kolco.model.MainViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private MainViewModel viewModel;
    private SwitchMaterial switchSound;
    private Spinner spinnerLanguage;
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

        switchSound = view.findViewById(R.id.switch_sound);
        spinnerLanguage = view.findViewById(R.id.spinner_language);
        btnResetProgress = view.findViewById(R.id.btn_reset_progress);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                switchSound.setChecked(user.isSoundEnabled());
            }
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.toggleSound(isChecked);
        });

        String[] languages = {"Русский", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // TODO: Для Разработчика 2 - реализовать смену языка

        btnResetProgress.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Сброс прогресса")
                    .setMessage(R.string.settings_reset_confirm)
                    .setPositiveButton("Сбросить", (dialog, which) -> {
                        viewModel.resetProgress();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        // TODO: Для Разработчика 5 - скрытый вход в админ-панель
    }
}
