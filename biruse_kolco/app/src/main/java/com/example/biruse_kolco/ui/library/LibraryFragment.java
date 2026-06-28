package com.example.biruse_kolco.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.data.DistrictItem;
import com.example.biruse_kolco.data.DistrictRepository;
import com.example.biruse_kolco.util.ImageAssets; // ДОБАВЛЯЕМ ИМПОРТ

public class LibraryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout list = view.findViewById(R.id.libraryList);

        list.removeAllViews();

        // Группируем районы по первой букве
        java.util.Map<String, java.util.List<DistrictItem>> grouped = new java.util.LinkedHashMap<>();
        for (DistrictItem district : DistrictRepository.getDistricts()) {
            String firstLetter = district.getName().substring(0, 1);
            if (!grouped.containsKey(firstLetter)) {
                grouped.put(firstLetter, new java.util.ArrayList<>());
            }
            grouped.get(firstLetter).add(district);
        }

        // Отображаем сгруппированный список
        for (String letter : grouped.keySet()) {
            // Заголовок буквы
            TextView header = new TextView(requireContext());
            header.setText(letter);
            header.setTextSize(18);
            header.setTextColor(getResources().getColor(R.color.orel_green, null));
            header.setTypeface(header.getTypeface(), android.graphics.Typeface.BOLD);
            header.setPadding(dp(0), dp(16), dp(0), dp(8));
            list.addView(header);

            // Районы этой буквы
            for (DistrictItem district : grouped.get(letter)) {
                View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_library_card, list, false);
                TextView title = card.findViewById(R.id.libraryName);
                TextView stats = card.findViewById(R.id.libraryStats);
                ImageView herbImage = card.findViewById(R.id.libraryHerbImage);

                if (title != null) title.setText(district.getName());
                if (stats != null) stats.setText(district.getPhotos().size() + " фото, " + district.getFacts().size() + " факта, 1 герб");

                // Загружаем герб
                if (herbImage != null) {
                    int herbRes = ImageAssets.drawableId(requireContext(),
                            ImageAssets.coatImageNameForDistrict(district.getId()));
                    if (herbRes != 0) {
                        herbImage.setImageResource(herbRes);
                        herbImage.setVisibility(View.VISIBLE);
                    } else {
                        herbImage.setImageResource(R.drawable.ic_launcher_foreground);
                        herbImage.setVisibility(View.VISIBLE);
                    }
                }

                card.setOnClickListener(v -> openDistrict(district.getId()));
                list.addView(card);
            }
        }
    }

    private void openDistrict(String districtId) {
        Bundle args = new Bundle();
        args.putString("district_id", districtId);
        NavHostFragment.findNavController(this).navigate(R.id.districtDetailFragment, args);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
