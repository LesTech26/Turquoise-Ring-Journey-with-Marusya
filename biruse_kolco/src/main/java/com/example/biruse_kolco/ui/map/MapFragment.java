package com.example.biruse_kolco.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trjwm.R;
import com.example.biruse_kolco.data.MapPointRepository;
import com.example.biruse_kolco.data.ProgressStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MapFragment extends Fragment {
    private TextView progressLabel;
    private TextView progressPercent;
    private ProgressBar progressBar;
    private WebView mapWebView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressLabel = view.findViewById(R.id.progressLabel);
        progressPercent = view.findViewById(R.id.progressPercent);
        progressBar = view.findViewById(R.id.progressBar);
        mapWebView = view.findViewById(R.id.mapWebView);

        configureMapWebView();
        loadMapHtml();

        view.findViewById(R.id.openTimelineButton).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.timelineFragment));
        view.findViewById(R.id.openLibraryButton).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.libraryFragment));
        view.findViewById(R.id.openGameButton).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.gameFragment));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgress();
    }

    @Override
    public void onDestroyView() {
        if (mapWebView != null) {
            mapWebView.stopLoading();
            mapWebView.loadUrl("about:blank");
            mapWebView.clearHistory();
            mapWebView.removeAllViews();
            mapWebView.destroy();
            mapWebView = null;
        }
        super.onDestroyView();
    }

    private void configureMapWebView() {
        android.webkit.WebSettings settings = mapWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(false);
        mapWebView.setWebViewClient(new WebViewClient());
        mapWebView.addJavascriptInterface(new MapBridge(), "Android");
    }

    private void loadMapHtml() {
        String html = readAsset("leaflet_map.html");
        if (html == null) {
            mapWebView.loadData("<html><body>Не удалось загрузить карту.</body></html>",
                    "text/html", "UTF-8");
            return;
        }

        html = html.replace("__POINTS_JSON__", buildPointsJson());

        mapWebView.loadDataWithBaseURL("https://trjwm.local/", html,
                "text/html", StandardCharsets.UTF_8.name(), null);
    }

    private String buildPointsJson() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        boolean first = true;
        for (com.example.biruse_kolco.data.MapPoint point : MapPointRepository.getPoints()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append('{')
                    .append("\"districtId\":").append(jsonString(point.getDistrictId())).append(',')
                    .append("\"title\":").append(jsonString(point.getTitle())).append(',')
                    .append("\"description\":").append(jsonString(point.getDescription())).append(',')
                    .append("\"latitude\":").append(point.getLatitude()).append(',')
                    .append("\"longitude\":").append(point.getLongitude()).append(',')
                    .append("\"pointType\":").append(jsonString(point.getPointType()))
                    .append('}');
        }
        builder.append(']');
        return builder.toString();
    }

    private String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        String escaped = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
        return "\"" + escaped + "\"";
    }

    private String readAsset(String fileName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                requireContext().getAssets().open(fileName), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private void updateProgress() {
        int total = com.example.biruse_kolco.data.DistrictRepository.getDistricts().size();
        int completed = ProgressStore.getCompletedCount(requireContext());
        int percent = Math.round((completed * 100f) / total);
        progressLabel.setText(getString(R.string.map_completed_format, completed, total));
        progressPercent.setText(getString(R.string.map_progress_format, percent));
        progressBar.setMax(100);
        progressBar.setProgress(percent);
    }

    private int districtIdToRoomId(String districtId) {
        java.util.List<com.example.biruse_kolco.data.DistrictItem> districts =
                com.example.biruse_kolco.data.DistrictRepository.getDistricts();
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).getId().equals(districtId)) {
                return i + 1;
            }
        }
        return 1;
    }

    private class MapBridge {
        @JavascriptInterface
        public void openDistrict(String districtId) {
            if (districtId != null && !districtId.isEmpty()) {
                requireActivity().runOnUiThread(() -> {
                    Bundle args = new Bundle();
                    args.putInt("district_id", MapFragment.this.districtIdToRoomId(districtId));
                    NavHostFragment.findNavController(MapFragment.this)
                            .navigate(R.id.districtDetailFragment, args);
                });
            }
        }
    }
}
