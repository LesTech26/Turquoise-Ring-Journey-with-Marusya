package com.example.biruse_kolco.ui.main;

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
import androidx.navigation.fragment.NavHostFragment;

import com.example.trjwm.R;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.data.database.entities.User;
import com.example.biruse_kolco.model.MainViewModel;
import com.google.android.material.button.MaterialButton;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;
    private TextView tvUserName, tvProgress, tvLevel;
    private TextView tvNextDistrict, tvProgressPercent;
    private View viewProgressBar;
    private CardView cardContinue, cardRoutePreview;
    private MaterialButton btnTimeline, btnLibrary, btnGame;
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
        cardRoutePreview = view.findViewById(R.id.card_route_preview);
        btnTimeline = view.findViewById(R.id.btn_open_timeline);
        btnLibrary = view.findViewById(R.id.btn_open_library);
        btnGame = view.findViewById(R.id.btn_open_game);

        setViewsAlphaZero();

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), this::updateUserInfo);
        viewModel.getNextDistrict().observe(getViewLifecycleOwner(), this::updateNextDistrict);
        viewModel.getCompletedCount().observe(getViewLifecycleOwner(), this::updateProgress);

        cardContinue.setOnClickListener(v -> {
            District next = viewModel.getNextDistrict().getValue();
            if (next != null) {
                openDistrict(next.getId());
            }
        });

        cardRoutePreview.setOnClickListener(v -> openDestination(R.id.mapFragment));
        btnTimeline.setOnClickListener(v -> openDestination(R.id.timelineFragment));
        btnLibrary.setOnClickListener(v -> openDestination(R.id.libraryFragment));
        btnGame.setOnClickListener(v -> openDestination(R.id.gameFragment));

        new Handler().postDelayed(this::startStaggeredAnimation, 300);
    }

    private void setViewsAlphaZero() {
        View header = rootView.findViewById(R.id.layout_header);
        if (header != null) header.setAlpha(0f);
        View cardProgress = rootView.findViewById(R.id.card_progress_main);
        if (cardProgress != null) cardProgress.setAlpha(0f);
        cardContinue.setAlpha(0f);
        View routeLabel = rootView.findViewById(R.id.tv_route_label);
        if (routeLabel != null) routeLabel.setAlpha(0f);
        cardRoutePreview.setAlpha(0f);
        btnTimeline.setAlpha(0f);
        btnLibrary.setAlpha(0f);
        btnGame.setAlpha(0f);
        View mascotLabel = rootView.findViewById(R.id.tv_mascot_label);
        if (mascotLabel != null) mascotLabel.setAlpha(0f);
        View cardMascot = rootView.findViewById(R.id.card_mascot);
        if (cardMascot != null) cardMascot.setAlpha(0f);
    }

    private void startStaggeredAnimation() {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());

        View header = rootView.findViewById(R.id.layout_header);
        ObjectAnimator headerAnim = ObjectAnimator.ofFloat(header, "alpha", 0f, 1f);
        headerAnim.setDuration(600);

        View cardProgress = rootView.findViewById(R.id.card_progress_main);
        ObjectAnimator progressAnim = ObjectAnimator.ofFloat(cardProgress, "alpha", 0f, 1f);
        progressAnim.setDuration(600);
        progressAnim.setStartDelay(150);

        ObjectAnimator continueAnim = ObjectAnimator.ofFloat(cardContinue, "alpha", 0f, 1f);
        continueAnim.setDuration(600);
        continueAnim.setStartDelay(300);

        View routeLabel = rootView.findViewById(R.id.tv_route_label);
        ObjectAnimator routeLabelAnim = ObjectAnimator.ofFloat(routeLabel, "alpha", 0f, 1f);
        routeLabelAnim.setDuration(500);
        routeLabelAnim.setStartDelay(450);

        ObjectAnimator routeCardAnim = ObjectAnimator.ofFloat(cardRoutePreview, "alpha", 0f, 1f);
        routeCardAnim.setDuration(500);
        routeCardAnim.setStartDelay(550);

        ObjectAnimator timelineAnim = ObjectAnimator.ofFloat(btnTimeline, "alpha", 0f, 1f);
        timelineAnim.setDuration(500);
        timelineAnim.setStartDelay(650);

        ObjectAnimator libraryAnim = ObjectAnimator.ofFloat(btnLibrary, "alpha", 0f, 1f);
        libraryAnim.setDuration(500);
        libraryAnim.setStartDelay(750);

        ObjectAnimator gameAnim = ObjectAnimator.ofFloat(btnGame, "alpha", 0f, 1f);
        gameAnim.setDuration(500);
        gameAnim.setStartDelay(850);

        View mascotLabel = rootView.findViewById(R.id.tv_mascot_label);
        ObjectAnimator mascotLabelAnim = ObjectAnimator.ofFloat(mascotLabel, "alpha", 0f, 1f);
        mascotLabelAnim.setDuration(500);
        mascotLabelAnim.setStartDelay(950);

        View cardMascot = rootView.findViewById(R.id.card_mascot);
        ObjectAnimator mascotCardAnim = ObjectAnimator.ofFloat(cardMascot, "alpha", 0f, 1f);
        mascotCardAnim.setDuration(500);
        mascotCardAnim.setStartDelay(1050);

        AnimatorSet.Builder builder = animSet.play(headerAnim);
        builder.with(progressAnim);
        builder.with(continueAnim);
        builder.with(routeLabelAnim);
        builder.with(routeCardAnim);
        builder.with(timelineAnim);
        builder.with(libraryAnim);
        builder.with(gameAnim);
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

    private void openDistrict(int districtId) {
        Bundle args = new Bundle();
        args.putInt("district_id", districtId);
        openDestination(R.id.districtDetailFragment, args);
    }

    private void openDestination(int destinationId) {
        openDestination(destinationId, null);
    }

    private void openDestination(int destinationId, @Nullable Bundle args) {
        NavHostFragment.findNavController(this).navigate(destinationId, args);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.updateProgress();
        }
    }
}
