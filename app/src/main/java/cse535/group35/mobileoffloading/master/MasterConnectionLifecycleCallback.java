package cse535.group35.mobileoffloading.master;

import android.content.Context;

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

    private final Context context;
    public MasterConnectionLifecycleCallback(Context context){
        this.context=context;
    }
    @Override
    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
        AppUtility.createAndDisplayToast(context, "Connection Initiated with " + s);
        Nearby.getConnectionsClient(context).acceptConnection(s, new MasterPayloadCallback(context));
    }

    @Override
    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
        Payload payload= Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE).build());
        Nearby.getConnectionsClient(context).sendPayload(s, payload);
        AppUtility.createAndDisplayToast(context, "Payload sent");
    }

    @Override
    public void onDisconnected(@NonNull String s) {
        AppUtility.createAndDisplayToast(context, "Disconnected");
    }
}
