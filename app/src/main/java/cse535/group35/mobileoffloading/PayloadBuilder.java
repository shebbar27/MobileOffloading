package cse535.group35.mobileoffloading;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cse535.group35.mobileoffloading.matrixutil.MatrixUtil;

public class PayloadBuilder {
    public static final String requestTypeKey="requestType";
    public static final String batteryLevelKey="batteryLevel";
    public static final String latitudeKey="latitude";
    public static final String longitudeKey="longitude";
    public static final String parametersKey="parameters";


    private RequestType requestType;
    private String parameters;
    private int[][] matrixA,matrixB;
    private ArrayList<Integer> rows;
    private int[][] result;
    private List<MatrixUtil.MultiplicationResult> matrixResult;
    private int index;


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

        parameters= body.toString();
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

    public PayloadBuilder setParameters(List<MatrixUtil.MultiplicationResult> multiplicationResultList){
        this.parameters = MatrixUtil.getMultiplicationResultJSONArray(multiplicationResultList).toString();
        return this;
    }



    public byte[] build(){
        JSONObject payload = new JSONObject();

        try {
        if(requestType==RequestType.COMPUTE_RESULT){
            if(matrixResult!=null){
                payload.put("resultMatrix",matrixResult);
            }
            else {
                payload.put("matrixA", matrixA);
                payload.put("matrixB", matrixB);
                payload.put("rowsToCompute", rows);
            }

        }
        else if(requestType==RequestType.DEVICE_STATE){
            payload.put(requestTypeKey, requestType);
            if(parameters != null){
                payload.put(parametersKey, parameters);
            }
        }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return payload.toString().getBytes(StandardCharsets.UTF_8);
    }
}
