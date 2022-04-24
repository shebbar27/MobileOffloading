package cse535.group35.mobileoffloading.master;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.AppPermissionsManager;
import cse535.group35.mobileoffloading.MainActivity;
import cse535.group35.mobileoffloading.R;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {
    public ArrayAdapter<String> nearbyDevicesAdapter;
    public ArrayAdapter<String> connectedDevicesAdaptor;

    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final int REQUEST_ENABLE_BT = 137;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        nearbyDevicesAdapter=new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        connectedDevicesAdaptor=new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        this.registerOnClickListenerCallBackForButtons();
        this.initializeDevicesListView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case scan_button:
                this.startDiscovery();
                break;
            case master_back_button:
                this.returnToMainActivity();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && AppPermissionsManager.areAllPermissionsGranted(this)) {
            AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(this, REQUEST_ENABLE_BT);
        } else {
            AppPermissionsManager.requestAllPermissions(this, REQUEST_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            AppPermissionsManager.checkForBluetoothEnabledAndDisplayAlert(this,
                    REQUEST_ENABLE_BT,
                    resultCode);
        }
    }

    private void startDiscovery() {
        AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(this,
                REQUEST_ENABLE_BT);
        AppUtility.createAndDisplayToast(this, "Starting Discovery");
        AlertDialog.Builder builder = new AlertDialog.Builder(MasterActivity.this);
        builder.setTitle("Choose a device");
        // add a list


        builder.setAdapter(nearbyDevicesAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedDevice=nearbyDevicesAdapter.getItem(i);
                AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(MasterActivity.this,
                        REQUEST_ENABLE_BT);
                Nearby.getConnectionsClient(getApplicationContext())
                        .requestConnection("MASTER", selectedDevice, new MasterConnectionLifecycleCallback(MasterActivity.this,connectedDevicesAdaptor))
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    dialogInterface.cancel();
                                    Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // Nearby Connections failed to request the connection.
                                });
            }
        });


        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
            DiscoveryOptions discoveryOptions =
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
            Nearby.getConnectionsClient(getApplicationContext())
                    .startDiscovery(getResources().getString(nearbyServiceId), new MasterEndpointDiscoveryCallback(this, nearbyDevicesAdapter), discoveryOptions)
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We're discovering!
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // We're unable to start discovering.
                            });
        }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(scan_button);
                    add(master_back_button);
                }}
        );
    }

    private void initializeDevicesListView() {
        ListView devicesListView = findViewById(devices_listview);
        devicesListView.setAdapter(connectedDevicesAdaptor);

    }

    private void returnToMainActivity() {
        AppUtility.finishAndCloseCurrentActivityWithDefaultAlert(this);
    }
}