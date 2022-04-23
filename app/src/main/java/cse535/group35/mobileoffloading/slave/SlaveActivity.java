package cse535.group35.mobileoffloading.slave;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Strategy;

import java.text.MessageFormat;
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
        BluetoothPermissionsManager.requestPermissions(this, REQUEST_PERMISSIONS_CODE);
        this.setDeviceNameAndLabel();
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

    private void returnToMainActivity() {
        AppUtility.finishAndCloseCurrentActivityWithDefaultAlert(this);
    }
}