package cse535.group35.mobileoffloading.slave;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class SlavePayloadCallback extends PayloadCallback {

    private Context context;
    private Payload payload;
    private int count=0;
    public SlavePayloadCallback(Context context){
        this.context=context;
    }
    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        String data = new String(payload.asBytes());
        Toast.makeText(context, "Received message: "+data, Toast.LENGTH_SHORT).show();
            try {
                JSONObject reader = new JSONObject(data);

                if(reader.getString(PayloadBuilder.requestTypeKey).equals(RequestType.DEVICE_STATE.toString())){
                    Payload responsePayload=Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE)
                            .setParameters(10,100,122)
                            .build());
                    Nearby.getConnectionsClient(context).sendPayload(endpointId,responsePayload);
                    Toast.makeText(context, "Sent payload", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        if(count<2){

            count++;
        }

    }
}
