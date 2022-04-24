package cse535.group35.mobileoffloading.master;

import android.app.Activity;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import cse535.group35.mobileoffloading.AppUtility;

public class MasterEndpointDiscoveryCallback extends EndpointDiscoveryCallback {
    private final Activity activity;
    private final ArrayAdapter<String> nearbyDevicesAdapter;

    public MasterEndpointDiscoveryCallback(Activity activity, ArrayAdapter<String> nearbyDevicesAdaptor){
        this.activity = activity;
        this.nearbyDevicesAdapter = nearbyDevicesAdaptor;
    }
    @Override
    public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
        AppUtility.createAndDisplayToast(this.activity, "Found Device: "+discoveredEndpointInfo.getEndpointName());
        nearbyDevicesAdapter.add(endPointId);
    }

    @Override
    public void onEndpointLost(@NonNull String s) {
        // TODO
    }
}
