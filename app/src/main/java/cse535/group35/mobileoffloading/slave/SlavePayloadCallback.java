package cse535.group35.mobileoffloading.slave;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class SlavePayloadCallback extends PayloadCallback {

    private final Activity activity;
    public SlavePayloadCallback(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        String data = new String(payload.asBytes());
        AppUtility.createAndDisplayToast(activity, "Received message: " + data);
            try {
                JSONObject reader = new JSONObject(data);

                double[] locationData = DeviceInfoHandler.getLastKnownLocation(activity);
                if(reader.getString(PayloadBuilder.requestTypeKey).equals(RequestType.DEVICE_STATE.toString())){
                    Payload responsePayload=Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE)
                            .setParameters(DeviceInfoHandler.getCurrentBatteryLevel(this.activity),
                                    locationData[0],
                                    locationData[1])
                            .build());
                    Nearby.getConnectionsClient(activity.getApplicationContext()).sendPayload(endpointId, responsePayload);
                    AppUtility.createAndDisplayToast(activity.getApplicationContext(), "Sent payload");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        // TODO
    }
}
