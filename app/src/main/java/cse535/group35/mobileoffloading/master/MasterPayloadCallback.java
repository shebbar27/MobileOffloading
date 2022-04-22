package cse535.group35.mobileoffloading.master;

import android.content.Context;
import android.widget.Toast;

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
        String data = new String(payload.asBytes());
        Toast.makeText(context, "Received message: "+data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

    }
}
