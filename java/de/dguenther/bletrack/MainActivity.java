package de.dguenther.bletrack;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    // Tested on a API 23+ device so requestPermission is needed should also run with API 21 if the requestPermission in line 48 is removed
    private static final int M = 23;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecyclerData> myDataset = new ArrayList<RecyclerData>();
    private ArrayList<String> alreadyScanned = new ArrayList<String>();
    BluetoothManager bluetoothManager;

    @RequiresApi(M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"},1);
        // This is for displaying a list
        recyclerView = findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
        // start scanning and advertising
        scanLeDevice();
        advertise();
    }

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;

    private static final long SCAN_PERIOD = 10000;

    @RequiresApi(M)
    void scanLeDevice() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        mScanning = true;
        UUID[] uuids = new UUID[]{UUID.fromString("7823C5DE-BFC9-4BC6-8E60-2280A22FED01")};
        // scanns for device with specified service uuid forever (not recommended, for better power efficency scanning should be limited)
        // when a device is found leScanCallback is called
        bluetoothAdapter.startLeScan(uuids, leScanCallback);

    }
    Context t = this;
    @RequiresApi(M)
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!alreadyScanned.contains(device.getAddress())){
                                //device hasn't been scanned before, add it to list now
                                alreadyScanned.add(device.getAddress());
                                //myDataset.add(new RecyclerData().withTitle(device.toString()).withDescription("test"));
                                //mAdapter.notifyData(myDataset);
                                // connect to device to query unique id, gattCallback is called upon success
                                device.connectGatt(t, false, gattCallback);
                            }
                        }
                    });
                }
            };
    
    @RequiresApi(M)
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //When connection state changes to connected, discover services
            if(newState == BluetoothGatt.STATE_CONNECTED)
                gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (BluetoothGattService service : gatt.getServices()) {
                        if (service.getUuid().toString().equalsIgnoreCase("7823C5DE-BFC9-4BC6-8E60-2280A22FED01")) {
                            // our service has been discovered add it with timestamp and uuid to the list
                            myDataset.add(new RecyclerData().withDescription(Calendar.getInstance().getTime().toString()).withTitle(service.getCharacteristics().get(0).getUuid().toString()));                       
                            mAdapter.notifyData(myDataset);
                        }

                    }
                }
            });
        }

    };

    @RequiresApi(M)
    void advertise(){
        AdvertisingSet currentAdvertisingSet;
        BluetoothLeAdvertiser advertiser =
                BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        byte[] serviceData = UUID.randomUUID().toString().getBytes();
        ParcelUuid uuid = ParcelUuid.fromString("7823C5DE-BFC9-4BC6-8E60-2280A22FED01");
        AdvertiseData data = (new AdvertiseData.Builder()).setIncludeDeviceName(false).addServiceUuid(uuid).build();
        AdvertiseSettings settings = (new AdvertiseSettings.Builder()).setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER).setConnectable(true).setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW).build();

        AdvertiseCallback callback = new AdvertiseCallback() {

        };
        advertiser.startAdvertising(settings, data, callback);



        BluetoothGattServer server = bluetoothManager.openGattServer(t, new BluetoothGattServerCallback() {
            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic){
                myDataset.add(new RecyclerData().withDescription("@" + Calendar.getInstance().getTime().toString()).withTitle(device.getAddress()));
                mAdapter.notifyData(myDataset);
                device.connectGatt(t, false, gattCallback);
            }
        });

        BluetoothGattService service = new BluetoothGattService(UUID.fromString("7823C5DE-BFC9-4BC6-8E60-2280A22FED01"), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(UUID.randomUUID(), BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        service.addCharacteristic(characteristic);
        server.addService(service);



    }

}
