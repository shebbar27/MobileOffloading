package cse535.group35.mobileoffloading.slave;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;
import cse535.group35.mobileoffloading.SlaveActivity;

public class SlaveConnectionLifecycleCallback extends ConnectionLifecycleCallback {

    private Context context;
    public SlaveConnectionLifecycleCallback(Context context){
        this.context=context;
    }
    @Override
    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
        Toast.makeText(context, "Accepting Connection from "+ connectionInfo.getEndpointName(), Toast.LENGTH_SHORT).show();
        Nearby.getConnectionsClient(context).acceptConnection(s,new SlavePayloadCallback(context) );
    }

    @Override
    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {



    }

    @Override
    public void onDisconnected(@NonNull String s) {

    }
}
