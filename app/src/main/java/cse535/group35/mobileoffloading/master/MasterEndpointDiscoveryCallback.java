package cse535.group35.mobileoffloading.master;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import cse535.group35.mobileoffloading.AppUtility;

public class MasterEndpointDiscoveryCallback extends EndpointDiscoveryCallback {
    private final Context context;
    private final ArrayAdapter<String> nearbyDevicesAdapter;

    public MasterEndpointDiscoveryCallback(Context context, ArrayAdapter<String> nearbyDevicesAdaptor){
        this.context = context;
        this.nearbyDevicesAdapter = nearbyDevicesAdaptor;
    }
    @Override
    public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
        AppUtility.createAndDisplayToast(context, "Found Device: "+discoveredEndpointInfo.getEndpointName());
        nearbyDevicesAdapter.add(endPointId);
    }

    @Override
    public void onEndpointLost(@NonNull String s) {
        // TODO
    }
}
