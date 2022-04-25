package cse535.group35.mobileoffloading.slave;

import android.app.Activity;
import android.util.Log;
import android.view.View;

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
            try {
                JSONObject reader = new JSONObject(data);
                //AppUtility.createAndDisplayToast(activity, "Received message: " + reader.getString(PayloadBuilder.requestTypeKey));
                AppUtility.createAndDisplayToast(activity, "Received message: " + reader.optString(PayloadBuilder.requestTypeKey, "not here"));


                if(reader.getString(PayloadBuilder.requestTypeKey).equals(RequestType.DEVICE_STATE.toString())){

                    double[] locationData = DeviceInfoHandler.getLastKnownLocation(activity);
                    Payload responsePayload=Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.DEVICE_STATE)
                            .setParameters(DeviceInfoHandler.getCurrentBatteryLevel(this.activity),
                                    locationData[0],
                                    locationData[1])
                            .build());
                    Nearby.getConnectionsClient(activity.getApplicationContext()).sendPayload(endpointId, responsePayload);
                    AppUtility.createAndDisplayToast(activity.getApplicationContext(), "Sent payload");
                }

                if(reader.getString(PayloadBuilder.requestTypeKey).equals(RequestType.COMPUTE_RESULT.toString())){
                    //AppUtility.createAndDisplayToast(activity, "Compute: " + data);
                    DeviceInfoHandler.updateStatusTextView(activity, SlaveStatus.BUSY);
                    JSONArray matrixAArr1 = reader.getJSONArray("matrixA"); //{{1,2},{3,4}}
                    Log.d("matrixAArr1", "matrix: " +  matrixAArr1);
                    //AppUtility.createAndDisplayToast(activity, "Compute1: " + matrixAArr1);
                    int[][] A = new int[matrixAArr1.length()][matrixAArr1.length()];
                    for(int i = 0; i < matrixAArr1.length(); i++) {
                        JSONArray matrixAArrRow = matrixAArr1.getJSONArray(i);
                        for(int j = 0; j < matrixAArr1.length(); j++) {
                            A[i][j] = Integer.parseInt(matrixAArrRow.get(j).toString());
                        }
                    }
                    JSONArray matrixAArr2 = reader.getJSONArray("matrixB");
                    int[][] B = new int[matrixAArr2.length()][matrixAArr2.length()];
                    for(int i = 0; i < matrixAArr2.length(); i++) {
                        JSONArray matrixAArrRow = matrixAArr2.getJSONArray(i);
                        for(int j = 0; j < matrixAArr2.length(); j++) {
                            B[i][j] = Integer.parseInt(matrixAArrRow.get(j).toString());
                        }
                    }


                    List<Integer> idxToCompute = new ArrayList<>();
                    JSONArray rows = reader.getJSONArray("rowsToCompute");
                    for(int i=0;i<rows.length();i++){
                        idxToCompute.add(rows.getInt(i));
                    }

                    AppUtility.createAndDisplayToast(activity, "After extract: " + idxToCompute);
                    List<MatrixUtil.MultiplicationResult> res=new MatrixUtil(A, B).getMultiplicationResult(idxToCompute);
                    //AppUtility.createAndDisplayToast(activity.getApplicationContext(), "multiply almost"+res.size());
                    //AppUtility.createAndDisplayToast(activity.getApplicationContext(), "multiply almost1"+A[0][0]);

                    //AppUtility.createAndDisplayToast(activity.getApplicationContext(), "Sent almost" + res.get(0).getRow()[0]);
                    Payload responsePayload=Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.COMPUTE_RESULT)
                            .setParameters(res)
                                    .build());
                    //AppUtility.createAndDisplayToast(activity.getApplicationContext(), "Sent almost");
                    Nearby.getConnectionsClient(activity.getApplicationContext()).sendPayload(endpointId, responsePayload);
                    AppUtility.createAndDisplayToast(activity.getApplicationContext(), "Sent payload" + endpointId);
                    DeviceInfoHandler.updateResultTextView(activity, MatrixUtil.getMultiplicationResultJSONArray(res).toString());
                    DeviceInfoHandler.setResultTextViewVisibility(activity, View.VISIBLE);
                    DeviceInfoHandler.updateStatusTextView(activity, SlaveStatus.IDLE);
                }
            } catch (JSONException e) {
                AppUtility.createAndDisplayToast(activity, "Exception"+e.getMessage() );
                Log.d("TAG", "onPayloadReceived: ",e);
                e.printStackTrace();
            }
        }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        // TODO
    }
}
