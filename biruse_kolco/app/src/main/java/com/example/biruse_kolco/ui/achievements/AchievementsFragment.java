package com.example.biruse_kolco.ui.achievements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.model.MainViewModel;

public class AchievementsFragment extends Fragment {

    private MainViewModel viewModel;
    private RecyclerView rvAchievements;
    private AchievementAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAchievements = view.findViewById(R.id.rv_achievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AchievementAdapter();
        rvAchievements.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getAchievements().observe(getViewLifecycleOwner(), achievements -> {
            if (achievements != null) {
                adapter.setAchievements(achievements);
            }
        });
    }
}