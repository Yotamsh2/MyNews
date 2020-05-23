package com.yotamshoval.mynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

public class WeatherFragment extends Fragment {     //DEPRECATED

    public static final String TAG = "WeatherFragment";
    public static final String WHEATER_API_KEY = "a1f8913fb4d6b93ff3e7166c3fa0260f";
    public static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    double wayLatitude, wayLongitude;
    String town, adminArea;
    String weatherDescription = null;
    String temperature = null;

    public static WeatherFragment newInstance(String town, String adminArea, double wayLatitude, double wayLongitude) {
        WeatherFragment weatherFragment = new WeatherFragment();

        Bundle bundle = new Bundle();
        bundle.putString("town", town);
        bundle.putString("adminArea", adminArea);
        bundle.putDouble("wayLatitude", wayLatitude);
        bundle.putDouble("wayLongitude", wayLongitude);
        weatherFragment.setArguments(bundle);

        return weatherFragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_fragment, container, false);


        wayLatitude = getArguments().getDouble("wayLatitude");
        wayLongitude = getArguments().getDouble("wayLongitude");
        town = getArguments().getString("town");
        adminArea = getArguments().getString("adminArea");

        Log.d("onCreateView town", "town: " + town + ", wayLatitude: " + wayLatitude); //CHECKING THAT WE'VE GOT TOWN AND ETC. (TO DELETE)


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.weather_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));


        final List<Weather> weathers = new ArrayList<>();

        //for example
        weathers.add(new Weather("" + town, "" + adminArea, "null", "" + wayLongitude, "" + wayLatitude, R.mipmap.ic_sunny_day));
        weathers.add(new Weather("Eilat", "eilat", "25c", "34.94821", "29.55805", R.mipmap.ic_sunny_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Ramat Gan", "Tel Aviv District", "25c", "34.81065", "32.08227", R.mipmap.ic_sunny_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Jerusalem", "Jerusalem", "25c", "35.21633", "31.76904", R.mipmap.ic_cloudy_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Beersheba", "South", "25c", "34.7913", "31.25181", R.mipmap.ic_partly_cloudy_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Jaffa", "Tel Aviv District", "25c", "34.2145", "30.2145", R.mipmap.ic_rainy_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Haifa", "Haifa", "25c", "34.9885", "32.81841", R.mipmap.ic_sunny_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Tiberias", "Lower Galilee", "25c", "35.53124", "32.79221", R.mipmap.ic_rainy_day)); //imagview(kind of the weather is null for example
        weathers.add(new Weather("Herzliya", "Sharon", "25c", "34.82536", "32.16627", R.mipmap.ic_rainy_day)); //imagview(kind of the weather is null for example


        for (int i = 0; i < weathers.size(); i++) {
            if (weathers.get(i).getLocation().equals(town)) {
                Log.d("location", "weathers.get(" + i + "): " + weathers.get(i).getLocation());
                Log.d("location", "weathers.get(0): " + weathers.get(0).getLocation());

                Collections.swap(weathers, i, 0);  //swap current town to be the colsest(first) element in the weatherRecycler

                Log.d("location", "weathers.get(" + i + "): " + weathers.get(i).getLocation()); //making sure the swap happened.
                Log.d("location", "weathers.get(0): " + weathers.get(0).getLocation());         //making sure the swap happened.
                break;
            }
        }

        final WeatherAdapter weatherAdapter = new WeatherAdapter(weathers);
        recyclerView.setAdapter(weatherAdapter);

        new RetrieveFeedTask(weathers, weatherAdapter).execute(); //getting weather description and degrees in AsyncTask(anonymous class)


        return rootView;
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        List<Weather> weathers;
        WeatherAdapter weatherAdapter;

        public RetrieveFeedTask(List<Weather> weathers, WeatherAdapter weatherAdapter) {
            this.weathers = weathers;
            this.weatherAdapter = weatherAdapter;
        }

        //I can to this doInBackground task in a for loop for each town, not only the town i've got.(which i want to present first- it's the town where the phone stays.
        @Override
        protected String doInBackground(Void... voids) {
            //getting the weather properties in the town where the phone stays.
            try {
                String paramsValue = town;
//                String urlString = "https://api.openweathermap.org/data/2.5/weather?q=Tel+Aviv-Yafo&appid=a1f8913fb4d6b93ff3e7166c3fa0260f";
                String urlString = API_URL + java.net.URLEncoder.encode(paramsValue, "UTF-8") + "&appid=" + WHEATER_API_KEY;
//                String urlString =API_URL + java.net.URLEncoder.encode(paramsValue, "UTF-8") + WHEATER_API_KEY;
                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    Log.d("stringBuilder: ", "" + stringBuilder);
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                Log.d("api_test", ": " + result);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    //getting the weather description at this exactly moment to determine the photo.
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) { //A needed if
                        JSONArray weatherJSONArray = jsonObject.getJSONArray("weather");

                        Log.d("api_test", "\nweatherJSONArray: " + weatherJSONArray.getString(0)); //TO DELETE
                        int descriptionIndex = weatherJSONArray.getString(0).indexOf("description") + 14;
                        int endIndex = weatherJSONArray.getString(0).indexOf("icon") - 3;
                        Log.d("api_test", "descriptionIndex: " + descriptionIndex); //TO DELETE
                        Log.d("api_test", "endIndex: " + endIndex); //TO DELETE
                        Log.d("api_test", "test result: " + weatherJSONArray.getString(0).substring(descriptionIndex, endIndex)); //TO DELETE

                        //getting the weather description
                        weatherDescription = weatherJSONArray.getString(0).substring(descriptionIndex, endIndex);

                        //getting temperature
                        JSONObject temperatureJSONObject = jsonObject.getJSONObject("main");
                        temperature = temperatureJSONObject.getString("feels_like");
                        Log.d("api_test", "temperature: " + temperature);

                        double tempKelvin = Double.parseDouble("" + temperature);
                        double tempCelcius = tempKelvin - 273.15; //Kelvin to Celcius formula
                        Log.d("api_test", "tempCelcius: " + (int) tempCelcius);

                        String townDegreesStr = String.valueOf((int) tempCelcius) + "c";

                        //sets the photo and degrees of the town I am at
                        for (int i = 0; i < weathers.size(); i++) {
                            if (weathers.get(i).getLocation().equals(town)) {
                                Log.d("api_test", "weathers.get(i).getLocation(): " + weathers.get(i).getWeatherKind());
                                switch (weatherDescription) {
                                    case ("clear sky"):
                                        Log.d("api_test", "inside case clear sky");
                                        weathers.get(i).setWeatherKind(R.mipmap.ic_sunny_day);
                                        weathers.get(i).setDegrees(townDegreesStr);
                                        weatherAdapter.notifyItemChanged(i);
                                        break;
                                    case ("few clouds"):
                                    case ("scattered clouds"):
                                        Log.d("api_test", "inside case few clouds");
                                        weathers.get(i).setWeatherKind(R.mipmap.ic_partly_cloudy_day);
                                        weathers.get(i).setDegrees(townDegreesStr);
                                        weatherAdapter.notifyItemChanged(i);
                                        break;
                                    case ("broken clouds"): //get a photo of broken clouds
                                        Log.d("api_test", "inside case broken clouds");
                                        weathers.get(i).setWeatherKind(R.mipmap.ic_cloudy_day);
                                        weathers.get(i).setDegrees(townDegreesStr);
                                        weatherAdapter.notifyItemChanged(i);
                                        break;
                                    case ("light intensity drizzle"):
                                    case ("shower rain"):
                                        Log.d("api_test", "inside case rainy");
                                        weathers.get(i).setWeatherKind(R.mipmap.ic_rainy_day);
                                        weathers.get(i).setDegrees(townDegreesStr);
                                        weatherAdapter.notifyItemChanged(i);
                                        break;
                                    case ("overcast clouds"):
                                        Log.d("api_test", "inside case rainy");
                                        weathers.get(i).setWeatherKind(R.mipmap.ic_cloudy_day);
                                        weathers.get(i).setDegrees(townDegreesStr);
                                        weatherAdapter.notifyItemChanged(i);
                                        break;

                                    default:
                                        Log.d("api_test", "inside case default");
                                        weathers.get(i).setWeatherKind(R.mipmap.ic_sunny_day);
                                        weathers.get(i).setDegrees(townDegreesStr);
                                        weatherAdapter.notifyItemChanged(i);
                                }
                            }
                            //I need to get the celcious also
                        }

                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("api_test", "result is null; " + result);
            }
        }

    }
}
