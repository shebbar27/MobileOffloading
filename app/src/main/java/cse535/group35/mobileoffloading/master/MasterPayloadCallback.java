package cse535.group35.mobileoffloading.master;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

public class MasterPayloadCallback extends PayloadCallback {

    private Context context;
    public MasterPayloadCallback(Context context){
        this.context=context;

    }
    @Override
    public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

    }
}
