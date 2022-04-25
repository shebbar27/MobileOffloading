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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class AppPermissionsManager {

    public static ArrayList<String> getAllPermissions() {
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

    public static void requestAllPermissions(Activity activity, int requestPermissionCode) {
        if(!areAllPermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(activity,
                    getAllPermissions().toArray(new String[0]),
                    requestPermissionCode);
        }
    }

    public static boolean areAllPermissionsGranted(Activity activity) {
        for (String permission : getAllPermissions()) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void checkForBluetoothEnabledAndDisplayAlert(Activity activity,
                                                                    int enableRequestCode,
                                                                    int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            displayBluetoothEnabledAlert(activity);
        } else {
            displayBluetoothNotEnabledToastAndRetry(activity, enableRequestCode);
        }
    }

    @SuppressLint("MissingPermission")
    public static void checkForBluetoothEnabledAndTakeAction(Activity activity, int enableRequestCode) {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            displayBluetoothNotSupportedAlertAndExitApp(activity);
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            checkForBluetoothPermissionsAndShowAlert(activity);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, enableRequestCode);
        }
    }

    private static void checkForBluetoothPermissionsAndShowAlert(Activity activity) {
        if(!areAllPermissionsGranted(activity)) {
            displayBluetoothPermissionDeniedAlertAndExitApp(activity);
        }
    }

    private static void displayBluetoothEnabledAlert(Activity activity) {
        AppUtility.createAlertDialogAndShow(activity,
                bluetooth_enabled_alert_title,
                bluetooth_enabled_alert_message,
                alert_dialog_ok,
                (dialog, which) -> dialog.cancel(),
                empty_string,
                (dialog, which) -> { }
        );
    }

    private static void displayBluetoothNotEnabledToastAndRetry(Activity activity, int enableRequestCode) {
        AppUtility.createAndDisplayToast(activity,
                "Bluetooth is not enabled on Device! Trying to enabled bluetooth.",
                Toast.LENGTH_SHORT);
        checkForBluetoothEnabledAndTakeAction(activity, enableRequestCode);
    }

    private static void displayBluetoothNotSupportedAlertAndExitApp(Activity activity) {
        AppUtility.createExitAlertDialogWithConsentAndExit(activity,
                bluetooth_not_supported_alert_title,
                bluetooth_not_supported_alert_message,
                alert_dialog_ok);
    }

    private static void displayBluetoothPermissionDeniedAlertAndExitApp(Activity activity) {
        AppUtility.createExitAlertDialogWithConsentAndExit(activity,
                bluetooth_permission_denied_alert_title,
                bluetooth_permission_denied_alert_message,
                alert_dialog_ok);
    }
}
