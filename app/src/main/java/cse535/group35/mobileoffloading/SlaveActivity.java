package cse535.group35.mobileoffloading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.Strategy;

import cse535.group35.mobileoffloading.slave.SlaveConnectionLifecycleCallback;

public class SlaveActivity extends AppCompatActivity {

    String nearbyServiceId;
    TextView slaveNameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave);
        nearbyServiceId=getResources().getString(R.string.nearbyServiceId);
        slaveNameTextView= (TextView) findViewById(R.id.slaveName);
        startAdvertising();
    }

    private void startAdvertising()
    {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        String slaveName= "Slave "+ java.util.UUID.randomUUID().toString().substring(0,4);
        slaveNameTextView.setText(slaveName);
        Nearby.getConnectionsClient(getApplicationContext())
                .startAdvertising(
                        slaveName, nearbyServiceId, new SlaveConnectionLifecycleCallback(getApplicationContext()), advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            Toast.makeText(this, "Started Advertising", Toast.LENGTH_SHORT).show();
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            Toast.makeText(this, "Failed to Advertise: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
    }
}