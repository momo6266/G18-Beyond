package com.example.mad1;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class ApiKeyManager {

    private static final String DEFAULT_MAPS_API_KEY = "YOUR_DEFAULT_API_KEY";

    public interface ApiKeyListener {
        void onApiKeyFetched(String apiKey);
    }

    public static void getGoogleMapsApiKey(Context context, ApiKeyListener listener) {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        // Fetch the remote config values
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Fetch successful, return the fetched value
                        String apiKey = remoteConfig.getString("api_key");
                        listener.onApiKeyFetched(apiKey);
                    } else {
                        // Fetch failed, return the default value
                        listener.onApiKeyFetched(DEFAULT_MAPS_API_KEY);
                    }
                });
    }
}



