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

import com.example.trjwm.R;
import com.example.biruse_kolco.data.TimelineEntry;
import com.example.biruse_kolco.data.DistrictRepository;
import com.google.android.material.card.MaterialCardView;

public class TimelineFragment extends Fragment {
    private TextView selectedLabel;
    private TextView selectedTitle;
    private TextView selectedDescription;

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
        TextView intro = view.findViewById(R.id.timelineIntro);
        selectedLabel = view.findViewById(R.id.timelineSelected);

        intro.setText("Откройте ключевые периоды истории Орловской области и нажимайте на блоки, чтобы узнать подробности.");

        list.removeAllViews();
        for (TimelineEntry entry : DistrictRepository.getTimeline()) {
            MaterialCardView card = (MaterialCardView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_timeline_event, list, false);
            TextView period = card.findViewById(R.id.timelinePeriod);
            TextView title = card.findViewById(R.id.timelineTitle);
            TextView description = card.findViewById(R.id.timelineDescription);
            period.setText(entry.getPeriod());
            title.setText(entry.getTitle());
            description.setText(entry.getDescription());
            card.setOnClickListener(v -> highlightEntry(card, entry));
            list.addView(card);
        }

        // Selected detail card inserted at top for interactive feedback.
        selectedLabel.setText(R.string.timeline_selected);
        selectedTitle = new TextView(requireContext());
        selectedDescription = new TextView(requireContext());
        selectedTitle.setTextColor(getResources().getColor(R.color.ink_900, null));
        selectedTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        selectedTitle.setPadding(0, dp(2), 0, 0);
        selectedTitle.setTypeface(selectedTitle.getTypeface(), android.graphics.Typeface.BOLD);
        selectedDescription.setTextColor(getResources().getColor(R.color.ink_700, null));
        selectedDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        ViewGroup root = (ViewGroup) view.findViewById(android.R.id.content);
        // No-op: detail text is appended into the intro card area by updating the header label.
        if (DistrictRepository.getTimeline().size() > 0) {
            highlightEntry((MaterialCardView) list.getChildAt(0), DistrictRepository.getTimeline().get(0));
        }
    }

    private void highlightEntry(MaterialCardView card, TimelineEntry entry) {
        selectedLabel.setText(entry.getPeriod() + " · " + entry.getTitle());
        TextView intro = requireView().findViewById(R.id.timelineIntro);
        intro.setText(entry.getDescription());

        LinearLayout list = requireView().findViewById(R.id.timelineList);
        for (int i = 0; i < list.getChildCount(); i++) {
            MaterialCardView item = (MaterialCardView) list.getChildAt(i);
            item.setStrokeWidth(dp(1));
            item.setStrokeColor(getResources().getColor(R.color.mint_200, null));
            item.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
        }
        card.setStrokeWidth(dp(2));
        card.setStrokeColor(getResources().getColor(R.color.turquoise_500, null));
        card.setCardBackgroundColor(getResources().getColor(R.color.mint_100, null));
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }
}
