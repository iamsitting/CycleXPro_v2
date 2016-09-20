/*
 * This file is licensed under MIT
 *
 * The MIT License (MIT)
 *
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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.Set;

/**
 * This activity manages the Bluetooth Connection.
 */

public class BluetoothActivity extends TitleBarActivity implements AdapterView.OnItemClickListener {

    static ConnectedThread sConnectedThread;

    ListView listView;
    ArrayAdapter<String> listAdapter;
    static BluetoothAdapter sBtAdapter;
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
        if(sBtAdapter == null){
            Toast.makeText(getApplicationContext(), "No BT detected", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if(!sBtAdapter.isEnabled()){
                turnOnBT();
            }
            getPairedDevices();
            startDiscovery();
        }
    }

    /** Puts Bluetooth in Discovery Mode */
    private void startDiscovery(){
        sBtAdapter.cancelDiscovery();
        sBtAdapter.startDiscovery();
    }

    /** Enables Bluetooth */
    private void turnOnBT(){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    /** list of BT devices to list of paired devices */
    private void getPairedDevices(){
        devicesArray = sBtAdapter.getBondedDevices();
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
        this.tvTitle.setText("Bluetooth Devices");
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        sBtAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devices = new ArrayList<>();
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
                    if(sBtAdapter.getState() == sBtAdapter.STATE_OFF){
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
        try{
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex){
            Log.w("Unreg", ex.toString());
        }

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
        if(sBtAdapter.isDiscovering()){
            sBtAdapter.cancelDiscovery();
        }
        if(listAdapter.getItem(arg2).contains("(Paired)")){
            BluetoothDevice selectedDevice = devices.get(arg2);
            Log.i("Check", selectedDevice.getName());
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();
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

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;
            secure = true;
            fallback = false;

            try {
                if (secure) tmp = mmDevice.createRfcommSocketToServiceRecord(Constants.MY_UUID);
                else tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(Constants.MY_UUID);

            } catch(IOException e){
                Log.e("ConnectThread", "Error:", e);
            }
            mmSocket = tmp;
        }

        /** Attempts to establish a Bluetooth connection */
        public void run(){
            sBtAdapter.cancelDiscovery();

            try{
                //for some reason, it sometimes doesn't work
                mmSocket.connect();
                Log.i("Check", "socket connected");
            } catch (IOException connectException){
                Log.e("ConnectThread", "connect IOException: ", connectException);
                //added
                try{
                    mmSocket = (BluetoothSocket) mmDevice.getClass()
                            .getMethod("createRfcommSocket", new Class[] {int.class})
                            .invoke(mmDevice,1);
                    mmSocket.connect();

                    Log.i("Check", "Successful Connection ONE!");

                } catch (Exception e2){
                    Log.e("No FBConnection", "e2", e2);

                }
            }
            if (mmSocket.isConnected()) Log.i("Check", "Successful Connection TWO!");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Globals.sBtConnected = true;
                    updateConBtn();
                    //updateConnectionStatus(true);
                }
            });

            if (fallback) {
                Globals.sHandler
                        .obtainMessage(Constants.SUCCESS_CONNECT, mmSocket)
                        .sendToTarget();
                Log.i("Check", "connected to fallback");
            } else {
                Globals.sHandler
                        .obtainMessage(Constants.SUCCESS_CONNECT, mmSocket)
                        .sendToTarget();
            }
        }

        /** Cancels the Bluetooth connection */
        public void cancel(){
            try{
                if (fallback) mmSocket.close();
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
            byte[] buffer = new byte[1024];
            int bytes = 0;
            int begin = 0;//start of buffer iterator
            int start = 0;//start of message
            int checksum = 0;
            int length = 0;
            int protocol = 0;
            boolean goodRead = false;

            while(true){
                try{
                    try{
                        sleep(30);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    //Add bytes to buffer
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);

                    //iterate from begin to end acc. bytes
                    for(int i =  begin; i < bytes; i++){
                        //find terminating byte
                        if((buffer[i] & 0xFF) == 0xA7){
                            //terminating byte should be deep into the buffer
                            Log.d("term8", Integer.toString(i));
                            if(i > 2){
                                protocol = buffer[i-1]; //get protocol byte
                                switch (protocol){
                                    case 1:
                                        length = 18;
                                        break;
                                    case 2:
                                        length = 19;
                                        break;
                                    case 3:
                                        length = 8;
                                    default:
                                        length = 0;
                                }
                                Log.d("len", Integer.toString(length));
                                //starting index of received message within the buffer
                                start = i - 2 - length;
                                //make sure the starting index is not negative
                                if(start >= 0){
                                    //iterate through message for checksum
                                    for(int j=0; j < length; j++){
                                        checksum += (buffer[start+j] & 0xFF);
                                    }

                                    //compare checksums
                                    if(( (byte) checksum ) == (buffer[i-2])){

                                        //protocol to line feed
                                        buffer[i-2] = 0x0A;

                                        Globals.sHandler
                                                .obtainMessage(protocol, start, i-1, buffer)
                                                .sendToTarget();

                                        if(protocol == Constants.HEADER_READ){
                                            Globals.sGoodHeaderRead = true;
                                        }

                                        goodRead = true;
                                    }
                                    checksum = 0;

                                }
                            }
                            //if buffer fills up, start at the beginning
                            if((i+1) == bytes){
                                bytes = 0;
                                begin = 0;
                            } else { //else continue where i left off
                                begin = i+1;
                            }
                        }
                    }

                    //if transmission was good, reset
                    if(goodRead){
                        goodRead = false;
                    } else { //else request a "resend"
                        if(!Globals.sGoodHeaderRead){
                            sConnectedThread.write(Constants.NEW_SESSION);
                        } else {
                            sConnectedThread.write(Constants.SEND_NEXT_SAMPLE);
                        }
                    }


                } catch (IOException e){
                    Log.e("BTthread", e.toString());
                    break;
                }
            }
        }

        /** Writes string to Bluetooth socket
         * @param income    message is a string
         * string is converted to byte array */
        public void write(String income){
            byte [] data = income.getBytes();
            try{
                mmOutStream.write(data,0, data.length);
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

    /** calls the connectedThread.cancel method */
    public static void disconnect(){
        if(sConnectedThread != null){
            sConnectedThread.cancel();
            sConnectedThread=null;
            Globals.sBtConnected = false;
            updateConBtn();
            //TODO: Fix BT icon to update on disconnect
            //updateConnectionStatus(false);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
