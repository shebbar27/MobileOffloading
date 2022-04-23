package cse535.group35.mobileoffloading.slave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.Strategy;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.R;

public class SlaveActivity extends AppCompatActivity {

    String nearbyServiceId;
    TextView slaveNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave);
        nearbyServiceId = getResources().getString(R.string.nearbyServiceId);
        slaveNameTextView = findViewById(R.id.slaveName);
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
                        (Void unused) -> AppUtility.createAndDisplayToast(this,
                                "Started Advertising"))
                .addOnFailureListener(
                        (Exception e) ->  AppUtility.createAndDisplayToast(this,
                                "Failed to Advertise: "+e.getMessage()));
    }
}