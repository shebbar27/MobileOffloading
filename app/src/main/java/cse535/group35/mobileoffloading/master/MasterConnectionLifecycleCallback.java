package cse535.group35.mobileoffloading.master;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class MasterConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private final Activity activity;
    public MasterConnectionLifecycleCallback(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
        AppUtility.createAndDisplayToast(this.activity, "Connection Initiated with " + s);
        Nearby.getConnectionsClient(this.activity).acceptConnection(s, new MasterPayloadCallback(this.activity));
    }

    @Override
    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
        Payload payload= Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE).build());
        Nearby.getConnectionsClient(this.activity).sendPayload(s, payload);
        AppUtility.createAndDisplayToast(this.activity, "Payload sent");
    }

    @Override
    public void onDisconnected(@NonNull String s) {
        AppUtility.createAndDisplayToast(this.activity, "Disconnected");
    }
}
