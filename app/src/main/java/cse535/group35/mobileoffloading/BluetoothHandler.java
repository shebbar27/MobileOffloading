package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.string.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BluetoothHandler {

    final private static int REQUEST_ENABLE_BT = 137;

    final private AppCompatActivity activity;
    final private BluetoothAdapter bluetoothAdapter;
    final private DeviceDiscoveryResultsReceiver deviceDiscoveryResultsReceiver;
    private final Set<BluetoothDevice> discoveredBluetoothDevices = new HashSet<>();

    @SuppressLint("MissingPermission")
    public BluetoothHandler(AppCompatActivity activity) {
        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.deviceDiscoveryResultsReceiver = new DeviceDiscoveryResultsReceiver(this.activity,
                this);
        this.registerDeviceDiscoveryResultsReceiver();
    }

    public static ArrayList<String> getBluetoothPermissions() {
        final ArrayList<String> permissions = new ArrayList<String>() {{
                add(Manifest.permission.ACCESS_COARSE_LOCATION);
                add(Manifest.permission.ACCESS_FINE_LOCATION);
            }};

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH);
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        return permissions;
    }

    public void checkForBluetoothConnectPermission() {
        if(!isBluetoothConnectPermissionGranted()) {
            this.displayBluetoothPermissionDeniedAlertAndExitApp();
        }
    }

    public void addDiscoveredBluetoothDevice(BluetoothDevice device) {
        this.discoveredBluetoothDevices.add(device);
    }

    @SuppressWarnings("MissingPermission")
    public Set<BluetoothDevice> getAllBluetoothDevices() {
        Set<BluetoothDevice> allBluetoothDevices = this.bluetoothAdapter.getBondedDevices();
        allBluetoothDevices.addAll(this.discoveredBluetoothDevices);
        return allBluetoothDevices;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    public void enableBluetooth() {
        if (this.bluetoothAdapter == null) {
            this.displayBluetoothNotSupportedAlertAndExitApp();
            return;
        }

        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.checkForBluetoothConnectPermission();

            this.activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @SuppressLint("MissingPermission")
    public void scanForAvailableBluetoothDevices() {
        if (!this.bluetoothAdapter.isEnabled()) {
            this.displayBluetoothNotEnabledToastAndRetry();
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

    public void checkForBluetoothEnabledAndDisplayAlert(int requestCode, int resultCode) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                this.displayBluetoothEnabledAlert();
            } else {
                this.displayBluetoothNotEnabledToastAndRetry();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void onDestroy() {
        if (this.bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        this.activity.unregisterReceiver(this.deviceDiscoveryResultsReceiver);
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

    private void displayBluetoothNotEnabledToastAndRetry() {
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

    private void registerDeviceDiscoveryResultsReceiver() {
        IntentFilter[] filters = {
                new IntentFilter(BluetoothDevice.ACTION_FOUND),
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED),
        };

        for (IntentFilter filter : filters) {
            this.activity.registerReceiver(this.deviceDiscoveryResultsReceiver, filter);
        }
    }
}
