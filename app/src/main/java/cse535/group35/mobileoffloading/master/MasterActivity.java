package cse535.group35.mobileoffloading.master;

import static cse535.group35.mobileoffloading.R.id.*;
import static cse535.group35.mobileoffloading.R.string.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import cse535.group35.mobileoffloading.AppUtility;
import cse535.group35.mobileoffloading.AppPermissionsManager;
import cse535.group35.mobileoffloading.ConnectedDevice;
import cse535.group35.mobileoffloading.PayloadBuilder;
import cse535.group35.mobileoffloading.R;
import cse535.group35.mobileoffloading.RequestType;
import cse535.group35.mobileoffloading.TestMatrix;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {
    public ArrayAdapter<String> nearbyDevicesAdapter;
    public ArrayAdapter<String> connectedDevicesAdaptor;
    public static List<ConnectedDevice> connectedDeviceList;

    private static int[][] matrixResult = new int[3][3];
    private Button computeButton;
    private TextView resultView;
    private static final int REQUEST_PERMISSIONS_CODE = 27;
    private static final int REQUEST_ENABLE_BT = 137;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        nearbyDevicesAdapter=new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        connectedDevicesAdaptor=new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item);
        this.registerOnClickListenerCallBackForButtons();
        this.initializeDevicesListView();
        computeButton= (Button) findViewById(computeBtn);

        connectedDeviceList= new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    boolean isCompleted = true;
                    if(MasterActivity.connectedDeviceList.isEmpty()) {
                        continue;
                    }

                    for(ConnectedDevice connectedDevice : MasterActivity.connectedDeviceList) {
                        if(!connectedDevice.isCompleted()) {
                            isCompleted = false;
                            break;
                        }
                    }
                    if(isCompleted) {
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("COMPLETED");
                runOnUiThread(()->{
                    String result = getStringFromMatrix(MasterActivity.matrixResult);
                    resultView = findViewById(R.id.resultView);
                    resultView.setText(result);
                    Toast.makeText(MasterActivity.this, "COMPLETED\n" + result, Toast.LENGTH_SHORT).show();
                });
            }
        };
        executorService.execute(runnable);
        

    }

    private String getStringFromMatrix(int[][] matrixResult) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int[] row : matrixResult) {
            for(int num : row) {
                stringBuilder.append(num).append(" ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
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
                startCompute();
        }
    }

    private void startCompute() {

        if(connectedDeviceList.size()==0){
            Toast.makeText(this, "No device connected", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Computing with "+connectedDeviceList.size()+" devices", Toast.LENGTH_SHORT).show();
        int[][] matrix= TestMatrix.getMatrixA();
        int rowsPerDevice= matrix.length/connectedDeviceList.size();
        int currentRow=0;
        for(int j = 0; j < connectedDeviceList.size(); j++){
            ConnectedDevice device = connectedDeviceList.get(j);
            ArrayList<Integer> rowsToCompute= new ArrayList<>();
            for(int i=0;i<rowsPerDevice;i++){
                rowsToCompute.add(currentRow);
                currentRow++;
            }
            if(j == connectedDeviceList.size()- 1) {
                for(int i=currentRow;i<matrix.length;i++){
                    rowsToCompute.add(currentRow);
                    currentRow++;
                }
            }
            device.setComputeRows(rowsToCompute);
            Payload payload= Payload.fromBytes(new PayloadBuilder().setRequestType(RequestType.COMPUTE_RESULT)
                    .setParameters(matrix,matrix)
                    .setParameters(rowsToCompute)
                    .build());
            Nearby.getConnectionsClient(this).sendPayload(device.getEndpointId(),payload);



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

    private void startDiscovery() {
        AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(this,
                REQUEST_ENABLE_BT);
        AppUtility.createAndDisplayToast(this, "Starting Discovery");
        AlertDialog.Builder builder = new AlertDialog.Builder(MasterActivity.this);
        builder.setTitle("Choose a device");
        // add a list


        builder.setAdapter(nearbyDevicesAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedDevice=nearbyDevicesAdapter.getItem(i);
                AppPermissionsManager.checkForBluetoothEnabledAndTakeAction(MasterActivity.this,
                        REQUEST_ENABLE_BT);
                Nearby.getConnectionsClient(getApplicationContext())
                        .requestConnection("MASTER", selectedDevice, new MasterConnectionLifecycleCallback(MasterActivity.this,
                                connectedDevicesAdaptor,
                                connectedDeviceList,
                                matrixResult))
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    dialogInterface.cancel();
                                    Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // Nearby Connections failed to request the connection.
                                });
            }
        });


        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
            DiscoveryOptions discoveryOptions =
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
            Nearby.getConnectionsClient(getApplicationContext())
                    .startDiscovery(getResources().getString(nearbyServiceId), new MasterEndpointDiscoveryCallback(this, nearbyDevicesAdapter), discoveryOptions)
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