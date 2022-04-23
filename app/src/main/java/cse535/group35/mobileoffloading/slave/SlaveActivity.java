package cse535.group35.mobileoffloading.slave;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.UUID;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.BluetoothPermissionsManager;
import cse535.group35.mobileoffloading.R;

public class SlaveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final int REQUEST_ENABLE_BT = 137;
    private String nearbyServiceId;
    private String slaveName;
    private boolean isAdvertisingStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave);

        this.registerOnClickListenerCallBackForButtons();
        this.nearbyServiceId = getString(R.string.nearbyServiceId);
        this.setDeviceNameAndLabel();
        this.setDeviceInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case advertise_button:
                this.startOrStopAdvertising();
                break;
            case slave_back_button:
                this.returnToMainActivity();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && BluetoothPermissionsManager.areAllPermissionsGranted(this)) {
            BluetoothPermissionsManager.checkForBluetoothEnabledAndTakeAction(this, REQUEST_ENABLE_BT);
        } else {
            BluetoothPermissionsManager.requestPermissions(this, REQUEST_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            BluetoothPermissionsManager.checkForBluetoothEnabledAndDisplayAlert(this,
                    REQUEST_ENABLE_BT,
                    resultCode);
        }
    }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(advertise_button);
                    add(slave_back_button);
                }}
        );
    }

    private void setDeviceNameAndLabel() {
        this.slaveName = String.format("%1$s %2$s",
                getString(slave),
                UUID.randomUUID().toString().substring(0, 4));
        TextView slaveNameTextView = findViewById(slave_name_textView);
        slaveNameTextView.setText(String.format(getString(device_name), this.slaveName));
    }

    private void setDeviceInfo() {
        TextView batteryLevelTextView = findViewById(battery_level_textView);
        batteryLevelTextView.setText(String.format(getString(battery_level), this.getCurrentBatteryLevel()));
        TextView locationTextView = findViewById(location_textView);
        String[] locationData = this.getGPSLocationCoordinates();
        locationTextView.setText(String.format(getString(location), locationData[0], locationData[1]));
    }

    private void startOrStopAdvertising()
    {
        BluetoothPermissionsManager.checkForBluetoothEnabledAndTakeAction(this,
                REQUEST_ENABLE_BT);

        Button startStopAdvertising = findViewById(advertise_button);
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        ConnectionsClient connectionsClient = Nearby.getConnectionsClient(getApplicationContext());

        if(this.isAdvertisingStarted) {
            connectionsClient.stopAdvertising();
            startStopAdvertising.setText(start_advertising);
            AppUtility.createAndDisplayToast(this,
                    "Stopped Advertising");
            this.isAdvertisingStarted = false;
        } else {
            Nearby.getConnectionsClient(getApplicationContext())
                    .startAdvertising(
                            this.slaveName, this.nearbyServiceId, new SlaveConnectionLifecycleCallback(getApplicationContext()), advertisingOptions)
                    .addOnSuccessListener(
                            (Void unused) -> AppUtility.createAndDisplayToast(this,
                                    "Started Advertising"))
                    .addOnFailureListener(
                            (Exception e) ->  AppUtility.createAndDisplayToast(this,
                                    "Failed to start Advertising: "+e.getMessage()));
            startStopAdvertising.setText(stop_advertising);
            this.isAdvertisingStarted = true;
        }
    }

    private String getCurrentBatteryLevel() {
        BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
        return Integer.toString(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    }

    private String[] getGPSLocationCoordinates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String[] locationData = new String[]{"NaN", "NaN"};
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            this.turnOnGPS();
        } else {
            locationData = this.getLocation(locationManager);
        }

        return locationData;
    }

    private void turnOnGPS() {
        AppUtility.createTurnOnGPSAlert(this,
                (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)),
                (dialog, which) ->
                {
                    dialog.cancel();
                    AppUtility.createAndDisplayToast(this,
                            "GPS was not enabled on device. Hence location data cannot be updated!",
                            Toast.LENGTH_LONG);
                });
    }

    @SuppressLint("MissingPermission")
    private String[] getLocation(LocationManager locationManager) {
        String[] locationData = new String[] {"NaN", "NaN"};
        if(!BluetoothPermissionsManager.areAllPermissionsGranted(this)) {
            BluetoothPermissionsManager.requestPermissions(this,
                    REQUEST_PERMISSIONS_CODE);
        }
        else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                locationData[0] = Double.toString(locationGPS.getLatitude());
                locationData[1] = Double.toString(locationGPS.getLatitude());
                AppUtility.createAndDisplayToast(this, "Your Location: " + "\n" + "Latitude: " + locationData[0] + "\n" + "Longitude: " + locationData[1]);
            } else {
                AppUtility.createAndDisplayToast(this, "Unable to find location. It will be updated soon");
            }
        }

        return locationData;
    }

    private void returnToMainActivity() {
        AppUtility.finishAndCloseCurrentActivityWithDefaultAlert(this);
    }
}