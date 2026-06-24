package com.example.biruse_kolco.ui.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.trjwm.R;
import com.example.biruse_kolco.data.DistrictItem;
import com.example.biruse_kolco.data.DistrictRepository;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.model.MainViewModel;
import com.example.biruse_kolco.repository.DataRepository;
import com.example.biruse_kolco.util.ImageAssets;
import com.google.android.material.button.MaterialButton;

public class DistrictDetailFragment extends Fragment {
    private static final String ARG_DISTRICT_ID = "district_id";

    private MainViewModel viewModel;
    private District district;
    private DistrictItem districtItem;
    private MaterialButton completeButton;

    public static DistrictDetailFragment newInstance(int districtId) {
        DistrictDetailFragment fragment = new DistrictDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DISTRICT_ID, districtId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_district_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        int districtId = requireArguments().getInt(ARG_DISTRICT_ID);

        completeButton = view.findViewById(R.id.completeButton);
        MaterialButton timelineButton = view.findViewById(R.id.openTimelineButton);
        MaterialButton libraryButton = view.findViewById(R.id.openLibraryButton);
        MaterialButton gameButton = view.findViewById(R.id.openGameButton);

        timelineButton.setOnClickListener(v -> openDestination(R.id.timelineFragment));
        libraryButton.setOnClickListener(v -> openDestination(R.id.libraryFragment));
        gameButton.setOnClickListener(v -> openDestination(R.id.gameFragment));

        completeButton.setOnClickListener(v -> {
            if (district != null && !district.isCompleted()) {
                viewModel.completeDistrict(district.getId());
                district.setCompleted(true);
                renderCompleteState();
            }
        });

        loadDistrict(districtId, view);
    }

    private void loadDistrict(int districtId, View root) {
        DataRepository.getInstance(requireContext()).getDistrictById(districtId, loaded -> {
            if (loaded == null || !isAdded()) {
                return;
            }
            district = loaded;
            int index = Math.max(1, district.getOrderIndex());
            java.util.List<DistrictItem> districts = DistrictRepository.getDistricts();
            districtItem = districts.get(Math.min(index - 1, districts.size() - 1));
            requireActivity().runOnUiThread(() -> bindUi(root));
        });
    }

    private void bindUi(View view) {
        TextView name = view.findViewById(R.id.districtName);
        TextView subtitle = view.findViewById(R.id.districtSubtitle);
        TextView summary = view.findViewById(R.id.districtSummary);
        TextView herb = view.findViewById(R.id.districtHerbBadge);
        ImageView herbImage = view.findViewById(R.id.districtHerbImage);
        TextView history = view.findViewById(R.id.districtHistory);
        LinearLayout factsList = view.findViewById(R.id.factsList);
        LinearLayout photoStrip = view.findViewById(R.id.photoStrip);

        name.setText(districtItem.getName());
        subtitle.setText(districtItem.getSubtitle());
        summary.setText(districtItem.getSummary());
        herb.setText("Герб\n" + districtItem.getName().replace(" район", ""));
        history.setText(districtItem.getHistory());

        int herbRes = ImageAssets.drawableId(requireContext(),
                ImageAssets.coatImageNameForDistrict(districtItem.getId()));
        if (herbRes != 0) {
            herbImage.setImageResource(herbRes);
            herbImage.setVisibility(View.VISIBLE);
            herb.setVisibility(View.GONE);
        } else {
            herbImage.setVisibility(View.GONE);
            herb.setVisibility(View.VISIBLE);
        }

        factsList.removeAllViews();
        for (String fact : districtItem.getFacts()) {
            TextView factView = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_fact, factsList, false);
            factView.setText(fact);
            factsList.addView(factView);
        }

        photoStrip.removeAllViews();
        int index = 1;
        for (String photo : districtItem.getPhotos()) {
            View photoView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_photo_card, photoStrip, false);
            TextView badge = photoView.findViewById(R.id.photoBadge);
            TextView title = photoView.findViewById(R.id.photoTitle);
            TextView caption = photoView.findViewById(R.id.photoCaption);
            badge.setText("Фото " + index);
            title.setText(photo);
            caption.setText("Материал для медиатеки района");
            photoStrip.addView(photoView);
            index++;
        }

        renderCompleteState();
    }

    private void renderCompleteState() {
        boolean completed = district != null && district.isCompleted();
        completeButton.setText(completed ? R.string.district_mark_done : R.string.district_mark_complete);
        completeButton.setEnabled(!completed);
        completeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                completed ? R.color.gold_light : R.color.biruzovy));
        completeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark));
    }

    private void openDestination(int destinationId) {
        androidx.navigation.NavController navController =
                androidx.navigation.fragment.NavHostFragment.findNavController(this);
        navController.navigate(destinationId);
    }
}
