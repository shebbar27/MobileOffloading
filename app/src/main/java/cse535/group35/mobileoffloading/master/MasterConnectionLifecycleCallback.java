package cse535.group35.mobileoffloading.master;

import android.app.Activity;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class MasterConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private final Activity activity;
    private final ArrayAdapter<String> connectedDevicesAdaptor;
    private final ArrayAdapter<String> nearbyDevicesAdaptor;

    public MasterConnectionLifecycleCallback(Activity activity,
                                             ArrayAdapter<String> connectedDevicesAdaptor,
                                             ArrayAdapter<String> nearbyDevicesAdaptor){
        this.activity = activity;
        this.connectedDevicesAdaptor = connectedDevicesAdaptor;
        this.nearbyDevicesAdaptor = nearbyDevicesAdaptor;
    }

    @Override
    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
        Nearby.getConnectionsClient(this.activity)
                .acceptConnection(endpointId,
                        new MasterPayloadCallback(this.activity));
    }

    @Override
    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
        AppUtility.createAndDisplayToast(this.activity, "Connected to endpointID: " + endpointId);
        this.connectedDevicesAdaptor.add(endpointId);
        ConnectedDevice newDevice = new ConnectedDevice(endpointId);
        MasterActivity.connectedDevices.add(newDevice);
        Payload payload = Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE).build());
        Nearby.getConnectionsClient(this.activity).sendPayload(endpointId, payload);
        nearbyDevicesAdaptor.clear();
    }

    @Override
    public void onDisconnected(@NonNull String s) {
        AppUtility.createAndDisplayToast(this.activity, "Disconnected");
    }
}
