package cse535.group35.mobileoffloading.slave;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;

import cse535.group35.mobileoffloading.AppUtility;

public class SlaveConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private final Activity activity;
    public SlaveConnectionLifecycleCallback(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo connectionInfo) {
        AppUtility.createConnectionConsentAlert(activity,
                endPointId,
                (dialog, which) ->
                {
                    Nearby.getConnectionsClient(activity.getApplicationContext())
                            .acceptConnection(endPointId, new SlavePayloadCallback(activity));
                },
                (dialog, which) -> dialog.cancel()
        );
    }


    @Override
    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
        Nearby.getConnectionsClient(activity.getApplicationContext()).stopAdvertising();
        DeviceInfoHandler.updateAdvertiseButton(activity, "Connected");
        DeviceInfoHandler.toggleAdvertiseButton(activity);
    }

    @Override
    public void onDisconnected(@NonNull String s) {
        DeviceInfoHandler.updateAdvertiseButton(activity, "Start Advertising");
        DeviceInfoHandler.toggleAdvertiseButton(activity);
    }
}
