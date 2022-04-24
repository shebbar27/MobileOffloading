package cse535.group35.mobileoffloading.slave;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.RequestType;
import cse535.group35.mobileoffloading.matrixutil.MatrixUtil;

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

                if(reader.getString(PayloadBuilder.requestTypeKey).equals(RequestType.COMPUTE_RESULT.toString())){
                     JSONArray matrixAArr1 = reader.getJSONArray("matrixA"); //{{1,2},{3,4}}
                    int[][] A = new int[matrixAArr1.length()][matrixAArr1.length()];
                    for(int i = 0; i < matrixAArr1.length(); i++) {
                        JSONArray matrixAArrRow = matrixAArr1.getJSONArray(i);
                        for(int j = 0; j < matrixAArr1.length(); j++) {
                            A[i][j] = matrixAArrRow.getInt(j);
                        }
                    }
                    JSONArray matrixAArr2 = reader.getJSONArray("matrixB");
                    int[][] B = new int[matrixAArr2.length()][matrixAArr2.length()];
                    for(int i = 0; i < matrixAArr2.length(); i++) {
                        JSONArray matrixAArrRow = matrixAArr2.getJSONArray(i);
                        for(int j = 0; j < matrixAArr2.length(); j++) {
                            B[i][j] = matrixAArrRow.getInt(j);
                        }
                    }

                    List<Integer> idxToCompute = new ArrayList<>();
                    reader.getJSONArray("rowsToCompute");


                    Payload responsePayload=Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.COMPUTE_RESULT)
                            .setParameters(new MatrixUtil(A, B).getMultiplicationResult(idxToCompute))
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
