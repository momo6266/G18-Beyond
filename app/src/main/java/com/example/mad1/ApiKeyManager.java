package com.example.mad1;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeyManager {

    public static String getGoogleMapsApiKey(Context context) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("secrets.properties");
            properties.load(inputStream);
            return properties.getProperty("MAPS_API_KEY");
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Consider handling error more gracefully
        }
    }
}
