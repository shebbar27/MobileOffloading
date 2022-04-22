package cse535.group35.mobileoffloading.master;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import cse535.group35.mobileoffloading.MasterActivity;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class MasterConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private Context context;
    private int count=0;
    public MasterConnectionLifecycleCallback(Context context){
        this.context=context;
    }
    @Override
    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
        Toast.makeText(context, "Connection Initiated with "+s, Toast.LENGTH_SHORT).show();
        Nearby.getConnectionsClient(context).acceptConnection(s, new MasterPayloadCallback(context));
    }

    @Override
    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
        if(count==0){
            Payload payload= Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE).build());
            Nearby.getConnectionsClient(context).sendPayload(s, payload);
            Toast.makeText(context, "Payload sent", Toast.LENGTH_SHORT).show();
            count++;
        }


    }

    @Override
    public void onDisconnected(@NonNull String s) {
        Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
    }
}
