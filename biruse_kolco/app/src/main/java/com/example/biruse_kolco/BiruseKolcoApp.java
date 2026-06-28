package com.example.biruse_kolco;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class BiruseKolcoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(getString(R.string.yandex_mapkit_api_key));
        MapKitFactory.initialize(this);
    }
}
