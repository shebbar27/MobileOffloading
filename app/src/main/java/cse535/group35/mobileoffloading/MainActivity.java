package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import cse535.group35.mobileoffloading.master.MasterActivity;
import cse535.group35.mobileoffloading.slave.SlaveActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final int REQUEST_ENABLE_BT = 137;
    private Spinner devicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerOnClickListenerCallBackForButtons();
        this.initializeModeSelectorSpinner();
        AppPermissionsManager.requestAllPermissions(this, REQUEST_PERMISSIONS_CODE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case continue_button:
                this.navigateToSelectedMode();
                break;
            case main_exit_button:
                this.exitApplication();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && AppPermissionsManager.areAllPermissionsGranted(this)) {
            AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(this, REQUEST_ENABLE_BT);
        } else {
            AppPermissionsManager.requestAllPermissions(this, REQUEST_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            AppPermissionsManager.checkForBluetoothEnabledAndDisplayAlert(this,
                    REQUEST_ENABLE_BT,
                    resultCode);
        }
    }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(continue_button);
                    add(main_exit_button);
                }}
        );
    }

    private void initializeModeSelectorSpinner() {
        ArrayAdapter<String> modeSelectorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item,
                new ArrayList<String>() {{
                    add(getString(slave));
                    add(getString(master));
                }}
        );
        this.devicesListView = findViewById(mode_selector_spinner);
        this.devicesListView.setAdapter(modeSelectorAdapter);
    }

    private void navigateToSelectedMode() {
        String mode = this.devicesListView.getSelectedItem().toString();
        if(mode.equals(getString(master))) {
            try {
                Intent intent = new Intent(getApplicationContext(), MasterActivity.class);
                this.startActivity(intent);
            } catch (Exception e) {
                AppUtility.handleException(e, this);
            }
        }
        else if(mode.equals(getString(slave))) {
            try {
                Intent intent = new Intent(getApplicationContext(), SlaveActivity.class);
                this.startActivity(intent);
            } catch (Exception e) {
                AppUtility.handleException(e, this);
            }
        }
    }

    private void exitApplication() {
        AppUtility.exitApplicationWithDefaultAlert(this);
    }
}
