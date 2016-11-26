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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.DataInputStream;
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
    private static Context context;
    static ProgressDialog progressDialog;
    static Runnable progShow, progDismiss;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        init();
        context = this.getApplicationContext();
        if (sBtAdapter == null) {
            Toast.makeText(getApplicationContext(), "No BT detected", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!sBtAdapter.isEnabled()) {
                turnOnBT();
            }
            getPairedDevices();
            startDiscovery();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Puts Bluetooth in Discovery Mode
     */
    private void startDiscovery() {
        sBtAdapter.cancelDiscovery();
        sBtAdapter.startDiscovery();
    }

    /**
     * Enables Bluetooth
     */
    private void turnOnBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    /**
     * list of BT devices to list of paired devices
     */
    private void getPairedDevices() {
        devicesArray = sBtAdapter.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray) {
                Log.i("Check", device.getName());
                pairedDevices.add(device.getName());
            }
        }
        listAdapter.notifyDataSetChanged();
    }


    /**
     * Initializes Views and Adapter
     */
    private void init() {
        this.tvTitle.setText("Bluetooth Devices");
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        sBtAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devices = new ArrayList<>();


        progressDialog = new ProgressDialog(BluetoothActivity.this);
        progressDialog.setTitle("Connecting");
        progressDialog.setMessage("Connecting to Cycle X-Pro...");

        progShow = new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        };

        progDismiss = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        };

        Log.i("Check", "BT init");

        receiver = new BroadcastReceiver() {

            /** Finds and generates a list of Bluetooth devices */
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Check Action", action);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.i("Check", "DeviceFound");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s = "";
                    for (int a = 0; a < pairedDevices.size(); a++) {
                        if ((device.getName() != null) && (device.getName().length() > 0)) {
                            if (device.getName().equals(pairedDevices.get(a))) {
                                s = "(Paired)";
                                break;
                            }
                        }
                    }
                    listAdapter.add(device.getName() + " " + s + " " + "\n" + device.getAddress());
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if (sBtAdapter.getState() == sBtAdapter.STATE_OFF) {
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
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex) {
            Log.w("Unreg", ex.toString());
        }

    }

    /**
     * If connection is cancelled, Toast tells user to turn on BT
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * @param arg2 the index of the chosen paired device
     *             paired device is passed to the ConnectThread
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.i("Check", "onItemClick");
        if (sBtAdapter.isDiscovering()) {
            sBtAdapter.cancelDiscovery();
        }
        if (listAdapter.getItem(arg2).contains("(Paired)")) {
            BluetoothDevice selectedDevice = devices.get(arg2);
            Log.i("Check", selectedDevice.getName());
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();
            this.runOnUiThread(progShow);
        } else {
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Bluetooth Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * Initializes the Bluetooth socket and creates the connection
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final boolean secure;
        private boolean fallback;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            secure = true;
            fallback = false;

            try {
                if (secure) tmp = mmDevice.createRfcommSocketToServiceRecord(Constants.MY_UUID);
                else tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(Constants.MY_UUID);

            } catch (IOException e) {
                Log.e("ConnectThread", "Error:", e);
            }
            mmSocket = tmp;
        }

        /**
         * Attempts to establish a Bluetooth connection
         */
        public void run() {
            sBtAdapter.cancelDiscovery();

            try {
                //for some reason, it sometimes doesn't work
                mmSocket.connect();
                Log.i("Check", "socket connected");
            } catch (IOException connectException) {
                Log.e("ConnectThread", "connect IOException: ", connectException);
                //added
                try {
                    mmSocket = (BluetoothSocket) mmDevice.getClass()
                            .getMethod("createRfcommSocket", new Class[]{int.class})
                            .invoke(mmDevice, 1);
                    mmSocket.connect();

                    Log.i("Check", "Successful Connection ONE!");

                } catch (Exception e2) {
                    Log.e("No FBConnection", "e2", e2);

                }
            }
            if (mmSocket.isConnected()) Log.i("Check", "Successful Connection TWO!");

            Globals.sBtConnected = true;
            updateConBtn();

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

        /**
         * Cancels the Bluetooth connection
         */
        public void cancel() {
            try {
                if (fallback) mmSocket.close();
                else mmSocket.close();

            } catch (IOException e) {
            }
        }
    }

    /**
     * Maintains the connection, writes, and reads
     */
    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final DataInputStream dinput;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            dinput = new DataInputStream(mmInStream);
        }

        /**
         * Reads bytes from Bluetooth socket
         * Passes messages to the Handler
         */
        public void run() {
            byte[] buffer = new byte[64];
            int checksum = 0;
            int length = 0;
            int protocol = 0;
            int time = 0;
            boolean goodRead = false;
            int misses = 0;

            runOnUI(progShow);
            String connect_confirm = 'C' + String.format("%03d", Globals.sWeight) +
                    Globals.sUsername + '\n' + Globals.sDestTRIOid + '\n';
            sConnectedThread.write(connect_confirm);
            while (true) {
                try {

                    Log.d("Deb", "Reading...");
                    dinput.readFully(buffer, 0, 40);
                    Log.d("dinput", Globals.getHexString(buffer, 40));

                    //bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    //bytes += buffer.length;

                    if (true) {
                        if ((buffer[0] & 0xFF) == 0xA7) {
                            switch (buffer[1]) {
                                case Constants.IDLE_READ:
                                    length = 9;
                                    break;
                                case Constants.COACH_READ:
                                case Constants.DATA_READ:
                                    length = 23;
                                    break;
                                case Constants.HEADER_READ:
                                    length = 30;
                                    break;
                                case Constants.ERPS_READ:
                                    length = 10;
                                    break;
                                case Constants.RACE_READ:
                                    length = 32;
                                    break;
                                //case Constants.COACH_READ:
                                    //length = 17;
                                    //break;
                                case Constants.XB_CONNECT:
                                    length = 13;
                                    break;
                                default:
                                    length = 0;
                            }
                            Log.d("len", Integer.toString(length));
                            for (int j = 0; j < length; j++) {
                                checksum += (buffer[j] & 0xFF);
                            }

                            if ((checksum & 0xFF) == (buffer[length] & 0xFF)) {
                                //checksum to line feed
                                buffer[length] = 0x0A;
                                if (!Globals.sERPSFlag) {
                                    switch (buffer[1]) {
                                        case Constants.ERPS_READ:
                                            Log.d("ERPS", "Good");
                                            Globals.sERPSFlag = true;
                                            BluetoothActivity.sConnectedThread.flush();
                                            BluetoothActivity.sConnectedThread.write(Constants.ERPS_ACK);
                                            //send from start to protocol index
                                            Globals.sHandler
                                                    .obtainMessage(buffer[1], 2, length + 1, buffer)
                                                    .sendToTarget();
                                            break;
                                        case Constants.HEADER_READ:
                                            if (!Globals.sGoodHeaderRead) {
                                                Globals.sGoodHeaderRead = true;
                                                Globals.sHandler
                                                        .obtainMessage(buffer[1], 2, length + 1, buffer)
                                                        .sendToTarget();
                                            }
                                            break;
                                        case Constants.XB_CONNECT:
                                            runOnUI(progDismiss);
                                        case Constants.COACH_READ:
                                        case Constants.DATA_READ:
                                        case Constants.IDLE_READ:
                                        case Constants.RACE_READ:
                                            Globals.sHandler
                                                    .obtainMessage(buffer[1], 2, length + 1, buffer)
                                                    .sendToTarget();
                                            //no break
                                        default:

                                    }
                                }

                                goodRead = true;
                            }
                            checksum = 0;
                        }
                    }

                    //if transmission was good, reset
                    if (goodRead) {
                        goodRead = false;
                    } else {
                        Log.d("Deb", "BadRead");
                        Log.d("Misses", Integer.toString(misses));
                        if (!Globals.sGoodHeaderRead) {
                            sConnectedThread.write(Constants.RETRY_NEW_SESSION);
                        }
                        if (Globals.sSessionOn) {
                            sConnectedThread.write(Constants.SEND_NEXT_SAMPLE);
                        }
                    }

                } catch (IOException e) {
                    Log.e("BTthread", e.toString());
                    break;
                }
            }
            Log.d("Force", "Should never print");
        }

        /**
         * Writes string to Bluetooth socket
         *
         * @param income message is a string
         *               string is converted to byte array
         */
        public void write(String income) {
            byte[] data = income.getBytes();
            try {
                mmOutStream.write(data, 0, data.length);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("Err", "bad write");
            }
        }

        /**
         * flushes output buffer
         **/
        public void flush() {
            try {
                mmOutStream.flush();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
            }
        }

        /**
         * Closes Bluetooth socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * calls the connectedThread.cancel method
     */
    public static void disconnect() {
        if (sConnectedThread != null) {
            sConnectedThread.cancel();
            sConnectedThread = null;
            Globals.sBtConnected = false;
            updateConBtn();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
