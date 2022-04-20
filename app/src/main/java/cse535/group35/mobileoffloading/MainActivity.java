package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final ArrayList<String> PERMISSIONS = BluetoothHandler.getBluetoothPermissions();

    private BluetoothHandler bluetoothHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bluetoothHandler = new BluetoothHandler(this);
        this.registerOnClickListenerCallBackForButtons();
        this.initializeDevicesListView();
        this.requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && this.areAllPermissionsGranted()) {
            this.bluetoothHandler.enableBluetooth();
        } else {
            this.bluetoothHandler.checkForBluetoothConnectPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.bluetoothHandler.checkForBluetoothEnabledAndDisplayAlert(requestCode, resultCode);
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
            case exit_button:
                this.exitApplication();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.bluetoothHandler.onDestroy();
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                PERMISSIONS.toArray(new String[0]),
                REQUEST_PERMISSIONS_CODE);
    }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(scan_button);
                    add(connect_button);
                    add(exit_button);
                }}
        );
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

    private void exitApplication() {
        AppUtility.createExitAlertDialogWithConsentAndExit(this,
                exit_dialog_title,
                exit_dialog_message,
                alert_dialog_yes,
                alert_dialog_no);
    }

    private void initializeDevicesListView() {
        ListView devicesListView = findViewById(devices_listview);
        devicesListView.setAdapter(this.bluetoothHandler.bluetoothDevicesAdapter);
    }
}