package cse535.group35.mobileoffloading;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class PayloadBuilder {
    public static final String requestTypeKey="requestType";
    public static final String batteryLevelKey="batteryLevel";
    public static final String latitudeKey="latitude";
    public static final String longitudeKey="longitude";
    public static final String parametersKey="parameters";

    private RequestType requestType;
    private String parameters;

    public PayloadBuilder setRequestType(RequestType requestType){
        this.requestType = requestType;
        return this;
    }

    public PayloadBuilder setParameters(float batteryLevel, float latitude, float longitude){
        JSONObject body = new JSONObject();

        try {
            body.put(batteryLevelKey, batteryLevel);
            body.put(latitudeKey, latitude);
            body.put(longitudeKey, longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        parameters= body.toString();
        return this;
    }

    public byte[] build(){
        JSONObject payload = new JSONObject();

        try {
            payload.put(requestTypeKey, requestType);
            if(parameters != null){
                payload.put(parametersKey, parameters);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return payload.toString().getBytes(StandardCharsets.UTF_8);
    }
}
