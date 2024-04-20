package com.example.mad1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    private List<AutocompletePrediction> results;
    private PlacesClient placesClient;

    public PlacesAutoCompleteAdapter(Context context, PlacesClient placesClient) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.placesClient = placesClient;
    }

    @Override
    public int getCount() {
        return results == null ? 0 : results.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return results.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
        textView1.setText(getItem(position).getPrimaryText(null));
        textView2.setText(getItem(position).getSecondaryText(null));
        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                List<AutocompletePrediction> predictions = getAutocomplete(constraint);
                if (predictions != null) {
                    results.values = predictions;
                    results.count = predictions.size();
                } else {
                    results.values = null;
                    results.count = 0;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    PlacesAutoCompleteAdapter.this.results = (List<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            private List<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
                if (placesClient == null || constraint == null || constraint.length() == 0) {
                    return Collections.emptyList();
                }

                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(constraint.toString())
                        .build();

                Task<FindAutocompletePredictionsResponse> task = placesClient.findAutocompletePredictions(request);
                try {
                    Tasks.await(task, 60, TimeUnit.SECONDS);
                    if (task.isSuccessful()) {
                        FindAutocompletePredictionsResponse response = task.getResult();
                        return response.getAutocompletePredictions();
                    } else {
                        Log.e("AutocompleteTask", "Task failed with exception: " + task.getException());
                    }
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    Log.e("AutocompleteTask", "Task interrupted", e);
                }
                return Collections.emptyList();
            }
        };
    }
}
