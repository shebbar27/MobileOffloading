package cse535.group35.mobileoffloading;

import static android.Manifest.*;
import static cse535.group35.mobileoffloading.R.id.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final private static int REQUEST_PERMISSIONS_CODE = 27;
    private static final ArrayList<String> PERMISSIONS = new ArrayList<>(
            Arrays.asList(
                    permission.ACCESS_COARSE_LOCATION,
                    permission.ACCESS_FINE_LOCATION)
    );

    private BluetoothHandler bluetoothHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();
        this.requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && this.areAllPermissionsGranted()) {
            this.bluetoothHandler.enableBluetooth();
        } else {
            this.bluetoothHandler.CheckForBluetoothConnectPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.bluetoothHandler.CheckForBluetoothEnabledAndDisplayAlert(requestCode, resultCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case scan_button:
                this.bluetoothHandler.scanForAvailableBluetoothDevices();
                break;

            case connect_button:
                this.bluetoothHandler.connectWithSelectedBluetoothDevices();
                break;
        }
    }

    private void initialize() {
        this.bluetoothHandler = new BluetoothHandler(this);

        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(scan_button);
                    add(connect_button);
                }}
        );

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            PERMISSIONS.add(permission.BLUETOOTH);
            PERMISSIONS.add(permission.BLUETOOTH_ADMIN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PERMISSIONS.add(permission.BLUETOOTH_SCAN);
            PERMISSIONS.add(permission.BLUETOOTH_ADVERTISE);
            PERMISSIONS.add(permission.BLUETOOTH_CONNECT);
        }
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                PERMISSIONS.toArray(new String[0]),
                REQUEST_PERMISSIONS_CODE);
    }

    private boolean areAllPermissionsGranted() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}