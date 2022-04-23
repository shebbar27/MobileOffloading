package cse535.group35.mobileoffloading.slave;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;

import cse535.group35.mobileoffloading.AppUtility;

public class SlaveConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private final Context context;
    public SlaveConnectionLifecycleCallback(Context context){
        this.context=context;
    }
    @Override
    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
        AppUtility.createAndDisplayToast(context, "Accepting Connection from " + connectionInfo.getEndpointName());
        Nearby.getConnectionsClient(context).acceptConnection(s,new SlavePayloadCallback(context) );
    }

    @Override
    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
        // TODO
    }

    @Override
    public void onDisconnected(@NonNull String s) {
        // TODO
    }
}
