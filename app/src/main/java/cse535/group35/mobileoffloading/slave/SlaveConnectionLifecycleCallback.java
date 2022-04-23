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
    public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo connectionInfo) {
        AppUtility.createAndDisplayToast(context,
                "Accepting Connection from " + connectionInfo.getEndpointName());
        AppUtility.createConnectionConsentAlert(context,
                endPointId,
                (dialog, which) -> Nearby.getConnectionsClient(context)
                        .acceptConnection(endPointId, new SlavePayloadCallback(context)),
                (dialog, which) -> dialog.cancel()
        );
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
