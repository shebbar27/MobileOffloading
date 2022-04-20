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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BluetoothHandler {

    final private static int REQUEST_ENABLE_BT = 137;

    private final AppCompatActivity activity;
    private final BluetoothAdapter bluetoothAdapter;
    private final DeviceDiscoveryResultsReceiver deviceDiscoveryResultsReceiver;
    private final Set<BluetoothDevice> discoveredBluetoothDevices = new HashSet<>();
    public ArrayAdapter<String> bluetoothDevicesAdapter;

    @SuppressLint("MissingPermission")
    public BluetoothHandler(AppCompatActivity activity) {
        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.deviceDiscoveryResultsReceiver = new DeviceDiscoveryResultsReceiver();
        this.deviceDiscoveryResultsReceiver.setActivity(activity);
        this.deviceDiscoveryResultsReceiver.setBluetoothHandler(this);
        this.registerDeviceDiscoveryResultsReceiver();
        this.initializeBluetoothDevicesAdapter();
    }

    public static ArrayList<String> getBluetoothPermissions() {
        final ArrayList<String> permissions = new ArrayList<String>() {{
                add(Manifest.permission.ACCESS_COARSE_LOCATION);
                add(Manifest.permission.ACCESS_FINE_LOCATION);
                add(Manifest.permission.BLUETOOTH);
                add(Manifest.permission.BLUETOOTH_ADMIN);
            }};

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

    @SuppressLint("MissingPermission")
    public void addDiscoveredBluetoothDevice(BluetoothDevice device) {
        this.discoveredBluetoothDevices.add(device);
        this.updateBluetoothDevicesAdapter();
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

        this.discoveredBluetoothDevices.clear();
        this.registerDeviceDiscoveryResultsReceiver();
        this.bluetoothAdapter.startDiscovery();
    }

    @SuppressLint("MissingPermission")
    public void connectWithSelectedBluetoothDevices() {
        // TODO
        if (!this.bluetoothAdapter.isDiscovering()) {
            AppUtility.createAndDisplayToast(this.activity,
                    "Device Discovery did not start",
                    Toast.LENGTH_LONG);
        }
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

        this.discoveredBluetoothDevices.clear();
        this.activity.unregisterReceiver(this.deviceDiscoveryResultsReceiver);
    }

    @SuppressLint("MissingPermission")
    private void initializeBluetoothDevicesAdapter() {
        Set<BluetoothDevice> pairedBluetoothDevices = this.bluetoothAdapter.getBondedDevices();
        ArrayList<String> pairedBluetoothDeviceNames = new ArrayList<>();
        for (BluetoothDevice device: pairedBluetoothDevices) {
            pairedBluetoothDeviceNames.add(device.getName());
        }

        this.bluetoothDevicesAdapter = new ArrayAdapter<>(this.activity,
                android.R.layout.simple_selectable_list_item,
                pairedBluetoothDeviceNames);
    }

    @SuppressLint("MissingPermission")
    private void updateBluetoothDevicesAdapter() {
        Set<BluetoothDevice> allBluetoothDevices = this.bluetoothAdapter.getBondedDevices();
        allBluetoothDevices.addAll(this.discoveredBluetoothDevices);
        ArrayList<String> allBluetoothDeviceNames = new ArrayList<>();
        for (BluetoothDevice device: allBluetoothDevices) {
            allBluetoothDeviceNames.add(device.getName());
        }

        this.bluetoothDevicesAdapter.clear();
        this.bluetoothDevicesAdapter.addAll(allBluetoothDeviceNames);
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
