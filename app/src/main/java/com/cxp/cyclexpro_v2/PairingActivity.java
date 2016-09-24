package com.cxp.cyclexpro_v2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PairingActivity extends TitleBarActivity implements View.OnClickListener{

    Button btXbConnect;
    EditText etPanId;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        init();

    }

    public void init(){
        btBtConnection.setOnClickListener(this);
        btXbConnect = (Button) findViewById(R.id.btXbConnect);
        btXbConnect.setOnClickListener(this);
        etPanId = (EditText) findViewById(R.id.etPanId);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting");
        progressDialog.setMessage("Connecting to trainer...");

    }

    public boolean isValid(){
        //check if entry is valid
        String entry = etPanId.getText().toString();
        //id should contain 4 numbers
        if (entry.length() != 4){
            return false;
        } //id should not start with 0
        else if(entry.startsWith("0")){
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onClick(View v){
        Class cl;
        switch (v.getId()){
            case R.id.btXbConnect:

                //check if connected to bluetooth
                if (Globals.sBtConnected) {
                    //check whether entry is valid
                    if(isValid()){
                        progressDialog.show();

                        cl = MetricsActivity.class;
                        final Intent intent = new Intent(this, cl);
                        intent.putExtra("Mode", "TRAINEE");

                        //this runnable attempts to pair XBees
                        //TODO: Debug XB pairing
                        Runnable progRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String panId = etPanId.getText().toString();
                                BluetoothActivity.sConnectedThread.write("id"+panId);
                                int time = 0;

                                //sXbConnected is static field, will change when XB connects
                                //times out after a time
                                //TODO: set correct timeout
                                while (!Globals.sXbConnected || time < Constants.XB_TIMEOUT){
                                    time += 1;
                                }
                                progressDialog.cancel();
                                startActivity(intent);
                            }
                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.post(progRunnable);

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid ID",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Not Connected to Cycle X-Pro",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.conbtn:
                if(!Globals.sBtConnected){
                    cl = BluetoothActivity.class;
                    startActivity(new Intent(this, cl));
                } else{

                    new AlertDialog.Builder(this)
                            .setTitle("Disconnecting Bluetooth")
                            .setMessage("Are you sure you want to disconnect?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    BluetoothActivity.disconnect();
                                }
                            }).setNegativeButton("No", null)
                            .show();
                }
                btBtConnection.invalidate();
                break;
            default:
        }
    }
}
