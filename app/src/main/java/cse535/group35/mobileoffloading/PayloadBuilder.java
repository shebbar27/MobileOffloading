package cse535.group35.mobileoffloading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cse535.group35.mobileoffloading.matrixutil.MatrixUtil;
import cse535.group35.mobileoffloading.matrixutil.MultiplicationResult;

public class PayloadBuilder {

    public static final String requestTypeKey="requestType";
    public static final String batteryLevelKey="batteryLevel";
    public static final String latitudeKey="latitude";
    public static final String longitudeKey="longitude";
    public static final String parametersKey="parameters";


    private RequestType requestType;
    private JSONObject parameters;
    private int[][] matrixA, matrixB;
    private ArrayList<Integer> rows;
    private List<MultiplicationResult> matrixResult;

    public PayloadBuilder setRequestType(RequestType requestType){
        this.requestType = requestType;
        return this;
    }

    public PayloadBuilder setParameters(int batteryLevel, double latitude, double longitude){
        JSONObject body = new JSONObject();

        try {
            body.put(batteryLevelKey, batteryLevel);
            body.put(latitudeKey, String.format(Locale.US, "%1$.3f", latitude));
            body.put(longitudeKey, String.format(Locale.US, "%1$.3f", longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        parameters= body;
        return this;
    }

    public PayloadBuilder setParameters(int[][] matrixA,int[][] matrixB){
        this.matrixA=matrixA;
        this.matrixB=matrixB;
        return this;
    }

    public PayloadBuilder setParameters(ArrayList<Integer> rows){
        this.rows=rows;
        return this;
    }

    public PayloadBuilder setParameters(List<MultiplicationResult> multiplicationResultList){
        this.matrixResult = multiplicationResultList;
        return this;
    }

    public byte[] build(){
        JSONObject payload = new JSONObject();

        try {
            payload.put(requestTypeKey, requestType);
            if(requestType==RequestType.COMPUTE_RESULT){
                if(matrixResult!=null){
                    payload.put("resultMatrix",MatrixUtil.getMultiplicationResultJSONArray(this.matrixResult).toString());
                }
                else {
                    payload.put("matrixA", convertToJSON(matrixA));
                    payload.put("matrixB", convertToJSON(matrixB));
                    JSONArray rowToComputeArr = new JSONArray();
                    for(int num : rows) {
                        rowToComputeArr.put(num);
                    }
                    payload.put("rowsToCompute", rowToComputeArr);
                }
            }
            else if(requestType==RequestType.DEVICE_STATE){
                if(parameters != null){
                    payload.put(parametersKey, parameters);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return payload.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static JSONArray convertToJSON(int[][] mat) {
        JSONArray jsonArray = new JSONArray();
        for(int[] row : mat) {
            JSONArray rowArr = new JSONArray();
            for(int num : row) {
                rowArr.put(num);
            }

            jsonArray.put(rowArr);
        }

        return jsonArray;
    }
}
