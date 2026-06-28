package com.example.biruse_kolco.trjwm.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.data.DistrictItem;
import com.example.biruse_kolco.data.DistrictRepository;
import com.example.biruse_kolco.trjwm.data.MapDataCache;
import com.example.biruse_kolco.trjwm.data.MapDataCache.CacheData;
import com.example.biruse_kolco.trjwm.data.MapDataCache.MapPointEntry;
import com.example.biruse_kolco.trjwm.data.MapDataCache.MapShape;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private static final Point ORLOVSKAYA_CENTER = new Point(52.9686, 36.0698);
    private static final float MAP_ZOOM = 8.0f;
    private static final LinkedHashMap<String, String> BOUNDARY_QUERIES = new LinkedHashMap<>();
    private static final List<PointSpec> POINTS = new ArrayList<>();

    static {
        BOUNDARY_QUERIES.put("oblast", "Орловская область");
        BOUNDARY_QUERIES.put("hotynets", "Хотынецкий район Орловская область");
        BOUNDARY_QUERIES.put("bolkhov", "Болховский район Орловская область");
        BOUNDARY_QUERIES.put("mcensk", "Мценский район Орловская область");
        BOUNDARY_QUERIES.put("dmitrov", "Дмитровский район Орловская область");
        BOUNDARY_QUERIES.put("trosna", "Троснянский район Орловская область");

        POINTS.add(new PointSpec("hotynets", "Хотынец", "Административный центр Хотынецкого района", new Point(53.1270, 35.3990), "district"));
        POINTS.add(new PointSpec("hotynets", "Орловское Полесье", "Природный парк, упомянутый в медиатеке района", new Point(53.2220, 35.3210), "nature"));
        POINTS.add(new PointSpec("hotynets", "Музей Тургеневское полесье", "Музей, связанный с районом", new Point(53.1270, 35.3990), "culture"));

        POINTS.add(new PointSpec("bolkhov", "Болхов", "Административный центр района", new Point(53.4500, 36.0000), "district"));
        POINTS.add(new PointSpec("bolkhov", "Кривцовский мемориал", "Памятный объект, упомянутый в медиатеке", new Point(53.4320, 35.9650), "memorial"));
        POINTS.add(new PointSpec("bolkhov", "Река Ока", "Река, упомянутая в медиатеке района", new Point(53.3900, 36.0600), "river"));

        POINTS.add(new PointSpec("mcensk", "Мценск", "Город, связанный с Мценским районом", new Point(53.2800, 36.5800), "district"));
        POINTS.add(new PointSpec("mcensk", "Сельский пейзаж", "Иллюстративная точка для медиатеки района", new Point(53.2100, 36.5200), "photo"));
        POINTS.add(new PointSpec("mcensk", "Историческая улица", "Иллюстративная точка для медиатеки района", new Point(53.2800, 36.5900), "culture"));

        POINTS.add(new PointSpec("dmitrov", "Дмитровск", "Административный центр района", new Point(52.5000, 35.1500), "district"));
        POINTS.add(new PointSpec("dmitrov", "Данилова Дача", "Лесной массив, упомянутый в медиатеке", new Point(52.5450, 35.2500), "forest"));
        POINTS.add(new PointSpec("dmitrov", "Клягинский лес", "Лесной массив, упомянутый в медиатеке", new Point(52.5600, 35.1900), "forest"));
        POINTS.add(new PointSpec("dmitrov", "Сельская дорога", "Иллюстративная точка для медиатеки района", new Point(52.4700, 35.2200), "road"));

        POINTS.add(new PointSpec("trosna", "Тросна", "Административный центр района", new Point(52.4442, 35.7814), "district"));
        POINTS.add(new PointSpec("trosna", "Река Тросна", "Река, упомянутая в медиатеке района", new Point(52.4405, 35.7950), "river"));
    }

    private ExecutorService executor;

    private NavController navController;
    private View rootView;
    private MapView mapView;
    private View loadingOverlay;
    private CardView btnBackToMain;
    private View pointPopup;
    private TextView popupTitle;
    private TextView popupDescription;
    private View popupOpenLibrary;
    private View popupClose;
    private PointSpec selectedSpec;
    private boolean popupVisible;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private boolean mapStarted;
    private InputListener mapTapListener;
    private CacheData cachedMapData;
    private List<MapPointEntry> currentPoints = new ArrayList<>();
    private static final double POINT_TAP_THRESHOLD_PX = 72.0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        mapView = view.findViewById(R.id.map_view);
        loadingOverlay = view.findViewById(R.id.map_loading_overlay);
        btnBackToMain = view.findViewById(R.id.btnBackToMain);
        pointPopup = view.findViewById(R.id.point_popup);
        popupTitle = view.findViewById(R.id.popupTitle);
        popupDescription = view.findViewById(R.id.popupDescription);
        popupOpenLibrary = view.findViewById(R.id.popupOpenLibrary);
        popupClose = view.findViewById(R.id.popupClose);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Log.d(TAG, "location permissions result=" + result);
                    startMapIfPossible();
                }
        );

        setViewsAlphaZero();
        setupMap();
        setupMapTapDismiss();

        btnBackToMain.setOnClickListener(v -> navController.navigate(R.id.mainFragment));
        popupOpenLibrary.setOnClickListener(v -> openSelectedLibraryPage());
        popupClose.setOnClickListener(v -> hidePopup());
        new Handler().postDelayed(this::startStaggeredAnimation, 200);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart mapStarted=" + mapStarted + " popupVisible=" + popupVisible + " executor=" + (executor == null ? "null" : (executor.isShutdown() ? "shutdown" : "alive")));
        startMapIfPossible();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        if (mapView != null) {
            try {
                mapView.onStop();
            } catch (SecurityException ignored) {
            }
        }
        try {
            MapKitFactory.getInstance().onStop();
        } catch (SecurityException ignored) {
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView selected=" + (selectedSpec == null ? "null" : selectedSpec.title) + " popupVisible=" + popupVisible);
        if (mapView != null && mapTapListener != null) {
            mapView.getMap().removeInputListener(mapTapListener);
        }
        super.onDestroyView();
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        mapStarted = false;
        selectedSpec = null;
        popupVisible = false;
    }

    private void setViewsAlphaZero() {
        View title = rootView.findViewById(R.id.tv_map_title);
        if (title != null) title.setAlpha(0f);
        View cardMap = rootView.findViewById(R.id.card_map_placeholder);
        if (cardMap != null) cardMap.setAlpha(0f);
        if (btnBackToMain != null) btnBackToMain.setAlpha(0f);
        if (pointPopup != null) pointPopup.setVisibility(View.GONE);
    }

    private void startStaggeredAnimation() {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());

        View title = rootView.findViewById(R.id.tv_map_title);
        ObjectAnimator titleAnim = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f);
        titleAnim.setDuration(500);
        titleAnim.setStartDelay(100);

        View cardMap = rootView.findViewById(R.id.card_map_placeholder);
        ObjectAnimator mapAnim = ObjectAnimator.ofFloat(cardMap, "alpha", 0f, 1f);
        mapAnim.setDuration(500);
        mapAnim.setStartDelay(200);

        ObjectAnimator backAnim = ObjectAnimator.ofFloat(btnBackToMain, "alpha", 0f, 1f);
        backAnim.setDuration(400);
        backAnim.setStartDelay(50);

        animSet.play(titleAnim).with(mapAnim).with(backAnim);
        animSet.start();
    }

    private void setupMap() {
        if (mapView == null) return;

        Log.d(TAG, "setupMap start");
        Map map = mapView.getMap();
        map.move(new CameraPosition(ORLOVSKAYA_CENTER, MAP_ZOOM, 0f, 0f),
                new Animation(Animation.Type.SMOOTH, 0.8f), null);

        ensureExecutor().execute(() -> {
            try {
                Log.d(TAG, "map worker started");
                CacheData cached = MapDataCache.load(requireContext());
                Log.d(TAG, "cache load=" + (cached == null ? "null" : ("shapes=" + cached.shapes.size() + ", points=" + cached.points.size())));
                if (cached == null || cached.shapes.isEmpty() || cached.points.isEmpty()) {
                    Log.d(TAG, "cache miss, building map data");
                    cached = buildAndCacheMapData();
                } else {
                    Log.d(TAG, "cache hit, skipping OSM fetch");
                }
                CacheData finalCached = cached;
                requireActivity().runOnUiThread(() -> drawMap(finalCached));
            } catch (Exception e) {
                Log.e(TAG, "setupMap failed", e);
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    if (loadingOverlay instanceof TextView) {
                        ((TextView) loadingOverlay).setText("Не удалось загрузить данные OSM");
                    }
                    if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private ExecutorService ensureExecutor() {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    private void setupMapTapDismiss() {
        if (mapView == null) return;
        mapTapListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                TapMatch nearest = findNearestPoint(point);
                Log.d(TAG, "map tap point=" + point
                        + " popupVisible=" + popupVisible
                        + " selected=" + (selectedSpec == null ? "null" : selectedSpec.title)
                        + " currentPoints=" + currentPoints.size()
                        + " nearest=" + (nearest == null ? "null" : nearest.entry.title)
                        + " nearestDistancePx=" + (nearest == null ? "n/a" : String.format(Locale.US, "%.1f", nearest.distancePx))
                        + " thresholdPx=" + POINT_TAP_THRESHOLD_PX);
                if (nearest != null && nearest.distancePx <= POINT_TAP_THRESHOLD_PX) {
                    Log.d(TAG, "map tap opening popup title=" + nearest.entry.title + " type=" + nearest.entry.type + " districtId=" + nearest.entry.districtId);
                    showPopup(nearest.entry);
                    return;
                }
                Log.d(TAG, "map tap dismiss no nearby point");
                hidePopup();
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            }
        };
        mapView.getMap().addInputListener(mapTapListener);
    }

    private void startMapIfPossible() {
        if (mapStarted || mapView == null) {
            Log.d(TAG, "startMapIfPossible skipped mapStarted=" + mapStarted + " mapView=" + (mapView != null));
            return;
        }

        boolean fineGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "startMapIfPossible permissions fine=" + fineGranted + " coarse=" + coarseGranted);

        if (!fineGranted && !coarseGranted) {
            if (locationPermissionLauncher != null) {
                Log.d(TAG, "requesting location permissions");
                locationPermissionLauncher.launch(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
            return;
        }

        try {
            mapView.onStart();
            MapKitFactory.getInstance().onStart();
            mapStarted = true;
            Log.d(TAG, "MapKit started");
        } catch (SecurityException ignored) {
            Log.w(TAG, "MapKit start blocked by SecurityException", ignored);
        }
    }

    private CacheData buildAndCacheMapData() throws Exception {
        Log.d(TAG, "buildAndCacheMapData start");
        CacheData data = new CacheData();

        for (Entry<String, String> entry : BOUNDARY_QUERIES.entrySet()) {
            try {
                Log.d(TAG, "fetch boundary key=" + entry.getKey() + " query=" + entry.getValue());
                GeoShape fetched = fetchShape(entry.getValue());
                MapShape shape = new MapShape();
                shape.rings.addAll(fetched.rings);
                data.shapes.put(entry.getKey(), shape);
                Log.d(TAG, "boundary fetched key=" + entry.getKey() + " rings=" + shape.rings.size());
            } catch (Exception boundaryError) {
                if ("oblast".equals(entry.getKey())) {
                    Log.e(TAG, "oblast boundary fetch failed", boundaryError);
                    throw boundaryError;
                }
                Log.w(TAG, "optional boundary fetch failed key=" + entry.getKey(), boundaryError);
            }
        }

        for (PointSpec spec : POINTS) {
            data.points.add(new MapPointEntry(spec.districtId, spec.title, spec.description, spec.point, spec.type));
        }

        MapDataCache.save(requireContext(), data);
        Log.d(TAG, "buildAndCacheMapData saved shapes=" + data.shapes.size() + " points=" + data.points.size());
        return data;
    }

    private void drawMap(CacheData cached) {
        if (mapView == null) return;

        Log.d(TAG, "drawMap shapes=" + cached.shapes.size() + " points=" + cached.points.size());
        cachedMapData = cached;
        currentPoints = new ArrayList<>(cached.points);
        MapObjectCollection objects = mapView.getMap().getMapObjects();
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);

        GeoShape oblast = toGeoShape(cached.shapes.get("oblast"));
        if (oblast != null) addGeoShape(objects, oblast, 0x22009EAE, 0xFF007C91, 4f);

        int[] fills = {0x1AFF9800, 0x1A4CAF50, 0x1A3F51B5, 0x1AE91E63, 0x1A9C27B0};
        int[] strokes = {0xFFE65100, 0xFF2E7D32, 0xFF1A237E, 0xFFAD1457, 0xFF6A1B9A};
        int districtIndex = 0;
        for (Entry<String, String> entry : BOUNDARY_QUERIES.entrySet()) {
            if ("oblast".equals(entry.getKey())) continue;
            GeoShape shape = toGeoShape(cached.shapes.get(entry.getKey()));
            if (shape != null) {
                addGeoShape(objects, shape, fills[districtIndex % fills.length], strokes[districtIndex % strokes.length], 2.5f);
                districtIndex++;
            }
        }

        addPoints(objects, cached.points);
    }

    private void addGeoShape(MapObjectCollection objects, GeoShape shape, int fillColor, int strokeColor, float strokeWidth) {
        for (List<Point> ring : shape.rings) {
            if (ring.size() < 3) continue;
            List<Point> closed = new ArrayList<>(ring);
            if (!samePoint(closed.get(0), closed.get(closed.size() - 1))) {
                closed.add(closed.get(0));
            }
            PolygonMapObject polygon = objects.addPolygon(new Polygon(new LinearRing(closed), Collections.emptyList()));
            polygon.setFillColor(fillColor);
            polygon.setStrokeColor(strokeColor);
            polygon.setStrokeWidth(strokeWidth);
        }
    }

    private void addPoints(MapObjectCollection objects, List<MapPointEntry> placedPoints) {
        for (MapPointEntry placed : placedPoints) {
            PlacemarkMapObject placemark = objects.addPlacemark(placed.point);
            placemark.setOpacity(1f);
            placemark.setZIndex(1000f);
            placemark.setIcon(ImageProvider.fromBitmap(buildMarkerBitmap(colorForType(placed.type))));
            placemark.setIconStyle(new IconStyle().setScale(1.35f).setAnchor(new PointF(0.5f, 0.5f)));
            placemark.addTapListener((mapObject, point) -> {
                Log.d(TAG, "placemark tap title=" + placed.title
                        + " districtId=" + placed.districtId
                        + " type=" + placed.type
                        + " point=" + placed.point
                        + " tapPoint=" + point
                        + " popupVisible=" + popupVisible
                        + " selected=" + (selectedSpec == null ? "null" : selectedSpec.title));
                showPopup(placed);
                return true;
            });
        }
    }

    private void showPopup(MapPointEntry placed) {
        Log.d(TAG, "showPopup title=" + placed.title + " districtId=" + placed.districtId + " type=" + placed.type + " popupView=" + (pointPopup != null));
        selectedSpec = new PointSpec(placed.districtId, placed.title, placed.description, placed.point, placed.type);
        popupVisible = true;
        if (popupTitle != null) popupTitle.setText(placed.title);
        if (popupDescription != null) popupDescription.setText(placed.description);
        if (pointPopup != null) pointPopup.setVisibility(View.VISIBLE);
    }

    private void hidePopup() {
        Log.d(TAG, "hidePopup selected=" + (selectedSpec == null ? "null" : selectedSpec.title) + " popupVisible=" + popupVisible);
        selectedSpec = null;
        popupVisible = false;
        if (pointPopup != null) {
            pointPopup.setVisibility(View.GONE);
        }
    }

    private TapMatch findNearestPoint(@NonNull Point tapPoint) {
        if (mapView == null) {
            return null;
        }
        ScreenPoint tapScreen = mapView.getMapWindow().worldToScreen(tapPoint);
        if (tapScreen == null) {
            Log.d(TAG, "findNearestPoint tapScreen=null tap=" + tapPoint);
            return null;
        }
        TapMatch nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (MapPointEntry candidate : currentPoints) {
            ScreenPoint candidateScreen = mapView.getMapWindow().worldToScreen(candidate.point);
            if (candidateScreen == null) {
                continue;
            }
            double distance = distancePixels(tapScreen, candidateScreen);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = new TapMatch(candidate, distance);
            }
        }
        if (nearest != null) {
            Log.d(TAG, "findNearestPoint tap=" + tapPoint + " nearest=" + nearest.entry.title + " distancePx=" + String.format(Locale.US, "%.1f", bestDistance));
        } else {
            Log.d(TAG, "findNearestPoint tap=" + tapPoint + " no candidates");
        }
        return nearest;
    }

    private double distancePixels(@NonNull ScreenPoint a, @NonNull ScreenPoint b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.hypot(dx, dy);
    }

    private void openSelectedLibraryPage() {
        Log.d(TAG, "openSelectedLibraryPage selected=" + (selectedSpec == null ? "null" : selectedSpec.title));
        if (selectedSpec == null) {
            Log.w(TAG, "openSelectedLibraryPage aborted: selectedSpec null");
            return;
        }
        int districtId = districtRoomId(selectedSpec.districtId);
        if (districtId <= 0) {
            Log.w(TAG, "openSelectedLibraryPage aborted: districtId not found for " + selectedSpec.districtId);
            return;
        }
        Bundle args = new Bundle();
        args.putString("district_id", selectedSpec.districtId);
        Log.d(TAG, "navigate districtDetailFragment roomId=" + districtId);
        navController.navigate(R.id.districtDetailFragment, args);
    }

    private int districtRoomId(String districtId) {
        List<DistrictItem> districts = DistrictRepository.getDistricts();
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).getId().equals(districtId)) {
                return i + 1;
            }
        }
        return -1;
    }

    private GeoShape fetchShape(String query) throws Exception {
        String url = String.format(Locale.US, "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&polygon_geojson=1&q=%s",
                java.net.URLEncoder.encode(query, StandardCharsets.UTF_8.name()));
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Codex/1.0");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line);
            JSONArray items = new JSONArray(json.toString());
            if (items.length() == 0) throw new IllegalStateException("OSM boundary not found for " + query);
            JSONObject first = items.getJSONObject(0);
            return parseGeoShape(first.getJSONObject("geojson"));
        } finally {
            connection.disconnect();
        }
    }

    private GeoShape parseGeoShape(JSONObject geojson) throws Exception {
        String type = geojson.getString("type");
        GeoShape shape = new GeoShape();
        if ("Polygon".equalsIgnoreCase(type)) {
            shape.rings.add(parseRing(geojson.getJSONArray("coordinates").getJSONArray(0)));
        } else if ("MultiPolygon".equalsIgnoreCase(type)) {
            JSONArray polygons = geojson.getJSONArray("coordinates");
            for (int i = 0; i < polygons.length(); i++) {
                shape.rings.add(parseRing(polygons.getJSONArray(i).getJSONArray(0)));
            }
        } else {
            throw new IllegalStateException("Unsupported GeoJSON type: " + type);
        }
        return shape;
    }

    private List<Point> parseRing(JSONArray ringArray) throws Exception {
        List<Point> ring = new ArrayList<>();
        for (int i = 0; i < ringArray.length(); i++) {
            JSONArray pair = ringArray.getJSONArray(i);
            ring.add(new Point(pair.getDouble(1), pair.getDouble(0)));
        }
        return ring;
    }

    private boolean samePoint(Point a, Point b) {
        return Double.compare(a.getLatitude(), b.getLatitude()) == 0
                && Double.compare(a.getLongitude(), b.getLongitude()) == 0;
    }

    private static final class GeoShape {
        final List<List<Point>> rings = new ArrayList<>();
    }

    private static final class PointSpec {
        final String districtId;
        final String title;
        final String description;
        final Point point;
        final String type;

        PointSpec(String districtId, String title, String description, Point point, String type) {
            this.districtId = districtId;
            this.title = title;
            this.description = description;
            this.point = point;
            this.type = type;
        }
    }

    private static final class TapMatch {
        final MapPointEntry entry;
        final double distancePx;

        TapMatch(MapPointEntry entry, double distancePx) {
            this.entry = entry;
            this.distancePx = distancePx;
        }
    }

    private static final class PlacedPoint {
        final PointSpec spec;
        final Point point;

        PlacedPoint(PointSpec spec, Point point) {
            this.spec = spec;
            this.point = point;
        }
    }

    private GeoShape toGeoShape(MapShape cachedShape) {
        if (cachedShape == null) {
            return null;
        }
        GeoShape shape = new GeoShape();
        shape.rings.addAll(cachedShape.rings);
        return shape;
    }

    private int colorForType(String type) {
        if ("district".equals(type)) return 0xFF1E88E5;
        if ("nature".equals(type)) return 0xFF2E7D32;
        if ("culture".equals(type)) return 0xFF8E24AA;
        if ("memorial".equals(type)) return 0xFFB71C1C;
        if ("river".equals(type)) return 0xFF0288D1;
        if ("forest".equals(type)) return 0xFF33691E;
        if ("road".equals(type)) return 0xFFF9A825;
        if ("church".equals(type)) return 0xFF6D4C41;
        if ("photo".equals(type)) return 0xFF455A64;
        return 0xFF009688;
    }

    private Bitmap buildMarkerBitmap(int color) {
        int size = 72;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint white = new Paint(Paint.ANTI_ALIAS_FLAG);
        white.setColor(0xFFFFFFFF);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, white);

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setColor(color);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 8f, fill);

        Paint inner = new Paint(Paint.ANTI_ALIAS_FLAG);
        inner.setColor(0xFFFFFFFF);
        canvas.drawCircle(size / 2f, size / 2f, 8f, inner);
        return bitmap;
    }
}
