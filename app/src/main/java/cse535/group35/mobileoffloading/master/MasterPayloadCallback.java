package cse535.group35.mobileoffloading.master;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import cse535.group35.mobileoffloading.AppUtility;

public class MasterPayloadCallback extends PayloadCallback {

    private final Context context;
    public MasterPayloadCallback(Context context){
        this.context=context;

    }
    @Override
    public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
        String data = new String(payload.asBytes());
        AppUtility.createAndDisplayToast(context, "Received message: "+data);
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        // TODO
    }
}
