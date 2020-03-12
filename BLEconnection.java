package com.wattathlon.wattathlon2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

//Bluetooth Low Energy set up
public class BLEconnection extends AppCompatActivity {

    private static final long SCAN_PERIOD = 10000; //10 seconds
    private static final String TAG = "BLEconnection";

    public static final UUID PM5_UUID= UUID.fromString("ce060036-43e5-11e4-916c-0800200c9a66");

    ListView deviceList;
    TextView text;
    ImageButton restartBtn;

    private BluetoothAdapter adapter;
    private DeviceListAdapter leDeviceListAdapter;
    private boolean scanning = false;
    private boolean scan;
    private Handler handler = new Handler();
    private BluetoothDevice connectedDevice;
    private IBinder binder;
    private Intent bindIntent;

    public BluetoothGatt bluetoothGatt;
    public BluetoothGattCallback gattCallback;
    public BluetoothLeService bluetoothLeService;
    public ArrayList<BluetoothDevice> devices;
    public boolean secondCall;
    public int power;
    public boolean connected;

    //BroadcastReceiver for the state of bluetooth adapter.
    private final BroadcastReceiver Receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(adapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, adapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "Receiver: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Receiver: STATE ON");
                        checkPermissions();
                        scanLedevices(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Receiver: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    //Broadcast Receiver for receiving intents sent from BluetoothLeService.
    public final BroadcastReceiver GattReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                Log.d(TAG, "GattReceiver: Gatt connected.");
                connected = true;
            } else if(action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                Log.d(TAG, "GattReceiver: Gatt disconnected.");
                connected = false;
            } else if(action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.d(TAG, "GattReceiver: Services discovered.");
            }else if(action.equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                Log.d(TAG, "GattReceiver: Data available.");
                power = intent.getIntExtra(BluetoothLeService.EXTRA_DATA,0);
                Log.d(TAG, "Extra data: " + power);

                Intent toReadingData = new Intent("POWER");
                toReadingData.putExtra("NEW_VALUE", power);
                sendBroadcast(toReadingData);

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!secondCall)
            unregisterReceiver(Receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleconnection);

        /*The next activity extends this one, so when ReadingData intent is started
        onCreate() method of this intent gets called. secondCall boolean is a boolean flag
        to check onCreate() has been called again and gets true right super.onCreate(...) get called
        in the next activity.
         */
        if(!secondCall) {

            deviceList = (ListView) findViewById(R.id.deviceList);
            text = (TextView) findViewById(R.id.text);
            restartBtn = (ImageButton) findViewById(R.id.restart);

            restartBtn.setImageResource(R.drawable.ic_refresh_black_24dp);
            restartBtn.setVisibility(View.INVISIBLE);

            restartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scanLedevices(true);
                }
            });

            adapter = BluetoothAdapter.getDefaultAdapter();
            devices = new ArrayList<>();

            IntentFilter btIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(Receiver, btIntent);

            /*In case bluetooth is not enabled, a dialog is displayed requesting user permission
            to enable it.
            */
            if (adapter == null || !adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }

            //if bluetooth is on, start scanning for BLE devices
            if(adapter.isEnabled()) {
                checkPermissions();
                scanLedevices(true);
            }


            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    connectedDevice = devices.get(i);

                    //Starts and binds service
                    bindIntent = new Intent(BLEconnection.this, BluetoothLeService.class);
                    getApplicationContext().startService(bindIntent);
                    getApplicationContext().bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);

                    Intent readingData = new Intent(getApplicationContext(), ReadingData.class);
                    startActivity(readingData);
                }
            });
        }
    }

    //If Android version is greater than lollipop we need to check some permission in the manifest file.
    private void checkPermissions(){
        if(Build.VERSION.SDK_INT >  Build.VERSION_CODES.LOLLIPOP) {
            int permissions = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            int permissions2 = this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if(permissions != 0 || permissions2 != 0)
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    private void scanLedevices(boolean enable) {
        if(enable) {

            //Stops scanning after defined period, because scanning is battery-intensive.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "stopped scanning.");

                    if (devices.isEmpty()) {
                        text.setText("No devices found.");
                    } else {
                        text.setText("Devices found: ");
                    }

                    restartBtn.setVisibility(View.VISIBLE);

                    scanning = false;
                    adapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            Log.d(TAG, "scanning...");

            text.setText("Scanning for devices...");
            restartBtn.setVisibility(View.INVISIBLE);

            scanning = true;
            adapter.startLeScan(leScanCallback);
        }
        else {
            Log.d(TAG, "stopped scanning.");

            if (devices.isEmpty()) {
                text.setText("No devices found.");
            } else {
                text.setText("Devices found: ");
            }

            restartBtn.setVisibility(View.VISIBLE);

            scanning = false;
            adapter.stopLeScan(leScanCallback);
        }

    }

    //deliver BLE scan results
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!devices.contains(device)) {
                                Log.d(TAG, "New device found.");

                                devices.add(device);
                                leDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_list_view, devices);
                                deviceList.setAdapter(leDeviceListAdapter);
                            }
                        }
                    });
                }
            };

    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");

            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            registerReceiver(GattReceiver, bluetoothLeService.getIntentFilter());

            /*Connects to the GATT server hosted by BLE device. The caller (the app) is the
             the GATT client.
            */
            bluetoothLeService.start(connectedDevice, adapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}
