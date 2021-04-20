package com.farnek.locationpicker.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.farnek.locationpicker.R;


public class LocationListViewHolder extends RecyclerView.ViewHolder {

    public final CardView cvList;
    public final TextView tvDate;
    public final TextView tvLatitude;
    public final TextView tvLongitude;

    public LocationListViewHolder(@NonNull View itemView) {
        super(itemView);
        cvList = (CardView) itemView.findViewById(R.id.cv_list);
        tvLatitude = (TextView) itemView.findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) itemView.findViewById(R.id.tv_longitude);
        tvDate = (TextView) itemView.findViewById(R.id.tv_date);
    }
}
