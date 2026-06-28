package com.example.biruse_kolco.trjwm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.model.MainViewModel;
import com.example.biruse_kolco.trjwm.data.DistrictItem;
import com.example.biruse_kolco.trjwm.data.DistrictRepository;
import com.example.biruse_kolco.trjwm.data.ProgressStore;
import com.example.biruse_kolco.ui.main.MainActivity;
import com.example.biruse_kolco.util.ImageAssets;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class DistrictDetailFragment extends Fragment {
    private static final String ARG_DISTRICT_ID = "district_id";

    public interface Listener {
        void onToggleCompleted(String districtId, boolean completed);
        void onDistrictCompleted();
    }

    private Listener listener;
    private DistrictItem district;
    private MaterialButton completeButton;
    private MaterialButton nextButton;
    private View btnBackToMain;
    private View rootView;
    private MainViewModel mainViewModel;

    public static DistrictDetailFragment newInstance(String districtId) {
        DistrictDetailFragment fragment = new DistrictDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DISTRICT_ID, districtId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_district_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        String districtId = requireArguments().getString(ARG_DISTRICT_ID);
        Log.d("DistrictDetailFragment", "open district_id=" + districtId);
        district = DistrictRepository.getDistrict(districtId);

        TextView name = view.findViewById(R.id.districtName);
        TextView subtitle = view.findViewById(R.id.districtSubtitle);
        TextView herb = view.findViewById(R.id.districtHerbBadge);
        ImageView herbImage = view.findViewById(R.id.districtHerbImage);
        TextView history = view.findViewById(R.id.districtHistory);
        LinearLayout factsList = view.findViewById(R.id.factsList);
        LinearLayout photoStrip = view.findViewById(R.id.photoStrip);
        completeButton = view.findViewById(R.id.completeButton);
        nextButton = view.findViewById(R.id.nextButton);
        btnBackToMain = view.findViewById(R.id.btnBackToMain);

        if (district != null) {
            name.setText(district.getName());
            subtitle.setText(district.getSubtitle());
            herb.setText("Герб\n" + district.getName().replace(" район", ""));
            history.setText(district.getHistory());

            int herbRes = ImageAssets.drawableId(requireContext(),
                    ImageAssets.coatImageNameForDistrict(district.getId()));
            if (herbRes != 0) {
                herbImage.setImageResource(herbRes);
                herbImage.setVisibility(View.VISIBLE);
                herb.setVisibility(View.GONE);
            } else {
                herbImage.setVisibility(View.GONE);
                herb.setVisibility(View.VISIBLE);
            }

            factsList.removeAllViews();
            for (String fact : district.getFacts()) {
                TextView factView = (TextView) LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_fact, factsList, false);
                factView.setText(fact);
                factsList.addView(factView);
            }

            // ============================================================
            // ФОТОГАЛЕРЕЯ — ПОКАЗЫВАЕМ ГЕРБ ВМЕСТО ФОТО
            // ============================================================
            photoStrip.removeAllViews();
            int index = 1;

            for (String photo : district.getPhotos()) {
                View photoView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_photo_card, photoStrip, false);
                TextView badge = photoView.findViewById(R.id.photoBadge);
                TextView title = photoView.findViewById(R.id.photoTitle);
                ImageView photoImage = photoView.findViewById(R.id.photoImage);
                String photoResName = ImageAssets.photoImageName(photo);
                int photoRes = ImageAssets.drawableId(requireContext(), photoResName);

                badge.setText("Фото " + index);
                title.setText(photo);

                if (photoImage != null && photoRes != 0) {
                    photoImage.setImageResource(photoRes);
                    photoImage.setVisibility(View.VISIBLE);
                    photoImage.setOnClickListener(v -> ImageViewerDialogFragment
                            .newInstance(photoResName)
                            .show(getParentFragmentManager(), "image_viewer"));
                } else if (photoImage != null) {
                    photoImage.setImageResource(R.drawable.ic_launcher_foreground);
                    photoImage.setVisibility(View.VISIBLE);
                    photoImage.setOnClickListener(v -> ImageViewerDialogFragment
                            .newInstance(photoResName)
                            .show(getParentFragmentManager(), "image_viewer"));
                }

                photoStrip.addView(photoView);
                index++;
            }

            updateCompleteButton();
            checkNextButtonVisibility();
            refreshCompletionControls();
        }

        // ============================================================
        // КНОПКА "ОТМЕТИТЬ ПРОЙДЕННЫМ"
        // ============================================================
        completeButton.setOnClickListener(v -> {
            if (district != null) {
                boolean newValue = !ProgressStore.isCompleted(requireContext(), district.getId());
                ProgressStore.setCompleted(requireContext(), district.getId(), newValue);
                if (newValue) {
                    mainViewModel.completeDistrictByName(district.getName());
                } else {
                    mainViewModel.uncompleteDistrictByName(district.getName());
                }
                updateCompleteButton();
                checkNextButtonVisibility();
                if (listener != null) {
                    listener.onToggleCompleted(district.getId(), newValue);
                    listener.onDistrictCompleted();
                }
            }
        });

        // ============================================================
        // КНОПКА "СЛЕДУЮЩИЙ РАЙОН" / "ЗАВЕРШИТЬ"
        // ============================================================
        nextButton.setOnClickListener(v -> {
            if (district == null) return;

            boolean completed = ProgressStore.isCompleted(requireContext(), district.getId());
            if (!completed) {
                ProgressStore.setCompleted(requireContext(), district.getId(), true);
                mainViewModel.completeDistrictByName(district.getName());
                updateCompleteButton();
                refreshCompletionControls();
                if (listener != null) {
                    listener.onToggleCompleted(district.getId(), true);
                    listener.onDistrictCompleted();
                }
            }

            DistrictItem nextDistrict = null;
            for (DistrictItem d : DistrictRepository.getDistricts()) {
                if (!ProgressStore.isCompleted(requireContext(), d.getId())) {
                    nextDistrict = d;
                    break;
                }
            }

            if (nextDistrict != null) {
                DistrictDetailFragment nextFragment = DistrictDetailFragment.newInstance(nextDistrict.getId());
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.nav_host_fragment, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Snackbar.make(rootView, "🎉 Поздравляем! Вы изучили все 5 районов!", Snackbar.LENGTH_LONG).show();
                goToMainFragment();
            }
        });

        // ============================================================
        // КНОПКА "НА ГЛАВНУЮ"
        // ============================================================
        btnBackToMain.setOnClickListener(v -> {
            goToMainFragment();
        });
    }

    private void goToMainFragment() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void updateCompleteButton() {
        if (district == null || completeButton == null) return;
        boolean completed = ProgressStore.isCompleted(requireContext(), district.getId());
        completeButton.setText(completed ? R.string.district_mark_done : R.string.district_mark_complete);
        completeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                completed ? R.color.gold_500 : R.color.turquoise_500));
        completeButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }

    private void checkNextButtonVisibility() {
        if (district == null || nextButton == null) return;

        boolean currentCompleted = ProgressStore.isCompleted(requireContext(), district.getId());
        boolean acknowledged = mainViewModel != null && mainViewModel.isTrainingAcknowledged();
        boolean trainingComplete = mainViewModel != null
                && Boolean.TRUE.equals(mainViewModel.getIsTrainingComplete().getValue());

        if (trainingComplete && acknowledged) {
            nextButton.setVisibility(View.GONE);
            if (completeButton != null) {
                completeButton.setVisibility(View.GONE);
            }
            return;
        }

        boolean hasNext = false;
        for (DistrictItem d : DistrictRepository.getDistricts()) {
            if (!ProgressStore.isCompleted(requireContext(), d.getId())) {
                hasNext = true;
                break;
            }
        }

        if (currentCompleted && hasNext) {
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setText("Следующий район");
        } else if (currentCompleted && !hasNext) {
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setText("Завершить");
        } else {
            nextButton.setVisibility(View.GONE);
        }
    }

    private void refreshCompletionControls() {
        boolean acknowledged = mainViewModel != null && mainViewModel.isTrainingAcknowledged();
        boolean trainingComplete = mainViewModel != null
                && Boolean.TRUE.equals(mainViewModel.getIsTrainingComplete().getValue());
        if (trainingComplete && acknowledged) {
            if (completeButton != null) {
                completeButton.setVisibility(View.GONE);
            }
            if (nextButton != null) {
                nextButton.setVisibility(View.GONE);
            }
        } else {
            if (completeButton != null) {
                completeButton.setVisibility(View.VISIBLE);
            }
            checkNextButtonVisibility();
        }
    }
}
