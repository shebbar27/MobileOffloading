package cse535.group35.mobileoffloading;

import static android.Manifest.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    final private static int REQUEST_PERMISSIONS_CODE = 27;
    final private static int REQUEST_ENABLE_BT = 137;

    private static final ArrayList<String> PERMISSIONS = new ArrayList<>(
            Arrays.asList(
                    permission.ACCESS_COARSE_LOCATION,
                    permission.ACCESS_FINE_LOCATION)
    );
    private boolean isBluetoothEnabled = false;

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
            this.enableBluetooth();
        } else {
            this.displayBluetoothPermissionDeniedAlertAndExitApp();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                this.isBluetoothEnabled = true;
                this.displayBluetoothEnabledAlert();
            }
            else {
                this.displayBluetoothNotEnabledAlertAndExitApp();
            }
        }
    }

    private void initialize() {
        this.isBluetoothEnabled = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PERMISSIONS.add(permission.BLUETOOTH_CONNECT);
        }
    }

    private void requestPermissions() {
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

    private void displayBluetoothNotEnabledAlertAndExitApp() {
        AppUtility.createExitAlertDialogWithConsentAndExit(this,
                getString(bluetooth_not_enabled_alert_title),
                getString(bluetooth_not_enabled_alert_message),
                getString(alert_dialog_ok));
    }

    private void displayBluetoothEnabledAlert() {
        AppUtility.createAlertDialogAndShow(this,
                getString(bluetooth_enabled_alert_title),
                getString(bluetooth_enabled_alert_message),
                getString(alert_dialog_ok),
                (dialog, which) -> dialog.cancel(),
                getString(empty_string),
                (dialog, which) -> {});
    }

    private void displayBluetoothPermissionDeniedAlertAndExitApp() {
        AppUtility.createExitAlertDialogWithConsentAndExit(this,
                getString(bluetooth_permission_denied_alert_title),
                getString(bluetooth_permission_denied_alert_message),
                getString(alert_dialog_ok));
    }

    private void displayBluetoothNotSupportedAlertAndExitApp() {
        AppUtility.createExitAlertDialogWithConsentAndExit(this,
                getString(bluetooth_not_supported_alert_title),
                getString(bluetooth_not_supported_alert_message),
                getString(alert_dialog_ok));
    }

    @SuppressWarnings("deprecation")
    private void enableBluetooth() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            this.displayBluetoothNotSupportedAlertAndExitApp();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions();
                return;
            }

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}