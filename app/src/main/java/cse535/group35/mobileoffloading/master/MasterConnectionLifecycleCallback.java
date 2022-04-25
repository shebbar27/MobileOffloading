package cse535.group35.mobileoffloading.master;

import android.app.Activity;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;

import java.util.List;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class MasterConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private final Activity activity;
    private ArrayAdapter<String> connectedDevicesAdaptor;
    private List<ConnectedDevice> connectedDeviceList;
    private int[][] matrixResult;
    public MasterConnectionLifecycleCallback(Activity activity, ArrayAdapter<String> connectedDevicesAdaptor, List<ConnectedDevice> connectedDeviceList, int[][] matrixResult){
        this.activity = activity;
        this.connectedDevicesAdaptor=connectedDevicesAdaptor;
        this.connectedDeviceList=connectedDeviceList;
        this.matrixResult=matrixResult;
    }
    @Override
    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
        AppUtility.createAndDisplayToast(this.activity, "Connection Initiated with " + s);
        Nearby.getConnectionsClient(this.activity).acceptConnection(s, new MasterPayloadCallback(this.activity,matrixResult,connectedDeviceList));
    }

    @Override
    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
        connectedDevicesAdaptor.add(endpointId);
        ConnectedDevice newDevice= new ConnectedDevice(endpointId);
        connectedDeviceList.add(newDevice);
        Payload payload= Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE).build());
        Nearby.getConnectionsClient(this.activity).sendPayload(endpointId, payload);
        AppUtility.createAndDisplayToast(this.activity, "Payload sent");
    }

    @Override
    public void onDisconnected(@NonNull String s) {
        AppUtility.createAndDisplayToast(this.activity, "Disconnected");
    }
}
