package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.string.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BluetoothHandler {

    final private static int REQUEST_ENABLE_BT = 137;

    private final AppCompatActivity activity;
    private final BluetoothAdapter bluetoothAdapter;

    public BluetoothHandler(AppCompatActivity activity) {
        this.activity = activity;
        BluetoothManager bluetoothManager = activity.getSystemService(BluetoothManager.class);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void CheckForBluetoothConnectPermission() {
        if(!isBluetoothConnectPermissionGranted()) {
            this.displayBluetoothPermissionDeniedAlertAndExitApp();
        }
    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("deprecation")
    public void enableBluetooth() {
        if (this.bluetoothAdapter == null) {
            this.displayBluetoothNotSupportedAlertAndExitApp();
            return;
        }

        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.CheckForBluetoothConnectPermission();

            this.activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @SuppressLint("MissingPermission")
    public void scanForAvailableBluetoothDevices() {
        if (!this.bluetoothAdapter.isEnabled()) {
            this.displayBluetoothNotEnabledAlertAndRetry();
            return;
        }

        AppUtility.createAndDisplayToast(this.activity,
                "Scanning for nearby bluetooth devices",
                Toast.LENGTH_LONG);

        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }

        // Request for device discovery from BluetoothAdapter
        this.bluetoothAdapter.startDiscovery();
    }

    public void connectWithSelectedBluetoothDevices() {
        // TODO
    }

    public void CheckForBluetoothEnabledAndDisplayAlert(int requestCode, int resultCode) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                this.displayBluetoothEnabledAlert();
            } else {
                this.displayBluetoothNotEnabledAlertAndRetry();
            }
        }
    }

    private void displayBluetoothEnabledAlert() {
        AppUtility.createAlertDialogAndShow(this.activity,
                bluetooth_enabled_alert_title,
                bluetooth_enabled_alert_message,
                alert_dialog_ok,
                (dialog, which) -> dialog.cancel(),
                empty_string,
                (dialog, which) -> { }
        );
    }

    private void displayBluetoothNotEnabledAlertAndRetry() {
        AppUtility.createAndDisplayToast(this.activity,
                "Bluetooth is not enabled on Device! Trying to enabled bluetooth.");

        this.enableBluetooth();
    }

    private void displayBluetoothNotSupportedAlertAndExitApp() {
        AppUtility.createExitAlertDialogWithConsentAndExit(this.activity,
                bluetooth_not_supported_alert_title,
                bluetooth_not_supported_alert_message,
                alert_dialog_ok);
    }

    private void displayBluetoothPermissionDeniedAlertAndExitApp() {
        AppUtility.createExitAlertDialogWithConsentAndExit(this.activity,
                bluetooth_permission_denied_alert_title,
                bluetooth_permission_denied_alert_message,
                alert_dialog_ok);
    }

    private boolean isBluetoothConnectPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                ActivityCompat.checkSelfPermission(this.activity,
                        Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }
}
