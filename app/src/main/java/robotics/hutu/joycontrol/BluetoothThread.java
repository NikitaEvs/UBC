package robotics.hutu.joycontrol;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Поток установки и поддержания соединения с клиентом
 * В случае успешного подключения запускает BluetoothConnectedThread
 */
public class BluetoothThread extends Thread {
    private String MACaddress = null;
    private final String tempUUID = "00001101-0000-1000-8000-00805F9B34FB";
    private UUID myUUID;
    private String nameCustom = null;
    private final Context context;
    private Activity activity;
    private DrawThread drawThread;
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothConnectedThread bluetoothConnectedThread;
    private boolean flgGet = false;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Handler handler;
    private boolean flgRun = true;
    long profileID;

    public BluetoothThread(final Context context, final Activity activity, Handler handler, long profileID) {
        this.context = context;
        this.activity = activity;
        this.handler = handler;
        this.profileID = profileID;
        myUUID = UUID.fromString(tempUUID);
    }

    public BluetoothThread(final Context context, final Activity activity, String MACaddress, Handler handler, long profileID) {
        this.context = context;
        this.activity = activity;
        this.MACaddress = MACaddress;
        this.nameCustom = MACaddress;
        this.handler = handler;
        this.profileID = profileID;
        myUUID = UUID.fromString(tempUUID);
        Log.d("Name", nameCustom);
    }
    public BluetoothThread(final Context context, final Activity activity, String nameCustom, Handler handler, long profileID, boolean name) {
        this.context = context;
        this.activity = activity;
        this.nameCustom = nameCustom;
        this.handler = handler;
        this.profileID = profileID;
        myUUID = UUID.fromString(tempUUID);
        Log.d("Name", nameCustom);
    }

    public void setDrawThread(DrawThread drawThread) {
        this.drawThread = drawThread;
        flgGet = true;
        sendBTCThread(bluetoothConnectedThread);
        Log.d("Info", "SetDraw");
    }

    /**
     * Функция комплексного поиска устройств
     */
    public void findDevice() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("Info", "FindDevice");
        findInPairedDevices();
        if (MACaddress != null) {
            Log.d("Info", "MAC: " + MACaddress + "");
            sendToast("Info", MACaddress+"");
        } else {
            Log.d("Info", "Find in Available");
            findInAvailableDevices(nameCustom);
        }
    }

    /**
     * Фунция поиска необходимого устройства в подключенных
     */
    public void findInPairedDevices() {
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        Log.d("Info", "Find in paired");
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(nameCustom)){
                    MACaddress = device.getAddress();
                } else if ((device.getName().equals("HC-05")) || (device.getName().equals("HC-04")) || (device.getName().equals("HC-06"))) {
                    MACaddress = device.getAddress();
                }
            }
        }
    }

    /**
     * Функция поиска необходимого устройства в доступных рядом
     * @param name
     */
    public void findInAvailableDevices(final String name) {
        adapter.cancelDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    new Logger(context, "Info").log(device.getName());
                    if (device.getName().equals(name)) {
                        MACaddress = device.getAddress();
                        //Toast.makeText(context, "Found " + MACaddress, Toast.LENGTH_SHORT).show();
                        adapter.cancelDiscovery();
                    } else if ((device.getName().equals("HC-05")) || (device.getName().equals("HC-04")) || (device.getName().equals("HC-06"))) {
                        MACaddress = device.getAddress();
                        //Toast.makeText(context, "Found " + MACaddress, Toast.LENGTH_SHORT).show();
                        adapter.cancelDiscovery();
                    }
                }
            }
        };
        activity.registerReceiver(broadcastReceiver, filter);
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        adapter.startDiscovery();
        //Toast.makeText(context, "Search devices...", Toast.LENGTH_SHORT).show();
        /*Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                if (MACaddress == null) {
                    errorInSearch();
                    activity.unregisterReceiver(broadcastReceiver);
                    adapter.cancelDiscovery();
                }
            }
        };
        handler.postDelayed(r, 20000);*/
        errorThread.start();
    }

    /**
     * Функция ошибки в поиске
     */
    public void errorInSearch() {
        adapter.cancelDiscovery();
        activity.unregisterReceiver(broadcastReceiver);
        Intent intent = new Intent(activity, SearchErrorActivity.class);
        intent.putExtra("ID", profileID+"");
        context.startActivity(intent);
    }

    public void errorInConnection() {
        adapter.cancelDiscovery();
        Intent intent = new Intent(activity, SearchErrorActivity.class);
        context.startActivity(intent);
    }

    /**
     * Функция поддержания подключения к устройству
     * Позволяет в кратчайшие сроки переподключаться при потере соединения
     */
    @Override
    public void run() {
        while (flgRun){
            Log.d("Info", "BluetoothThread.Run "+MACaddress);
            if (MACaddress == null) {
                Log.d("Info", "Find");
                findDevice();
            } else {
                Log.d("Info", "runSocket");
                try {
                    BluetoothDevice device = adapter.getRemoteDevice(MACaddress);
                    try {
                        socket = device.createRfcommSocketToServiceRecord(myUUID);
                    } catch (Exception e) {
                        new Logger(context, "Error").log("Error creating socket");
                    }
                    if (adapter.isDiscovering()) {
                        adapter.cancelDiscovery();
                    }
                    try {
                        socket.connect();
                        sendToast("Info", "Connect");
                        new Logger(context, "Info").log("Success in connection");
                        bluetoothConnectedThread = new BluetoothConnectedThread(socket, context, profileID);
                        sendBTCThread(bluetoothConnectedThread);
                        bluetoothConnectedThread.start();
                    } catch (IOException e) {
                        /*try {
                            //socket.close();
                            Log.d("Kill", "Kill BT Thread");
                        } catch (IOException e1) {
                            Log.d("Info", "Error in close");
                            e.printStackTrace();
                        } catch (NullPointerException e2){
                            e2.printStackTrace();
                        }*/
                    }
                    if (socket.isConnected()) {
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void sendBTCThread(BluetoothConnectedThread bluetoothConnectedThread) {
        try {
            drawThread.setBluetoothConnectedThread(bluetoothConnectedThread);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void sendToast(String tag, String msg){
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(tag, msg);
        message.setData(bundle);
        handler.sendMessage(message);
    }
    public void kill(){
        flgRun = false;
    }

    /**
     * Вспомогательгный поток ожидания подключения
     */
    Thread errorThread = new Thread(){
        @Override
        public void run(){
            try {
                boolean run = true;
                while(run) {
                    sleep(20000);
                    if(MACaddress == null){
                        errorInSearch();
                        activity.unregisterReceiver(broadcastReceiver);
                        adapter.cancelDiscovery();
                    }
                    run = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    /*
    public void connectToDevice() {
        if (MACaddress != null) {
            try{
                activity.unregisterReceiver(broadcastReceiver);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            if (!BluetoothAdapter.checkBluetoothAddress(MACaddress)) {
                findInAvailableDevices(MACaddress);
            }
            if (!BluetoothAdapter.checkBluetoothAddress(MACaddress)) {
                errorInConnection();
            } else {
                BluetoothDevice device = adapter.getRemoteDevice(MACaddress);
                try {
                    socket = device.createRfcommSocketToServiceRecord(myUUID);
                    socket.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorInConnection();
                }

            }
        }
    }*/
}
