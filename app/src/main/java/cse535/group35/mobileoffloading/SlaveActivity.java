package cse535.group35.mobileoffloading;

import static com.google.android.material.circularreveal.CircularRevealHelper.STRATEGY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

public class SlaveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave);
        startAdvertizing();
    }

    private void startAdvertizing() 
    {
        Toast.makeText(this, "Started Advertizing", Toast.LENGTH_SHORT).show();
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startAdvertising(
                        "Test Slave", "OFFLOADINGSERVICE", new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                                Toast.makeText(SlaveActivity.this, "Accepting Connection from "+ connectionInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
                                Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(connectionInfo.getEndpointName(), new PayloadCallback() {
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

                            }

                            @Override
                            public void onDisconnected(@NonNull String s) {

                            }
                        }, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're advertising!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
                        });
    }
}