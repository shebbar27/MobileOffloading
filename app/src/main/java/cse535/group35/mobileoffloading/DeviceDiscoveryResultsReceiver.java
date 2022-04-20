package cse535.group35.mobileoffloading;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceDiscoveryResultsReceiver extends BroadcastReceiver {

    private final AppCompatActivity activity;
    private final BluetoothHandler bluetoothHandler;

    public DeviceDiscoveryResultsReceiver(AppCompatActivity activity, BluetoothHandler bluetoothHandler) {
        this.activity = activity;
        this.bluetoothHandler = bluetoothHandler;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null && device.getBondState() != BluetoothDevice.BOND_BONDED) {
                this.bluetoothHandler.addDiscoveredBluetoothDevice(device);
                AppUtility.createAndDisplayToast(this.activity,
                        "Found Device: " + device.getName() + "\t" + device.getAddress()
                );
            }
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            AppUtility.createAndDisplayToast(this.activity,
                    "Scanning for nearby bluetooth devices. Please select a device from " +
                            "the dropdown selector and connect.");
        }

    }
}
