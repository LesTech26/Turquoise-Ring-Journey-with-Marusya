package com.example.biruse_kolco.ui.timeline;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biruse_kolco.R; // ИСПРАВЛЕНО
import com.example.biruse_kolco.data.DistrictRepository; // ИСПРАВЛЕНО
import com.example.biruse_kolco.data.TimelineEntry; // ИСПРАВЛЕНО
import com.google.android.material.card.MaterialCardView;

public class TimelineFragment extends Fragment {
    private TextView selectedLabel;
    private TextView descriptionLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout list = view.findViewById(R.id.timelineList);
        selectedLabel = view.findViewById(R.id.timelineSelected);
        descriptionLabel = view.findViewById(R.id.timelineDescription);

        list.removeAllViews();
        for (TimelineEntry entry : DistrictRepository.getTimeline()) {
            MaterialCardView card = (MaterialCardView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_timeline_event, list, false);
            TextView period = card.findViewById(R.id.timelinePeriod);
            TextView title = card.findViewById(R.id.timelineTitle);
            TextView desc = card.findViewById(R.id.timelineDescription);
            period.setText(entry.getPeriod());
            title.setText(entry.getTitle());
            desc.setText(entry.getShortDescription());
            card.setOnClickListener(v -> highlightEntry(entry, list));
            list.addView(card);
        }

        if (DistrictRepository.getTimeline().size() > 0) {
            highlightEntry(DistrictRepository.getTimeline().get(0), list);
        }
    }

    private void highlightEntry(TimelineEntry entry, LinearLayout list) {
        selectedLabel.setText(entry.getPeriod() + " · " + entry.getTitle());
        descriptionLabel.setText(entry.getFullDescription());

        for (int i = 0; i < list.getChildCount(); i++) {
            MaterialCardView item = (MaterialCardView) list.getChildAt(i);
            item.setStrokeWidth(dp(1));
            item.setStrokeColor(getResources().getColor(R.color.orel_gray_light, null));
            item.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
        }

        for (int i = 0; i < list.getChildCount(); i++) {
            MaterialCardView item = (MaterialCardView) list.getChildAt(i);
            TextView period = item.findViewById(R.id.timelinePeriod);
            if (period.getText().toString().equals(entry.getPeriod())) {
                item.setStrokeWidth(dp(2));
                item.setStrokeColor(getResources().getColor(R.color.gold, null));
                item.setCardBackgroundColor(getResources().getColor(R.color.gold_light, null));
                break;
            }
        }
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }
}
