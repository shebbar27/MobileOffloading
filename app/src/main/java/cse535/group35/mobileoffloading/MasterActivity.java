package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;

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

            case next_button:
                break;
        }
    }

    private void startDiscovery() {
        Toast.makeText(this, "Starting Discovery", Toast.LENGTH_SHORT).show();
            DiscoveryOptions discoveryOptions =
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
            Nearby.getConnectionsClient(getApplicationContext())
                    .startDiscovery("OFFLOADINGSERVICE", new EndpointDiscoveryCallback() {
                        @Override
                        public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                            Log.d("A", "onEndpointFound: Found device"+ discoveredEndpointInfo.getEndpointName());
                            Toast.makeText(getApplicationContext(), "Found Device: "+discoveredEndpointInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
                            nearbyDevicesAdapter.add(endPointId);

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
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 String selected=(String)adapterView.getItemAtPosition(i);
                 Toast.makeText(MasterActivity.this, "Selected: " +selected, Toast.LENGTH_SHORT).show();
                Nearby.getConnectionsClient(getApplicationContext())
                        .requestConnection("MASTER", selected, new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                                Toast.makeText(MasterActivity.this, "Connection Initiated with "+s, Toast.LENGTH_SHORT).show();
                                Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(s, new PayloadCallback() {
                                    @Override
                                    public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

                                    }

                                    @Override
                                    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

                                    }
                                });

                            }

                            @Override
                            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                                String inputString = "Hello World!";
                                byte[] byteArrray = inputString.getBytes();
                                Payload bytesPayload = Payload.fromBytes(byteArrray);
                                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(s, bytesPayload);
                                Toast.makeText(MasterActivity.this, "Payload sent", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onDisconnected(@NonNull String s) {
                                Toast.makeText(MasterActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    // We successfully requested a connection. Now both sides
                                    // must accept before the connection is established.
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // Nearby Connections failed to request the connection.
                                });
            }
        });
    }
}