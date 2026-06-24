package com.example.biruse_kolco.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trjwm.R;
import com.example.biruse_kolco.data.DistrictItem;
import com.example.biruse_kolco.data.DistrictRepository;

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
        TextView summary = view.findViewById(R.id.librarySummary);
        summary.setText("В медиатеке собраны материалы по всем 24 районам: тексты, фото-заготовки и гербы для дальнейшего наполнения.");

        list.removeAllViews();
        for (DistrictItem district : DistrictRepository.getDistricts()) {
            View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_library_card, list, false);
            TextView title = card.findViewById(R.id.libraryName);
            TextView stats = card.findViewById(R.id.libraryStats);
            TextView hint = card.findViewById(R.id.libraryHint);
            title.setText(district.getName());
            stats.setText(district.getPhotos().size() + " фото, " + district.getFacts().size() + " факта, 1 герб");
            hint.setText("Открыть район в медиатеке");
            card.setOnClickListener(v -> openDistrict(district.getId()));
            list.addView(card);
        }
    }

    private void openDistrict(String districtId) {
        java.util.List<DistrictItem> districts = DistrictRepository.getDistricts();
        int roomId = 1;
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).getId().equals(districtId)) {
                roomId = i + 1;
                break;
            }
        }
        Bundle args = new Bundle();
        args.putInt("district_id", roomId);
        NavHostFragment.findNavController(this).navigate(R.id.districtDetailFragment, args);
    }
}
