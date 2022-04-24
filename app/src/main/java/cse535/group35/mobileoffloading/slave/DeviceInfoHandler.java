package cse535.group35.mobileoffloading.slave;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.Arrays;
import java.util.Locale;

import cse535.group35.mobileoffloading.AppUtility;

public class DeviceInfoHandler {

    private static final double[] defaultLocationData = new double[]{
            Double.NaN,
            Double.NaN,
    };

    public static void updateBatteryLevelTextView(Activity activity) {
        TextView batteryLevelTextView = activity.findViewById(battery_level_textView);
        batteryLevelTextView.setText(String.format(activity.getString(battery_level),
                getCurrentBatteryLevel(activity)));
    }

    public static void updateLocationTextView(Activity activity) {
        LocationManager locationManager = (LocationManager)activity.getSystemService(
                Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            turnOnGPS(activity);
        }
        else {
            updateLocationTextView(activity, locationManager);
        }
    }

    public static int getCurrentBatteryLevel(Activity activity) {
        BatteryManager batteryManager = (BatteryManager)activity.getSystemService(
                Activity.BATTERY_SERVICE);
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    @SuppressLint("MissingPermission")
    public static double[] getLastKnownLocation(Activity activity) {
        LocationManager locationManager = (LocationManager)activity.getSystemService(
                Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            turnOnGPS(activity);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                return new double[] {
                        location.getLatitude(),
                        location.getLongitude(),
                };
            }
        }

        return defaultLocationData;
    }

    private static void setLocationTextView(Activity activity,
                                            String[] locationData) {
        if(locationData == null) {
            locationData = new String[] {
                    Double.toString(defaultLocationData[0]),
                    Double.toString(defaultLocationData[0])
            };
        }

        TextView locationTextView = activity.findViewById(location_textView);
        locationTextView.setText(String.format(activity.getString(location), locationData[0], locationData[1]));
    }

    private static void turnOnGPS(Activity activity) {
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
    private static void updateLocationTextView(Activity activity,
                                               LocationManager locationManager) {
        setLocationTextView(activity, new String[] {
                Double.toString(defaultLocationData[0]),
                Double.toString(defaultLocationData[0])
        });

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
