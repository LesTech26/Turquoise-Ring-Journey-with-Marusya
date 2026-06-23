package com.example.biruse_kolco.ui.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biruse_kolco.R;

// TODO: Для Разработчика 2 - реализовать полный экран района
public class DistrictDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_district_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tv_district_title);
        tvTitle.setText("Экран района");

        // TODO: Для Разработчика 2:
        // - Добавить историю района
        // - Добавить временную ленту
        // - Добавить описание костюма
        // - Добавить фотографии
        // - Добавить кнопку перехода к игре
    }
}