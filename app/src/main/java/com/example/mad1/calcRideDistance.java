package com.example.mad1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class calcRideDistance extends AppCompatActivity implements OnMapReadyCallback{

    private TextView distanceView, startLocation, endLocation, departTV, destinationTV, totalCostTextView, bookingCapacityTextView;
    private Switch prebook_switch;
    private RequestQueue requestQueue;
    private PlacesClient placesClient;
    private MapView mapView;
    private GoogleMap gMap;
    private String userID;
    private TextView bookingTimeTextView;
    private static final double BASE_FARE = 3.00; // Base fare in RM
    private static final double RATE_PER_KILOMETER = 1; // Rate per kilometer in RM
    private String formattedTotalCost;
    private boolean isPassenger;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_ride_distance);

        if (!Places.isInitialized()) {
            String apiKey = ApiKeyManager.getGoogleMapsApiKey(this);
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);

        // Initialize TextViews
        startLocation = findViewById(R.id.startLocation);
        endLocation = findViewById(R.id.endLocation);
        departTV = findViewById(R.id.departTV);
        destinationTV = findViewById(R.id.destinationTV);
        distanceView = findViewById(R.id.textView4);
        totalCostTextView = findViewById(R.id.totalCostTextView);
        bookingCapacityTextView = findViewById(R.id.ETBookingCapacity);
        prebook_switch= findViewById(R.id.prebook_switch);

        // Setup autocomplete fragments
        setupAutoCompleteFragment(R.id.autocomplete_fragment_start, startLocation, departTV);
        setupAutoCompleteFragment(R.id.autocomplete_fragment_end, endLocation, destinationTV);

        bookingTimeTextView = findViewById(R.id.bookingTimeTextView);
        bookingCapacityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog();
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        bookingTimeTextView.setOnClickListener(v -> showDatePickerDialog());
        prebook_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(calcRideDistance.this, "You are booking this booking as a passenger.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(calcRideDistance.this, "You are booking this booking as a driver.", Toast.LENGTH_LONG).show();
                }

                isPassenger = isChecked;
            }
        });
    }

    private void showNumberPickerDialog() {
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(7);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Capacity");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bookingCapacityTextView.setText(String.valueOf(numberPicker.getValue()));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setView(numberPicker);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    Calendar newCalendar = (Calendar) calendar.clone();
                    newCalendar.set(Calendar.YEAR, yearSelected);
                    newCalendar.set(Calendar.MONTH, monthOfYear);
                    newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    newCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    newCalendar.set(Calendar.MINUTE, 0);
                    newCalendar.set(Calendar.SECOND, 0);
                    newCalendar.set(Calendar.MILLISECOND, 0);

                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.HOUR_OF_DAY, 0);
                    now.set(Calendar.MINUTE, 0);
                    now.set(Calendar.SECOND, 0);
                    now.set(Calendar.MILLISECOND, 0);

                    if (newCalendar.before(now)) {
                        // The selected date is in the past
                        Toast.makeText(this, "Please choose current or future date.", Toast.LENGTH_LONG).show();
                    } else {
                        showTimePickerDialog(newCalendar);
                    }
                },
                year, month, day);

        datePickerDialog.show();

    }


    private void showTimePickerDialog(Calendar dateCalendar) {
        int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = dateCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (timeView, hourOfDay, minuteOfHour) -> {
                    Calendar newCalendar = (Calendar) dateCalendar.clone();
                    newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    newCalendar.set(Calendar.MINUTE, minuteOfHour);
                    newCalendar.set(Calendar.SECOND, 0);
                    newCalendar.set(Calendar.MILLISECOND, 0);

                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.SECOND, 0);
                    now.set(Calendar.MILLISECOND, 0);

                    if (newCalendar.before(now)) {
                        // The selected time is in the past
                        Toast.makeText(this, "Please choose current or future time.", Toast.LENGTH_LONG).show();
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                        bookingTimeTextView.setText(dateFormat.format(newCalendar.getTime()));
                    }
                },
                hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }


    private String calculateCost(double distanceKm) {
        BigDecimal baseFare = new BigDecimal(BASE_FARE);
        BigDecimal ratePerKilometer = new BigDecimal(RATE_PER_KILOMETER);
        BigDecimal distance = new BigDecimal(distanceKm);
        BigDecimal cost = baseFare.add(distance.multiply(ratePerKilometer));
        // Set scale to 2 decimal places without rounding
        return cost.setScale(2, RoundingMode.DOWN).toString();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable zoom controls on the map
        gMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable zoom gestures (pinch to zoom)
        gMap.getUiSettings().setZoomGesturesEnabled(true);

    }

    private void drawRouteOnMap(JSONObject route) {
        try {
            gMap.clear(); // Clear old markers, polylines, etc.

            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String encodedPath = overviewPolyline.getString("points");
            List<LatLng> path = decodePoly(encodedPath);

            PolylineOptions polylineOptions = new PolylineOptions().addAll(path).color(Color.RED);
            gMap.addPolyline(polylineOptions);

            // Setting bounds for the route
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : path) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // Padding in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            gMap.animateCamera(cu);
        } catch (JSONException e) {
            Log.e("drawRouteOnMap", "Failed to draw route on map", e);
        }
    }


    private void setupAutoCompleteFragment(int fragmentId, final TextView locationTextView, final TextView displayTextView) {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(fragmentId);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locationTextView.setText(place.getName());
                displayTextView.setText(place.getName());
                triggerDistanceCalculation();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e("PlacesApiError", "Autocomplete error: " + status.getStatusMessage());
                locationTextView.setText("Error: " + status.getStatusMessage());
            }
        });

        View autocompleteView = autocompleteFragment.getView();
        EditText editText = findAutocompleteEditText(autocompleteView);
        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    // Before text changed
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if (charSequence.length() == 0) {
                        // User cleared text, update your views or models as needed
                        locationTextView.setText("");
                        displayTextView.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // After text changed
                }
            });
        }

    }

    private EditText findAutocompleteEditText(View autocompleteView) {
        if (autocompleteView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) autocompleteView;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof EditText) {
                    return (EditText) child;
                } else if (child instanceof ViewGroup) {
                    EditText result = findAutocompleteEditText((ViewGroup) child);
                    if (result != null) return result;
                }
            }
        }
        return null; // EditText not found
    }

    private void triggerDistanceCalculation() {
        String origin = startLocation.getText().toString().trim();
        String destination = endLocation.getText().toString().trim();
        if (!origin.isEmpty() && !destination.isEmpty()) {
            getDirections(origin, destination);
        }
    }

    private void getDirections(String origin, String destination) {
        String apiKey = ApiKeyManager.getGoogleMapsApiKey(this);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin + "&destination=" + destination + "&key=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");

                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONArray legs = route.getJSONArray("legs");
                            JSONObject leg = legs.getJSONObject(0);
                            JSONObject distance = leg.getJSONObject("distance");
                            drawRouteOnMap(route);
                            distanceView.setText(distance.getString("text"));

                            double distanceKm = distance.getDouble("value")/1000;
                            formattedTotalCost = calculateCost(distanceKm);
                            totalCostTextView.setText("RM" + formattedTotalCost);

                        } else {
                            distanceView.setText("Route not found");
                            totalCostTextView.setText("RM0.00");
                        }

                    } catch (Exception e) {
                        Log.e("JsonParsingError", "Failed to parse the JSON response", e);
                        distanceView.setText("Failed to read path");
                        totalCostTextView.setText("RM0.00");
                    }
                }, error -> {
            Log.e("VolleyRequestError", "HTTP request failed", error);
            distanceView.setText("Request failed");
            totalCostTextView.setText("RM0.00");
        });
        requestQueue.add(jsonObjectRequest);

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void onConfirmButtonClick(View view) {
        try {
            // Check if start and end locations are filled
            String startLocationStr = startLocation.getText().toString().trim();
            String endLocationStr = endLocation.getText().toString().trim();
            if (startLocationStr.isEmpty() || endLocationStr.isEmpty()) {
                Toast.makeText(this, "Please make sure to select valid start and end locations.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if booking time is selected
            String bookingTimeStr = bookingTimeTextView.getText().toString();
            if ("Select Time".equals(bookingTimeStr)) {
                Toast.makeText(this, "Please select a booking time.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if distance is available
            String distance = distanceView.getText().toString().replaceAll("[^0-9.]", ""); // Regex to extract only numbers and decimal points
            if (distance.isEmpty()) {
                Toast.makeText(this, "Distance information is not available. Please make sure to select valid locations.", Toast.LENGTH_SHORT).show();
                return; // Exit the method early if distance is not available
            }

            String capacityStr = bookingCapacityTextView.getText().toString();
            if (capacityStr.isEmpty()){
                Toast.makeText(this, "Please enter capacity", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                int capacity;
                try {
                    capacity = Integer.parseInt(capacityStr);
                }catch (NumberFormatException e){
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (capacity<1){
                    Toast.makeText(this, "Capacity at least need to be 1", Toast.LENGTH_SHORT).show();
                    return;
                }
            }





            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking_table");
            String bookingId = databaseReference.push().getKey(); // Generate a unique key for a new booking entry

            if (bookingId != null) {
                Map<String, Object> bookingData = new HashMap<>();
                bookingData.put("booking_startlocation", startLocationStr);
                bookingData.put("booking_endlocation", endLocationStr);
                bookingData.put("booking_datetime", bookingTimeStr);
                bookingData.put("booking_distance", distance);
                bookingData.put("booking_ttlprice", formattedTotalCost);
                bookingData.put("booking_status", "active");
                bookingData.put("booking_capacity", capacityStr);
                bookingData.put("booking_leader", userID);
                if(isPassenger){
                    bookingData.put("booking_driver", "");
                }
                else{
                    bookingData.put("booking_driver", userID);
                }


                // Push data to Firebase
                databaseReference.child(bookingId).setValue(bookingData)
                        .addOnSuccessListener(aVoid -> {
                            if(isPassenger){
                                databaseReference.child(bookingId).child("booking_user").child(userID).child("booking_user_price").setValue(formattedTotalCost);
                            }
                            else{
                                databaseReference.child(bookingId).child("booking_user").child(userID).child("booking_user_price").setValue("");
                            }

                            Toast.makeText(calcRideDistance.this, "Booking data sent successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(calcRideDistance.this, MainActivity.class);
                            intent.putExtra("userId", userID);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(calcRideDistance.this, "Failed to send booking data: " + e.getMessage(), Toast.LENGTH_SHORT).show());


            }

        } catch (Exception e) {
            Log.e("ConfirmClickError", "Error in onConfirmButtonClick", e);
            Toast.makeText(this, "Error during booking confirmation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onBackButtonClick(View view) {
        finish();
    }
}
