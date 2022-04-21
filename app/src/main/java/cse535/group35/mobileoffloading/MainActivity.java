package cse535.group35.mobileoffloading;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayAdapter<String> modeSelectorAdapter;
    private Spinner devicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerOnClickListenerCallBackForButtons();
        this.initializeModeSelectorSpinner();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case next_button:
                this.navigateToSelectedMode();
                break;
            case exit_button:
                this.exitApplication();
        }
    }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(next_button);
                    add(exit_button);
                }}
        );
    }

    private void initializeModeSelectorSpinner() {
        this.modeSelectorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item,
                new ArrayList<String>() {{
                    add(getString(slave));
                    add(getString(master));
                }}
        );
        this.devicesListView = findViewById(mode_selector_spinner);
        this.devicesListView.setAdapter(this.modeSelectorAdapter);
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
        AppUtility.createExitAlertDialogWithConsentAndExit(this,
                exit_dialog_title,
                exit_dialog_message,
                alert_dialog_yes,
                alert_dialog_no);
    }
}