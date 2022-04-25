package cse535.group35.mobileoffloading.master;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.AppPermissionsManager;
import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.R;
import cse535.group35.mobileoffloading.RequestType;
import cse535.group35.mobileoffloading.TestMatrix;
import cse535.group35.mobileoffloading.matrixutil.MatrixUtil;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {

    public static Set<ConnectedDevice> connectedDevices = new HashSet<>();
    ArrayList<ConnectedDevice> activeDevices = new ArrayList<>();
    public static final int[][] matrixResult =
            new int[TestMatrix.getMatrixA().length][TestMatrix.getMatrixB().length];
    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final int REQUEST_ENABLE_BT = 137;

    public ArrayAdapter<String> nearbyDevicesAdapter;
    public static ArrayAdapter<String> connectedDevicesAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        nearbyDevicesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        this.connectedDevicesAdaptor = new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        this.registerOnClickListenerCallBackForButtons();
        this.initializeDevicesListView();
        this.checkForResultCompletedAndUpdateResultTextView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case scan_button:
                this.startDiscovery();
                break;
            case master_back_button:
                this.returnToMainActivity();
                break;
            case computeBtn:
                this.startCompute();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length > 0
                && AppPermissionsManager.areAllPermissionsGranted(this)) {
            AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(this,
                    REQUEST_ENABLE_BT);
        } else {
            AppPermissionsManager.requestAllPermissions(this,
                    REQUEST_PERMISSIONS_CODE);
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

    private void checkForResultCompletedAndUpdateResultTextView() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Runnable runnable = () -> {
            while(true) {
                activeDevices.clear();
                for(ConnectedDevice d:connectedDevices){
                    if(d.getDeviceState()!=-1){
                        activeDevices.add(d);
                    }
                }

                boolean isCompleted = true;

                if (activeDevices.isEmpty()) {
                    continue;
                }

                for (ConnectedDevice connectedDevice : activeDevices) {
                    if (!connectedDevice.isCompleted()) {
                        isCompleted = false;
                        break;
                    }
                }

                if (isCompleted) {
                    break;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(()->{
                String result = MatrixUtil.getStringFromMatrix();
                TextView resultView = findViewById(R.id.resultView);
                resultView.setMovementMethod(new ScrollingMovementMethod());
                resultView.setText(result);

            });
        };

        executorService.execute(runnable);
    }

    private void startCompute() {
        activeDevices.clear();
        for(ConnectedDevice d:connectedDevices){
            if(d.getDeviceState()!=-1){
                activeDevices.add(d);
            }
        }if (activeDevices.size() == 0){
            AppUtility.createAndDisplayToast(this, "No active device connected");
            return;
        }

        AppUtility.createAndDisplayToast(this,
                "Computing with "+ activeDevices.size() + " devices");
        int[][] matrix = TestMatrix.getMatrixA();
        int rowsPerDevice = matrix.length/activeDevices.size();
        int currentRow = 0;
        ArrayList<ConnectedDevice> connectedDevicesList = new ArrayList<>(activeDevices);
        for (int j = 0; j< connectedDevicesList.size(); j++) {
            ConnectedDevice device = connectedDevicesList.get(j);
            ArrayList<Integer> rowsToCompute= new ArrayList<>();
            for(int i=0; i<rowsPerDevice; i++){
                rowsToCompute.add(currentRow);
                currentRow++;
            }

            if(j == connectedDevicesList.size() - 1) {
                for(int i=currentRow; i<matrix.length; i++){
                    rowsToCompute.add(currentRow);
                    currentRow++;
                }
            }

            device.setComputeRows(rowsToCompute);
            Payload payload= Payload.fromBytes(
                    new PayloadBuilder().setRequestType(RequestType.COMPUTE_RESULT)
                            .setParameters(matrix, matrix)
                            .setParameters(rowsToCompute)
                            .build());
            Nearby.getConnectionsClient(this).sendPayload(device.getEndpointId(),payload);
        }
    }

    private void startDiscovery() {
        AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(this,
                REQUEST_ENABLE_BT);
        AlertDialog.Builder builder = new AlertDialog.Builder(MasterActivity.this);
        builder.setTitle("Choose a device");
        builder.setAdapter(nearbyDevicesAdapter, (dialogInterface, i) -> {
            String selectedDevice = nearbyDevicesAdapter.getItem(i);
            selectedDevice = selectedDevice.substring(selectedDevice.length()-5, selectedDevice.length()-1);

            AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(MasterActivity.this,
                    REQUEST_ENABLE_BT);
            Nearby.getConnectionsClient(getApplicationContext())
                    .requestConnection("MASTER",
                            selectedDevice,
                            new MasterConnectionLifecycleCallback(MasterActivity.this,
                                    this.connectedDevicesAdaptor,
                                    this.nearbyDevicesAdapter))
                    .addOnSuccessListener(
                            (Void unused) -> {
                                dialogInterface.cancel();
                                Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // Nearby Connections failed to request the connection.
                            });
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
            DiscoveryOptions discoveryOptions =
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
            Nearby.getConnectionsClient(getApplicationContext())
                    .startDiscovery(getResources().getString(nearbyServiceId),
                            new MasterEndpointDiscoveryCallback(this, nearbyDevicesAdapter),
                            discoveryOptions)
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We're discovering!
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // We're unable to start discovering.
                            });
        }

    private void registerOnClickListenerCallBackForButtons() {
        AppUtility.registerButtonOnClickCallBack(this,
                this,
                new ArrayList<Integer>() {{
                    add(scan_button);
                    add(master_back_button);
                    add(computeBtn);
                }}
        );
    }

    private void initializeDevicesListView() {
        ListView devicesListView = findViewById(devices_listview);
        devicesListView.setAdapter(connectedDevicesAdaptor);
    }

    private void returnToMainActivity() {
        AppUtility.finishAndCloseCurrentActivityWithDefaultAlert(this);
    }
}