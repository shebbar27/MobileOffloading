package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final ArrayList<String> PERMISSIONS = BluetoothHandler.getBluetoothPermissions();
    public ArrayAdapter<String> nearbyDevicesAdapter;
    private BluetoothHandler bluetoothHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        nearbyDevicesAdapter=new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        this.bluetoothHandler = new BluetoothHandler(this);
        this.registerOnClickListenerCallBackForButtons();
        this.requestPermissions();
        this.initializeDevicesListView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case scan_button:
                //this.bluetoothHandler.scanForAvailableBluetoothDevices();
                startDiscovery();
                break;

            case next_button:
                this.bluetoothHandler.connectWithSelectedBluetoothDevices();
                break;
        }
    }

    private void startDiscovery() {
        Toast.makeText(this, "Starting Discovery", Toast.LENGTH_SHORT).show();
            DiscoveryOptions discoveryOptions =
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
            Nearby.getConnectionsClient(getApplicationContext())
                    .startDiscovery("OFFLOADINGSERVICE", new EndpointDiscoveryCallback() {
                        @Override
                        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                            Log.d("A", "onEndpointFound: Found device"+ discoveredEndpointInfo.getEndpointName());
                            Toast.makeText(getApplicationContext(), "Found Device: "+discoveredEndpointInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
                            nearbyDevicesAdapter.add(discoveredEndpointInfo.getEndpointName());

                        }

                        @Override
                        public void onEndpointLost(@NonNull String s) {


                        }
                    }, discoveryOptions)
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We're discovering!
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // We're unable to start discovering.
                            });
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && this.areAllPermissionsGranted()) {
            this.bluetoothHandler.enableBluetooth();
        } else {
            this.bluetoothHandler.checkForBluetoothConnectPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.bluetoothHandler.checkForBluetoothEnabledAndDisplayAlert(requestCode, resultCode);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.bluetoothHandler.onDestroy();
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                PERMISSIONS.toArray(new String[0]),
                REQUEST_PERMISSIONS_CODE);
    }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(scan_button);
                    add(connect);
                    add(exit_button);
                }}
        );
    }

    private void initializeDevicesListView() {
        ListView devicesListView = findViewById(devices_listview);
        devicesListView.setAdapter(nearbyDevicesAdapter);
//        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                 String selected=(String)adapterView.getItemAtPosition(i);
//                 Toast.makeText(MasterActivity.this, "Selected: " +selected, Toast.LENGTH_SHORT).show();
//                Nearby.getConnectionsClient(getApplicationContext())
//                        .requestConnection("MASTER", selected, new ConnectionLifecycleCallback() {
//                            @Override
//                            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
//
//                            }
//
//                            @Override
//                            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
//
//                            }
//
//                            @Override
//                            public void onDisconnected(@NonNull String s) {
//
//                            }
//                        })
//                        .addOnSuccessListener(
//                                (Void unused) -> {
//                                    // We successfully requested a connection. Now both sides
//                                    // must accept before the connection is established.
//                                })
//                        .addOnFailureListener(
//                                (Exception e) -> {
//                                    // Nearby Connections failed to request the connection.
//                                });
//            }
//        });
    }

    private boolean areAllPermissionsGranted() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}