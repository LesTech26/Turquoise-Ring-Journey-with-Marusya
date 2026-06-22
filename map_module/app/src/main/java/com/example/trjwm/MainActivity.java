package com.example.trjwm;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.WindowCompat;

import com.example.trjwm.ui.DistrictDetailFragment;
import com.example.trjwm.ui.LibraryFragment;
import com.example.trjwm.ui.MapFragment;
import com.example.trjwm.ui.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements
        MapFragment.Listener,
        DistrictDetailFragment.Listener,
        LibraryFragment.Listener {

    private BottomNavigationView bottomNavigationView;
    private boolean suppressNavigationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(this::onBottomItemSelected);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            showRootFragment(new MapFragment(), R.id.nav_map);
        }
    }

    private boolean onBottomItemSelected(@NonNull MenuItem item) {
        if (suppressNavigationCallback) {
            return true;
        }
        if (item.getItemId() == R.id.nav_map) {
            showRootFragment(new MapFragment(), item.getItemId());
            return true;
        }
        if (item.getItemId() == R.id.nav_timeline) {
            showRootFragment(new TimelineFragment(), item.getItemId());
            return true;
        }
        if (item.getItemId() == R.id.nav_library) {
            showRootFragment(new LibraryFragment(), item.getItemId());
            return true;
        }
        return false;
    }

    private void showRootFragment(Fragment fragment, int selectedMenuId) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.mainContainer, fragment)
                .commit();
        if (bottomNavigationView.getSelectedItemId() != selectedMenuId) {
            suppressNavigationCallback = true;
            bottomNavigationView.setSelectedItemId(selectedMenuId);
            suppressNavigationCallback = false;
        }
    }

    private void openDetailFragment(String districtId) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.mainContainer, DistrictDetailFragment.newInstance(districtId))
                .addToBackStack("district")
                .commit();
    }

    private void openRootFragmentFromDetail(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDistrictSelected(String districtId) {
        openDetailFragment(districtId);
    }

    @Override
    public void onOpenTimeline() {
        openRootFragmentFromDetail(new TimelineFragment());
        if (bottomNavigationView.getSelectedItemId() != R.id.nav_timeline) {
            suppressNavigationCallback = true;
            bottomNavigationView.setSelectedItemId(R.id.nav_timeline);
            suppressNavigationCallback = false;
        }
    }

    @Override
    public void onOpenLibrary() {
        openRootFragmentFromDetail(new LibraryFragment());
        if (bottomNavigationView.getSelectedItemId() != R.id.nav_library) {
            suppressNavigationCallback = true;
            bottomNavigationView.setSelectedItemId(R.id.nav_library);
            suppressNavigationCallback = false;
        }
    }

    @Override
    public void onToggleCompleted(String districtId, boolean completed) {
        // Progress is stored locally in SharedPreferences by the fragment.
    }
}
