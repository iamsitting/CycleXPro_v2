/*
 * Copyright (C) 2016 Carlos Salamanca (@iamsitting)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy modify, merge publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTIBILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

/*
 * @author Carlos Salamanca
 * @version 2.0.0
 */

package com.cxp.cyclexpro_v2;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * This activity manages the Bluetooth Connection.
 */

public class BluetoothActivity extends Activity implements AdapterView.OnItemClickListener {

    /** calls the connectedThread.cancel method */
    public static void disconnect(){
        if(sConnectedThread != null){
            sConnectedThread.cancel();
            sConnectedThread=null;
        }
    }


    static ConnectedThread sConnectedThread;

    ListView listView;
    ArrayAdapter<String> listAdapter;
    static BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> devices;
    IntentFilter filter;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        init();
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(), "No BT detected", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if(!btAdapter.isEnabled()){
                turnOnBT();
            }
            getPairedDevices();
            startDiscovery();
        }
    }

    /** Puts Bluetooth in Discovery Mode */
    private void startDiscovery(){
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    /** Enables Bluetooth */
    private void turnOnBT(){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    /** list of BT devices to list of paired devices */
    private void getPairedDevices(){
        devicesArray = btAdapter.getBondedDevices();
        if(devicesArray.size() > 0){
            for(BluetoothDevice device:devicesArray){
                Log.i("Check", device.getName());
                pairedDevices.add(device.getName());
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    /** Initializes Views and Adapter */
    private void init(){
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devices = new ArrayList<BluetoothDevice>();
        Log.i("Check", "BT init");

        receiver = new BroadcastReceiver() {

            /** Finds and generates a list of Bluetooth devices */
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Check Action", action);

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    Log.i("Check", "DeviceFound");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s = "";
                    for(int a = 0; a < pairedDevices.size(); a++){
                        if((device.getName() != null) && (device.getName().length() > 0)){
                            if(device.getName().equals(pairedDevices.get(a))){
                                s = "(Paired)";
                                break;
                            }
                        }
                    }
                    listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(btAdapter.getState() == btAdapter.STATE_OFF){
                        turnOnBT();
                    }
                }
            }
        };

        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(receiver, filter);
        Log.i("Check", "End init()");

    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }

    /** If connection is cancelled, Toast tells user to turn on BT */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * @param arg2  the index of the chosen paired device
     * paired device is passed to the ConnectThread
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
        Log.i("Check", "onItemClick");
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
            Log.i("Check", "Discovery is cancelled");
        }
        if(listAdapter.getItem(arg2).contains("(Paired)")){
            BluetoothDevice selectedDevice = devices.get(arg2);
            Log.i("Check", selectedDevice.getName());
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();
            Log.i("Check", "ConnectThread.start()");
        } else {
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initializes the Bluetooth socket and creates the connection
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final boolean secure;
        private boolean fallback;
        private BluetoothSocket fbsocket;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;
            secure = true;
            fallback = false;

            try {
                if (secure) tmp = mmDevice.createRfcommSocketToServiceRecord(Constants.MY_UUID);
                else tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(Constants.MY_UUID);

                Log.i("Check", "create rfcommsocket");
            } catch(IOException e){
                Log.e("ConnectThread", "Error:", e);
            }
            mmSocket = tmp;
        }

        /** Attempts to establish a Bluetooth connection */
        public void run(){
            btAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
                Log.i("Check", "socket connected");
            } catch (IOException connectException){
                Log.e("ConnectThread", "connect IOException: ", connectException);
                //added
                try{
                    Log.i("Check", "trying fallback");
                    if (mmDevice == null) Log.i("Check", "Device is null");
                    Log.i("Check", mmDevice.getName());
                    mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                    if (mmSocket == null) Log.i("Check", "Socket is null");
                    mmSocket.connect();

                    //mmSocket = (BluetoothSocket) mmDevice.getClass()
                    //        .getMethod("createRfcommSocket",
                    //               new Class[] {int.class})
                    //        .invoke(mmDevice, 1);
                    //mmSocket.connect();

                    Log.i("Check", "Successful Connection ONE!");

                } catch (Exception e2){
                    Log.e("No FBConnection", "e2", e2);

                }
                //added
            }
            if (mmSocket.isConnected()) Log.i("Check", "Successful Connection TWO!");
            /*Log.i("Check", "trying fallback");
            String sec;
            if (secure) sec = "";
            else sec = "Insecure";

            for(Integer port = 1; port <= 5; port++){
                try{
                    btAdapter.cancelDiscovery();
                    Class<?> clazz = mmSocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                    Method m = clazz.getMethod("create"+sec+"RfcommSocket",
                            paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(port)};
                    fbsocket = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                    fbsocket.connect();
                    Log.i("Check", "Connection");
                    fallback = true;
                    break;
                } catch (NoSuchMethodException | InvocationTargetException |
                        IllegalAccessException ex){
                    Log.e("FBConnectThread", "Exception: ", ex);
                } catch (IOException ex) {
                    Log.e("FBConnectThread", "IOException: ", ex);
                    try{
                        mmSocket.close();
                    } catch (IOException e){}
                }
            }*/
            if (fallback) {
                MainActivity.sHandler
                        .obtainMessage(Constants.SUCCESS_CONNECT, mmSocket)
                        .sendToTarget();
                Log.i("Check", "connected to fallback");
            } else {
                if (mmSocket == null) Log.i("Check", "Socket is null");
                MainActivity.sHandler
                        .obtainMessage(Constants.SUCCESS_CONNECT, mmSocket)
                        .sendToTarget();
            }
        }

        /** Cancels the Bluetooth connection */
        public void cancel(){
            try{
                if (fallback) fbsocket.close();
                else mmSocket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * Maintains the connection, writes, and reads
     */
    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**
         * Reads bytes from Bluetooth socket
         * Passes messages to the Handler
         */
        public void run(){
            byte[] buffer;
            int bytes;

            while(true){
                try{
                    try{
                        sleep(30);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    MainActivity.sHandler
                            .obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e){
                    break;
                }
            }
        }

        /** Writes string to Bluetooth socket
         * @param income    message is sent as string */
        public void write(String income){
            try{
                mmOutStream.write(income.getBytes());
                try{
                    Thread.sleep(20);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            } catch (IOException e) {}
        }

        /** Closes Bluetooth socket */
        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e){}
        }
    }
}
