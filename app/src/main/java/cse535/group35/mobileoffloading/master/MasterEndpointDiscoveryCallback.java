package cse535.group35.mobileoffloading.master;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

public class MasterEndpointDiscoveryCallback extends EndpointDiscoveryCallback {
    private Context context;
    private ArrayAdapter nearbyDevicesAdapter;

    public MasterEndpointDiscoveryCallback(Context context,ArrayAdapter nearbyDevicesAdaptor){
        this.context=context;
        this.nearbyDevicesAdapter=nearbyDevicesAdaptor;
    }
    @Override
    public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
        Toast.makeText(context, "Found Device: "+discoveredEndpointInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
        nearbyDevicesAdapter.add(endPointId);

    }

    @Override
    public void onEndpointLost(@NonNull String s) {


    }
}
