package cse535.group35.mobileoffloading.master;

import android.app.Activity;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;

import java.util.ArrayList;

import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;
import cse535.group35.mobileoffloading.TestMatrix;

public class MasterEndpointDiscoveryCallback extends EndpointDiscoveryCallback {
    private final Activity activity;
    private final ArrayAdapter<String> nearbyDevicesAdapter;

    public MasterEndpointDiscoveryCallback(Activity activity, ArrayAdapter<String> nearbyDevicesAdaptor){
        this.activity = activity;
        this.nearbyDevicesAdapter = nearbyDevicesAdaptor;
    }
    @Override
    public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
        this.nearbyDevicesAdapter.add(discoveredEndpointInfo.getEndpointName() + " (" + endPointId + ")");
    }

    @Override
    public void onEndpointLost(@NonNull String endPointId) {
        try {
            ConnectedDevice connectedDevice = MasterActivity.connectedDevices.stream()
                    .filter(e -> e.getEndpointId().equals(endPointId))
                    .findFirst().orElseThrow(() ->
                            new Exception("No device with endpointID: " + endPointId + " found!"));
            connectedDevice.setDeviceState(-1);
            connectedDevice.setCompleted(true);
            connectedDevice.setBusy(false);
            ArrayList<Integer> computeRows = connectedDevice.getComputeRows();

            ConnectedDevice completedConnectedDevice = MasterActivity.connectedDevices.stream()
                    .filter(ConnectedDevice::isCompleted)
                    .findFirst().orElseThrow(() -> new Exception("Exception"));
            while(completedConnectedDevice == null) {
                Thread.sleep(3000);
            }

            connectedDevice.setCompleted(false);
            connectedDevice.setComputeRows(computeRows);

            Payload payload= Payload.fromBytes(
                    new PayloadBuilder().setRequestType(RequestType.COMPUTE_RESULT)
                        .setParameters(TestMatrix.getMatrixA(), TestMatrix.getMatrixB())
                        .setParameters(computeRows)
                        .build());
            Nearby.getConnectionsClient(this.activity).sendPayload(connectedDevice.getEndpointId(),payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
