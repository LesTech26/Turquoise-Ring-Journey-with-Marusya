package com.example.biruse_kolco.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.model.MainViewModel;

import java.util.List;

public class HistoryFragment extends Fragment {

    private MainViewModel viewModel;
    private LinearLayout historyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        historyList = view.findViewById(R.id.historyList);

        // Наблюдаем за списком районов
        viewModel.getDistricts().observe(getViewLifecycleOwner(), districts -> {
            if (districts != null) {
                updateHistoryList(districts);
            }
        });
    }

    private void updateHistoryList(List<District> districts) {
        historyList.removeAllViews();

        boolean hasCompleted = false;

        for (District district : districts) {
            if (district.isCompleted()) {
                hasCompleted = true;
                // Создаём карточку для изученного района
                View cardView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_history_card, historyList, false);

                TextView tvName = cardView.findViewById(R.id.tv_history_name);
                TextView tvDate = cardView.findViewById(R.id.tv_history_date);
                TextView tvStatus = cardView.findViewById(R.id.tv_history_status);

                tvName.setText(district.getName());
                tvDate.setText("Изучен");
                tvStatus.setText("📚 Материалы доступны");

                historyList.addView(cardView);
            }
        }

        // Если нет изученных районов
        if (!hasCompleted) {
            View emptyView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_history_empty, historyList, false);
            historyList.addView(emptyView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем список при возврате
        viewModel.updateProgress();
    }
}