package cse535.group35.mobileoffloading.slave;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.AppPermissionsManager;

public class DeviceInfoHandler {

    private static final String[] defaultLocationData = new String[] {
            Double.toString(Double.NaN),
            Double.toString(Double.NaN)
    };

    public static void updateBatteryLevelTextView(AppCompatActivity activity) {
        TextView batteryLevelTextView = activity.findViewById(battery_level_textView);
        batteryLevelTextView.setText(String.format(activity.getString(battery_level),
                getCurrentBatteryLevel(activity)));
    }

    private static String getCurrentBatteryLevel(AppCompatActivity activity) {
        BatteryManager batteryManager = (BatteryManager)activity.getSystemService(
                AppCompatActivity.BATTERY_SERVICE);
        return Integer.toString(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    }

    public static void updateLocationTextView(AppCompatActivity activity,
                                              int requestPermissionsCode) {
        LocationManager locationManager = (LocationManager)activity.getSystemService(
                Context.LOCATION_SERVICE);
        if(!AppPermissionsManager.areAllPermissionsGranted(activity)) {
            AppPermissionsManager.requestAllPermissions(activity,
                    requestPermissionsCode);
        }
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            turnOnGPS(activity);
        }
        else {
            updateLocationTextView(activity, locationManager);
        }
    }

    private static void setLocationTextView(AppCompatActivity activity,
                                            String[] locationData) {
        if(locationData == null) {
            locationData = defaultLocationData;
        }

        TextView locationTextView = activity.findViewById(location_textView);
        locationTextView.setText(String.format(activity.getString(location), locationData[0], locationData[1]));
    }

    private static void turnOnGPS(AppCompatActivity activity) {
        AppUtility.createTurnOnGPSAlert(activity,
                (dialog, which) -> activity.startActivity(
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)),
                (dialog, which) ->
                {
                    dialog.cancel();
                    AppUtility.createAndDisplayToast(activity,
                            "GPS was not enabled on device. Hence location data cannot be updated!",
                            Toast.LENGTH_LONG);
                });
    }

    private static void removeLocationUpdates(LocationManager locationManager,
                                              LocationListener locationListener) {
        locationManager.removeUpdates(locationListener);
    }

    @SuppressLint("MissingPermission")
    private static void updateLocationTextView(AppCompatActivity activity,
                                               LocationManager locationManager) {
        setLocationTextView(activity, defaultLocationData);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            setLocationTextView(activity,
                    new String[]{
                            String.format(Locale.US, "%.3f", location.getLatitude()),
                            String.format(Locale.US, "%.3f", location.getLongitude())
                    }
            );

            AppUtility.createAndDisplayToast(activity,
                    String.format(Locale.US,
                            "Location data successfully updated! Longitude: %1$.3f, Latitude: %2$.3f",
                            location.getLatitude(),
                            location.getLongitude()),
                    Toast.LENGTH_LONG);
        }
        else {
            AppUtility.createAndDisplayToast(activity,
                    "Location data is currently not available! It will be updated soon!");
            LocationListener locationListener = new LocationListener() {
                int count = 0;
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    setLocationTextView(activity,
                            new String[]{
                                    String.format(Locale.US, "%.3f", location.getLatitude()),
                                    String.format(Locale.US, "%.3f", location.getLongitude())
                            }
                    );

                    count++;
                    if(count > 3) {
                        removeLocationUpdates(locationManager, this);
                    }

                    AppUtility.createAndDisplayToast(activity,
                            String.format(Locale.US,
                                    "Location data successfully updated! Longitude: %1$.3f, Latitude: %2$.3f",
                                    location.getLatitude(),
                                    location.getLongitude()),
                            Toast.LENGTH_LONG);
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    200,
                    0,
                    locationListener
            );
        }
    }
}
