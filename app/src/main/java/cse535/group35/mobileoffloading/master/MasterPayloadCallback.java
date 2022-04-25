package cse535.group35.mobileoffloading.master;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import org.json.JSONArray;
import org.json.JSONObject;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;

public class MasterPayloadCallback extends PayloadCallback {

    private final Activity activity;

    public MasterPayloadCallback(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        String data = new String(payload.asBytes());
        try{
            JSONObject receivedPayload= new JSONObject(data);
            if(receivedPayload.getString(PayloadBuilder.requestTypeKey).equals(RequestType.COMPUTE_RESULT.name())){
                JSONArray array =new JSONArray(receivedPayload.getString("resultMatrix"));
                for(int i=0;i<array.length();i++){
                    int row=array.getJSONObject(i).getInt("rowIdx");
                    JSONArray arr=array.getJSONObject(i).getJSONArray("row");
                    for(int j = 0; j < arr.length(); j++) {
                        MasterActivity.matrixResult[row][j] = arr.getInt(j);
                    }

                }
                for(ConnectedDevice device: MasterActivity.connectedDevices){
                    if(device.getEndpointId().equals(endpointId)){
                        device.setCompleted(true);
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        // TODO
    }
}
