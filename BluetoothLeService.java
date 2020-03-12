package com.wattathlon.wattathlon2;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.UUID;

//Service that interacts with BLE device.
public class BluetoothLeService extends Service{

    private static final String TAG = "BluetoothService";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static final String ACTION_GATT_CONNECTED = "com.wattathlon.wattathlon2.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.wattathlon.wattathlon2.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.wattathlon.wattathlon2.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "com.wattathlon.wattathlon2.ACTION_DATA_AVAILABLE";
    public static final String EXTRA_DATA = "com.wattathlon.wattathlon2.EXTRA_DATA";

    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("ce060036-43e5-11e4-916c-0800200c9a66");
    public static final UUID SERVICE_UUID = UUID.fromString("ce060030-43e5-11e4-916c-0800200c9a66");
    public static final UUID CLIENT_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //public Context context;

    private BluetoothAdapter adapter;
    private String address;
    private BluetoothGatt gatt;
    private int connectionState = STATE_DISCONNECTED;
    private byte[]data;
    private boolean connected;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            Log.d(TAG, "getting service");
            return BluetoothLeService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "returning binder");
        return binder;
    }

    public void start(BluetoothDevice device, BluetoothAdapter adapt) {
        adapter = adapt;
        gatt = device.connectGatt(this,false, gattCallback);
    }

    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GATT_CONNECTED);
        filter.addAction(ACTION_GATT_DISCONNECTED);
        filter.addAction(ACTION_DATA_AVAILABLE);
        return filter;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    String intentAction;

                    if(newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(TAG, "Connected to GATT server.");

                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;

                        broadcastUpdate(intentAction);

                        Log.d(TAG, "Attempting to start service discovery");
                        gatt.discoverServices();
                    }
                    else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d(TAG, "Disconnected from GATT server.");

                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;

                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if(status == BluetoothGatt.GATT_SUCCESS) {

                        BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID);
                        Log.d(TAG, characteristic.toString());

                        /*Enable notifications for characteristic. So when the value of
                        characteristic changes, onCharavteristicChanged() is called.
                         */
                        gatt.setCharacteristicNotification(characteristic, true);

                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CONFIG_UUID);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);

                        gatt.readCharacteristic(characteristic);

                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    }

                    else
                        Log.d(TAG, "onServicesDiscovered received: " + status);

                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
                    BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_UUID);
                    //write {1,0} to enable notifications and {0,0} to disable them.
                    characteristic.setValue(new byte[]{1, 0});
                    gatt.writeCharacteristic(characteristic);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    Log.d(TAG, "onCharacteristicChanged: ");
                    gatt.readCharacteristic(characteristic);
                }

                @Override
                public void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d(TAG, "onCharacteristicRead: ");
                        byte data[] = characteristic.getValue();
                        Log.d(TAG, data.toString());
                        Log.d(TAG, "" + convertToWatts(data));

                        broadcastUpdate(ACTION_DATA_AVAILABLE, convertToWatts(data));
                    }
                }
            };

    /*TODO: convert characteristic value to watts and broadcast it to BLEconnection
     TODO: vale to getwatts sto BLEconnection k call getWatts() apo ti ReadingData
     */

    /**Characteristic's data are packed in 15 bytes as follows:
     * Elapsed Time Lo (0.01 sec lsb),
     * Elapsed Time Mid,
     * Elapsed Time High,
     *
     * Stroke Power Lo (watts),
     * Stroke Power Hi,
     *
     * Stroke Calories Lo (cal/hr),
     * Stroke Calories Hi,
     *
     * Stroke Count Lo
     * Stroke Count Hi,
     *
     * Projected Work Time Lo (secs),
     * Projected Work Time Mid,
     * Projected Work Time Hi,
     *
     * Projected Work Distance Lo (meters),
     * Projected Work Distance Mid,
     * Projected Work Distance Hi
     *
     * I need the value of power, so I need bytes with offset 3 and 4.
     */

    public int convertToWatts(byte []data) {
        byte []powerHiLo = {data[3], data[4]};
        int power = 0;

        power = ((data[4] << 8) | (data[3]  & 0x00ff));

        return power;
    }

    public void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        Log.d(TAG, intent.getAction());

        sendBroadcast(intent);
    }

    public void broadcastUpdate(final String action, final int watts) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, watts);
       /* byte[] data = characteristiic.getValue();

        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                    stringBuilder.toString());
        }*/

        sendBroadcast(intent);
    }

}
