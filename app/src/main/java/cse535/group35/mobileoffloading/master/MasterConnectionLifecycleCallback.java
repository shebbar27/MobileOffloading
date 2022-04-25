package cse535.group35.mobileoffloading.master;

import static java.util.Collections.addAll;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;

import java.util.ArrayList;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;
import cse535.group35.mobileoffloading.TestMatrix;

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
        for(int i=0;i<nearbyDevicesAdaptor.getCount();i++){
                if(nearbyDevicesAdaptor.getItem(i).contains(endpointId)){
                    this.connectedDevicesAdaptor.add(nearbyDevicesAdaptor.getItem(i));
                    break;
                }

        }
        ConnectedDevice newDevice = new ConnectedDevice(endpointId);
        MasterActivity.connectedDevices.add(newDevice);
        Payload payload = Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE).build());
        Nearby.getConnectionsClient(this.activity).sendPayload(endpointId, payload);
        nearbyDevicesAdaptor.clear();
    }

    @Override
    public void onDisconnected(@NonNull String endpointId) {
        try{
            AppUtility.createAndDisplayToast(this.activity, "Disconnected");
            ConnectedDevice disconnectedDevice=new ConnectedDevice("");
            for(ConnectedDevice device: MasterActivity.connectedDevices){
                if(device.getEndpointId().equals(endpointId)){
                    disconnectedDevice=device;
                    disconnectedDevice.setDeviceState(-1);
                    if(device.getComputeRows()==null) return;

                }

            }
            for(ConnectedDevice device: MasterActivity.connectedDevices){
                if(!device.getEndpointId().equals(endpointId)){
                    ArrayList<Integer> updatedRows=device.getComputeRows();

                    updatedRows.addAll(disconnectedDevice.getComputeRows());
                    device.setComputeRows(updatedRows);
                    Payload payload= Payload.fromBytes(
                            new PayloadBuilder().setRequestType(RequestType.COMPUTE_RESULT)
                                    .setParameters(TestMatrix.getMatrixA(), TestMatrix.getMatrixA())
                                    .setParameters(disconnectedDevice.getComputeRows())
                                    .build());
                    Nearby.getConnectionsClient(activity.getApplicationContext()).sendPayload(device.getEndpointId(),payload);

                }

            }
        }
        catch (Exception e){
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}
