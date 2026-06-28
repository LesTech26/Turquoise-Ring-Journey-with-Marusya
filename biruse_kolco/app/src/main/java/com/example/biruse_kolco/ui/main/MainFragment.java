package com.example.biruse_kolco.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.games.MainActivity;
import com.example.biruse_kolco.model.MainViewModel;
import com.example.biruse_kolco.util.ImageAssets;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;
    private TextView tvProgress;
    private TextView tvProgressPercent;
    private TextView tvNextDistrict;
    private TextView tvNextDistrictHint;
    private TextView tvLevel;
    private TextView tvUserName;
    private ImageView ivNextDistrictCoat;
    private View progressBar;
    private CardView cardContinue;
    private CardView cardMap;
    private CardView cardGames;
    private CardView cardTimeline;
    private MaterialButton btnCompleteTraining;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        try {
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        } catch (Exception e) {
            navController = null;
        }

        tvProgress = view.findViewById(R.id.tv_progress);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvNextDistrict = view.findViewById(R.id.tv_next_district);
        tvNextDistrictHint = view.findViewById(R.id.tv_next_district_hint);
        tvLevel = view.findViewById(R.id.tv_level);
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivNextDistrictCoat = view.findViewById(R.id.iv_next_district_coat);
        progressBar = view.findViewById(R.id.view_progress_bar);
        cardContinue = view.findViewById(R.id.card_continue);
        cardMap = view.findViewById(R.id.card_map);
        cardGames = view.findViewById(R.id.card_games);
        cardTimeline = view.findViewById(R.id.card_timeline);
        btnCompleteTraining = view.findViewById(R.id.btn_complete_training);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvUserName.setText(user.getUserName() + " 👋");
                tvLevel.setText("Ур. " + user.getLevel());
            }
        });

        viewModel.getCompletedCount().observe(getViewLifecycleOwner(), count -> {
            int total = 5;
            tvProgress.setText(count + " / " + total + " районов");
            int percent = (count * 100) / total;
            tvProgressPercent.setText(percent + "% пройдено");

            if (progressBar != null && progressBar.getLayoutParams() != null) {
                View parent = (View) progressBar.getParent();
                if (parent != null) {
                    int parentWidth = parent.getWidth();
                    if (parentWidth > 0) {
                        int newWidth = (parentWidth * percent) / 100;
                        progressBar.getLayoutParams().width = newWidth;
                        progressBar.requestLayout();
                    }
                }
            }
        });

        viewModel.getIsTrainingComplete().observe(getViewLifecycleOwner(), isComplete -> refreshContinueState(viewModel.getNextDistrict().getValue()));

        viewModel.getNextDistrict().observe(getViewLifecycleOwner(), district -> {
            refreshContinueState(district);
        });

        cardContinue.setOnClickListener(v -> {
            if (navController != null) {
                District next = viewModel.getNextDistrict().getValue();
                if (next != null) {
                    String districtKey = districtKeyForName(next.getName());
                    if (districtKey != null) {
                        Bundle args = new Bundle();
                        args.putString("district_id", districtKey);
                        navController.navigate(R.id.action_main_to_district_detail, args);
                    }
                }
            } else {
                Intent intent = new Intent(getActivity(), com.example.biruse_kolco.ui.main.MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        btnCompleteTraining.setOnClickListener(v -> {
            Snackbar.make(v, "🎉 Поздравляем! Вы изучили все 5 районов!", Snackbar.LENGTH_LONG).show();
            viewModel.acknowledgeTrainingComplete();
            refreshContinueState(viewModel.getNextDistrict().getValue());
        });

        cardMap.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.action_main_to_map);
            } else {
                Intent intent = new Intent(getActivity(), com.example.biruse_kolco.ui.main.MainActivity.class);
                intent.putExtra("open_tab", "map");
                startActivity(intent);
                requireActivity().finish();
            }
        });

        cardGames.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        cardTimeline.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.action_main_to_timeline);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.updateProgress();
    }

    private void refreshContinueState(District nextDistrict) {
        Integer completedCount = viewModel.getCompletedCount().getValue();
        Boolean complete = viewModel.getIsTrainingComplete().getValue();
        boolean acknowledged = viewModel.isTrainingAcknowledged();

        if (complete != null && complete && acknowledged) {
            cardContinue.setVisibility(View.VISIBLE);
            btnCompleteTraining.setVisibility(View.GONE);
            cardContinue.setEnabled(false);
            cardContinue.setAlpha(0.5f);
            if (ivNextDistrictCoat != null) {
                ivNextDistrictCoat.setVisibility(View.GONE);
            }
            if (tvNextDistrictHint != null) {
                tvNextDistrictHint.setVisibility(View.GONE);
            }
            tvNextDistrict.setText("🎉 Вы великолепны!");
            return;
        }

        if (complete != null && complete) {
            cardContinue.setVisibility(View.GONE);
            btnCompleteTraining.setVisibility(View.VISIBLE);
            cardContinue.setEnabled(false);
            cardContinue.setAlpha(0.5f);
            tvNextDistrict.setText("🎉 Все районы изучены!");
            return;
        }

        btnCompleteTraining.setVisibility(View.GONE);
        cardContinue.setVisibility(View.VISIBLE);
        cardContinue.setEnabled(true);
        cardContinue.setAlpha(1f);
        if (ivNextDistrictCoat != null) {
            ivNextDistrictCoat.setVisibility(View.VISIBLE);
        }
        if (tvNextDistrictHint != null) {
            tvNextDistrictHint.setVisibility(View.VISIBLE);
        }
        if (nextDistrict != null) {
            tvNextDistrict.setText(nextDistrict.getName());
            updateNextDistrictCoat(nextDistrict.getName());
        }
        if (completedCount != null) {
            tvProgress.setText(completedCount + " / 5 районов");
        }
    }

    private void updateNextDistrictCoat(String districtName) {
        if (ivNextDistrictCoat == null) {
            return;
        }
        String districtKey = districtKeyForName(districtName);
        int coatRes = ImageAssets.drawableId(requireContext(),
                ImageAssets.coatImageNameForDistrict(districtKey));
        ivNextDistrictCoat.setImageResource(coatRes != 0 ? coatRes : R.drawable.gerb);
        ivNextDistrictCoat.setContentDescription("Герб " + districtName);
    }

    private String districtKeyForName(String districtName) {
        if (districtName == null) {
            return null;
        }
        switch (districtName) {
            case "Хотынецкий район":
                return "hotynets";
            case "Болховский район":
                return "bolkhov";
            case "Мценский район":
                return "mcensk";
            case "Дмитровский район":
                return "dmitrov";
            case "Троснянский район":
                return "trosna";
            default:
                return null;
        }
    }
}
