package cse535.group35.mobileoffloading.master;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.appcompat.app.AppCompatActivity;

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
import cse535.group35.mobileoffloading.R;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {
    public ArrayAdapter<String> nearbyDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        nearbyDevicesAdapter=new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        this.registerOnClickListenerCallBackForButtons();
        this.initializeDevicesListView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case scan_button:
                startDiscovery();
                break;
            case master_back_button:
                ReturnToMainActivity();
                break;
        }
    }

    private void startDiscovery() {
        Toast.makeText(this, "Starting Discovery", Toast.LENGTH_SHORT).show();
            DiscoveryOptions discoveryOptions =
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
            Nearby.getConnectionsClient(getApplicationContext())
                    .startDiscovery(getResources().getString(nearbyServiceId), new MasterEndpointDiscoveryCallback(getApplicationContext(),nearbyDevicesAdapter), discoveryOptions)
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
        devicesListView.setAdapter(nearbyDevicesAdapter);
        devicesListView.setOnItemClickListener((adapterView, view, i, l) -> {
             String selected=(String)adapterView.getItemAtPosition(i);
             Toast.makeText(MasterActivity.this, "Selected: " +selected, Toast.LENGTH_SHORT).show();
            Nearby.getConnectionsClient(getApplicationContext())
                    .requestConnection("MASTER", selected, new MasterConnectionLifecycleCallback(getApplicationContext()))
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We successfully requested a connection. Now both sides
                                // must accept before the connection is established.
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // Nearby Connections failed to request the connection.
                            });
        });
    }

    private void ReturnToMainActivity() {
        AppUtility.finishAndCloseCurrentActivityWithDefaultAlert(this);
    }
}