package com.farnek.locationpicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.model.LocationHistoryModel.DateModel;
import com.farnek.locationpicker.viewholder.LocationListViewHolder;

import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListViewHolder> {

    private Context context;
    private List<DateModel> locationList;

    public LocationListAdapter(Context context, List<DateModel> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_list_view, parent, false);
        return new LocationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListViewHolder holder, int position) {
        DateModel dateModel = locationList.get(position);
        if (dateModel != null) {
            if (dateModel.getLatitude() != null) {
                holder.tvLatitude.setText("Latitude : " + dateModel.getLatitude());
            }
            if (dateModel.getLongitude() != null) {
                holder.tvLongitude.setText("Longitude : " + dateModel.getLongitude());
            }
            if (dateModel.getTimestamp() != null) {
                holder.tvDate.setText(dateModel.getTimestamp());
            }

        }
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
