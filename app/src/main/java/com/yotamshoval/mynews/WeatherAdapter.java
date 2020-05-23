package com.yotamshoval.mynews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private List<Weather> weathers;

    public WeatherAdapter(List<Weather> weathers) {
        this.weathers = weathers;
    }


    public class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView locationTv;
        TextView areaTv;
        TextView degreesTv;
        TextView longitudeTv, latitudeTv;
        ImageView weatherKindIv;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            locationTv = itemView.findViewById(R.id.location_tv);
            areaTv = itemView.findViewById(R.id.area_tv);
            degreesTv = itemView.findViewById(R.id.degrees_tv);
            longitudeTv = itemView.findViewById(R.id.longitude_tv);
            latitudeTv = itemView.findViewById(R.id.latitude_tv);
            weatherKindIv = itemView.findViewById(R.id.current_weather_image_view);
        }
    }

    @NonNull
    @Override
    public WeatherAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(view);
        return weatherViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.WeatherViewHolder holder, int position) {
        Weather weather = weathers.get(position);
        holder.locationTv.setText(weather.getLocation());
        holder.areaTv.setText(weather.getArea());
        holder.degreesTv.setText(weather.getDegrees());
        holder.latitudeTv.setText(weather.getLatitude());
        holder.longitudeTv.setText(weather.getLongitude());
        holder.weatherKindIv.setImageResource(weather.getWeatherKind());
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }
}
