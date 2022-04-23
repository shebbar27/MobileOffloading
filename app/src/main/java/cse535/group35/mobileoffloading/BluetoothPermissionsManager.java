package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.string.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class BluetoothPermissionsManager {

    private static final int REQUEST_ENABLE_BT = 137;
    private static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    public static void checkForBluetoothConnectPermission(AppCompatActivity activity) {
        if(!isBluetoothConnectPermissionGranted(activity)) {
            displayBluetoothPermissionDeniedAlertAndExitApp(activity);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    public static void enableBluetooth(AppCompatActivity activity) {
        if (bluetoothAdapter == null) {
            displayBluetoothNotSupportedAlertAndExitApp(activity);
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            checkForBluetoothConnectPermission(activity);

            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public static void checkForBluetoothEnabledAndDisplayAlert(AppCompatActivity activity,
                                                        int requestCode,
                                                        int resultCode) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                displayBluetoothEnabledAlert(activity);
            } else {
                displayBluetoothNotEnabledToastAndRetry(activity);
            }
        }
    }

    private static void displayBluetoothEnabledAlert(AppCompatActivity activity) {
        AppUtility.createAlertDialogAndShow(activity,
                bluetooth_enabled_alert_title,
                bluetooth_enabled_alert_message,
                alert_dialog_ok,
                (dialog, which) -> dialog.cancel(),
                empty_string,
                (dialog, which) -> { }
        );
    }

    private static void displayBluetoothNotEnabledToastAndRetry(AppCompatActivity activity) {
        AppUtility.createAndDisplayToast(activity,
                "Bluetooth is not enabled on Device! Trying to enabled bluetooth.",
                Toast.LENGTH_SHORT);
        enableBluetooth(activity);
    }

    private static void displayBluetoothNotSupportedAlertAndExitApp(AppCompatActivity activity) {
        AppUtility.createExitAlertDialogWithConsentAndExit(activity,
                bluetooth_not_supported_alert_title,
                bluetooth_not_supported_alert_message,
                alert_dialog_ok);
    }

    private static void displayBluetoothPermissionDeniedAlertAndExitApp(AppCompatActivity activity) {
        AppUtility.createExitAlertDialogWithConsentAndExit(activity,
                bluetooth_permission_denied_alert_title,
                bluetooth_permission_denied_alert_message,
                alert_dialog_ok);
    }

    private static boolean isBluetoothConnectPermissionGranted(AppCompatActivity activity) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }
}
