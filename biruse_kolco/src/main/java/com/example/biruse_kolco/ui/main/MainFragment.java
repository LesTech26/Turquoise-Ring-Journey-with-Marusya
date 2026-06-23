package com.example.biruse_kolco.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.data.database.entities.User;
import com.example.biruse_kolco.model.MainViewModel;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;
    private TextView tvUserName, tvProgress, tvLevel;
    private TextView tvNextDistrict, tvProgressPercent;
    private View viewProgressBar;
    private CardView cardContinue, cardMap, cardGames;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvProgress = view.findViewById(R.id.tv_progress);
        tvLevel = view.findViewById(R.id.tv_level);
        tvNextDistrict = view.findViewById(R.id.tv_next_district);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        viewProgressBar = view.findViewById(R.id.view_progress_bar);
        cardContinue = view.findViewById(R.id.card_continue);
        cardMap = view.findViewById(R.id.card_map);
        cardGames = view.findViewById(R.id.card_games);

        // Скрываем элементы для анимации
        setViewsAlphaZero();

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), this::updateUserInfo);
        viewModel.getNextDistrict().observe(getViewLifecycleOwner(), this::updateNextDistrict);
        viewModel.getCompletedCount().observe(getViewLifecycleOwner(), this::updateProgress);

        cardContinue.setOnClickListener(v -> {
            District next = viewModel.getNextDistrict().getValue();
            if (next != null) {
                viewModel.completeDistrict(next.getId());
            }
        });

        cardMap.setOnClickListener(v -> {
            // TODO: Для Разработчика 2 - открыть карту
        });

        cardGames.setOnClickListener(v -> {
            // TODO: Для Разработчика 4 - открыть игры
        });

        // Запускаем анимацию появления после небольшой задержки
        new Handler().postDelayed(this::startStaggeredAnimation, 300);
    }

    private void setViewsAlphaZero() {
        // Шапка
        View header = rootView.findViewById(R.id.layout_header);
        if (header != null) header.setAlpha(0f);
        // Карточка прогресса
        View cardProgress = rootView.findViewById(R.id.card_progress_main);
        if (cardProgress != null) cardProgress.setAlpha(0f);
        // Кнопка продолжения
        cardContinue.setAlpha(0f);
        // Быстрый доступ
        View quickAccess = rootView.findViewById(R.id.tv_quick_access);
        if (quickAccess != null) quickAccess.setAlpha(0f);
        cardMap.setAlpha(0f);
        cardGames.setAlpha(0f);
        // Маскот
        View mascotLabel = rootView.findViewById(R.id.tv_mascot_label);
        if (mascotLabel != null) mascotLabel.setAlpha(0f);
        View cardMascot = rootView.findViewById(R.id.card_mascot);
        if (cardMascot != null) cardMascot.setAlpha(0f);
    }

    private void startStaggeredAnimation() {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Шапка
        View header = rootView.findViewById(R.id.layout_header);
        ObjectAnimator headerAnim = ObjectAnimator.ofFloat(header, "alpha", 0f, 1f);
        headerAnim.setDuration(600);

        // Карточка прогресса
        View cardProgress = rootView.findViewById(R.id.card_progress_main);
        ObjectAnimator progressAnim = ObjectAnimator.ofFloat(cardProgress, "alpha", 0f, 1f);
        progressAnim.setDuration(600);
        progressAnim.setStartDelay(150);

        // Кнопка продолжения
        ObjectAnimator continueAnim = ObjectAnimator.ofFloat(cardContinue, "alpha", 0f, 1f);
        continueAnim.setDuration(600);
        continueAnim.setStartDelay(300);

        // Быстрый доступ
        View quickAccess = rootView.findViewById(R.id.tv_quick_access);
        ObjectAnimator quickAnim = ObjectAnimator.ofFloat(quickAccess, "alpha", 0f, 1f);
        quickAnim.setDuration(500);
        quickAnim.setStartDelay(450);

        // Карта
        ObjectAnimator mapAnim = ObjectAnimator.ofFloat(cardMap, "alpha", 0f, 1f);
        mapAnim.setDuration(500);
        mapAnim.setStartDelay(550);

        // Игры
        ObjectAnimator gamesAnim = ObjectAnimator.ofFloat(cardGames, "alpha", 0f, 1f);
        gamesAnim.setDuration(500);
        gamesAnim.setStartDelay(650);

        // Маскот - заголовок
        View mascotLabel = rootView.findViewById(R.id.tv_mascot_label);
        ObjectAnimator mascotLabelAnim = ObjectAnimator.ofFloat(mascotLabel, "alpha", 0f, 1f);
        mascotLabelAnim.setDuration(500);
        mascotLabelAnim.setStartDelay(750);

        // Маскот - карточка
        View cardMascot = rootView.findViewById(R.id.card_mascot);
        ObjectAnimator mascotCardAnim = ObjectAnimator.ofFloat(cardMascot, "alpha", 0f, 1f);
        mascotCardAnim.setDuration(500);
        mascotCardAnim.setStartDelay(850);

        // Собираем всё вместе
        AnimatorSet.Builder builder = animSet.play(headerAnim);
        builder.with(progressAnim);
        builder.with(continueAnim);
        builder.with(quickAnim);
        builder.with(mapAnim);
        builder.with(gamesAnim);
        builder.with(mascotLabelAnim);
        builder.with(mascotCardAnim);

        animSet.start();
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            tvUserName.setText(user.getUserName());
            tvLevel.setText("Ур. " + user.getLevel());
            tvProgress.setText(user.getCompletedDistricts() + " / " +
                    user.getTotalDistricts() + " районов");
        }
    }

    private void updateNextDistrict(District district) {
        if (district != null) {
            tvNextDistrict.setText(district.getName());
            cardContinue.setVisibility(View.VISIBLE);
        } else {
            tvNextDistrict.setText("🎉 Все районы изучены!");
            cardContinue.setVisibility(View.VISIBLE);
        }
    }

    private void updateProgress(Integer completed) {
        if (completed != null) {
            int total = 24;
            int percent = (int) ((completed * 100.0) / total);
            tvProgressPercent.setText(percent + "% пройдено");

            viewProgressBar.post(() -> {
                ViewParent parent = viewProgressBar.getParent();
                if (parent instanceof View) {
                    View parentView = (View) parent;
                    int parentWidth = parentView.getWidth();
                    if (parentWidth > 0) {
                        ViewGroup.LayoutParams params = viewProgressBar.getLayoutParams();
                        params.width = (parentWidth * percent) / 100;
                        viewProgressBar.setLayoutParams(params);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.updateProgress();
        }
    }
}