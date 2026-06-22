package com.example.trjwm.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
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

import com.example.trjwm.R;
import com.example.trjwm.data.DistrictItem;
import com.example.trjwm.data.DistrictRepository;
import com.example.trjwm.data.ProgressStore;
import com.example.trjwm.util.ImageAssets;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class DistrictDetailFragment extends Fragment {
    private static final String ARG_DISTRICT_ID = "district_id";

    public interface Listener {
        void onOpenTimeline();

        void onOpenLibrary();

        void onToggleCompleted(String districtId, boolean completed);
    }

    private Listener listener;
    private DistrictItem district;
    private MaterialButton completeButton;

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
        String districtId = requireArguments().getString(ARG_DISTRICT_ID);
        district = DistrictRepository.getDistrict(districtId);

        TextView name = view.findViewById(R.id.districtName);
        TextView subtitle = view.findViewById(R.id.districtSubtitle);
        TextView summary = view.findViewById(R.id.districtSummary);
        TextView herb = view.findViewById(R.id.districtHerbBadge);
        ImageView herbImage = view.findViewById(R.id.districtHerbImage);
        TextView history = view.findViewById(R.id.districtHistory);
        LinearLayout factsList = view.findViewById(R.id.factsList);
        LinearLayout photoStrip = view.findViewById(R.id.photoStrip);
        completeButton = view.findViewById(R.id.completeButton);
        MaterialButton timelineButton = view.findViewById(R.id.openTimelineButton);
        MaterialButton libraryButton = view.findViewById(R.id.openLibraryButton);

        name.setText(district.getName());
        subtitle.setText(district.getSubtitle());
        summary.setText(district.getSummary());
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
            TextView factView = (TextView) LayoutInflater.from(requireContext()).inflate(R.layout.item_fact, factsList, false);
            factView.setText(fact);
            factsList.addView(factView);
        }

        photoStrip.removeAllViews();
        int index = 1;
        for (String photo : district.getPhotos()) {
            View photoView = LayoutInflater.from(requireContext()).inflate(R.layout.item_photo_card, photoStrip, false);
            TextView badge = photoView.findViewById(R.id.photoBadge);
            TextView title = photoView.findViewById(R.id.photoTitle);
            TextView caption = photoView.findViewById(R.id.photoCaption);
            badge.setText("Фото " + index);
            title.setText(photo);
            caption.setText("Материал для медиатеки района");
            photoStrip.addView(photoView);
            index++;
        }

        updateCompleteButton();
        completeButton.setOnClickListener(v -> {
            boolean newValue = !ProgressStore.isCompleted(requireContext(), district.getId());
            ProgressStore.setCompleted(requireContext(), district.getId(), newValue);
            updateCompleteButton();
            if (listener != null) {
                listener.onToggleCompleted(district.getId(), newValue);
            }
        });

        timelineButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenTimeline();
            }
        });

        libraryButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenLibrary();
            }
        });
    }

    private void updateCompleteButton() {
        boolean completed = ProgressStore.isCompleted(requireContext(), district.getId());
        completeButton.setText(completed ? R.string.district_mark_done : R.string.district_mark_complete);
        completeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                completed ? R.color.gold_500 : R.color.turquoise_500));
        completeButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }
}
