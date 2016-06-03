package com.cxp.cyclexpro_v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TrainingActivity extends TitleBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        init();
    }

    public void init(){
        this.tvTitle.setText("Training Mode");
        conbtn.setOnClickListener(this);
        Button btTrainer = (Button) findViewById(R.id.btTrainer);
        btTrainer.setOnClickListener(this);
        Button btCyclists = (Button) findViewById(R.id.btCyclist);
        btCyclists.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Class cl;
        switch (v.getId()){
            case R.id.btTrainer:
                cl = MetricsActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.btCyclist:
                cl = MetricsActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.conbtn:
                if(!sBtConnected){
                    cl = BluetoothActivity.class;
                    startActivity(new Intent(this, cl));
                    break;
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
                    break;
                }
            default:
                //nothing
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}

