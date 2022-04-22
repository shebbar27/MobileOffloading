package cse535.group35.mobileoffloading;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class PayloadBuilder {
    private RequestType requestType;
    private RequestType responseType;
    private String parameters;


    public static final String requestTypeKey="requestType";
    public static final String batteryKey="battery";
    public static final String latKey="latitude";
    public static final String longKey="longitude";


    public  PayloadBuilder setRequestType(RequestType type){
        this.requestType=type;
        return this;

    }

    public  PayloadBuilder setParameters(float battery,float lat,float lon){
        JSONObject body = new JSONObject();

        try {
            body.put(batteryKey, battery);
            body.put(latKey, lat);
            body.put(longKey, lon);



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
            if(parameters!=null){
                payload.put("parameters",parameters);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload.toString().getBytes(StandardCharsets.UTF_8);
    }
}
