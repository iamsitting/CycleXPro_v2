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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * App launches into this activity
 * Presents mode of operations
 */
public class MainActivity extends TitleBarActivity implements View.OnClickListener{
    //public static CustomHandler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateInit();
        init();
    }


    /** initializes the Views*/
    void init(){

        this.tvTitle.setText("Cycle-X Pro");
        btBtConnection.setOnClickListener(this);
        btMenu.setOnClickListener(this);
        Button solobtn = (Button) findViewById(R.id.solobtn);
        solobtn.setOnClickListener(this);
        Button trainbtn = (Button) findViewById(R.id.trainbtn);
        trainbtn.setOnClickListener(this);
        Button racebtn = (Button) findViewById(R.id.racebtn);
        racebtn.setOnClickListener(this);
        Globals.sHandler = new CustomHandler(getApplicationContext());
    }

    /**
     * Listens to a button click
     * Launches appropriate activity
     * @param v     an Android View (Button)
     */
    @Override
    public void onClick(View v){
        Class cl;
        switch(v.getId()){
            case R.id.solobtn:
                Intent intent = new Intent(this, MetricsActivity.class);
                intent.putExtra("Mode", "SOLO");
                startActivity(intent);
                break;
            case R.id.trainbtn:
                cl = TrainingActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.racebtn:
                cl = MetricsActivity.class;
                startActivity(new Intent(this, cl));
                break;
            case R.id.conbtn:
                if(!Globals.sBtConnected){
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
            case R.id.btMenu:
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), btMenu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.itemSettings:
                                Toast.makeText(getApplicationContext(), "Setting page not done ", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.itemPushData:
                                startActivity(new Intent(MainActivity.this, DataListActivity.class));
                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();
                break;
            default:
                Log.i("Check", "def...");
        }
    }

    /** Loads the locally stored date and session number **/
    public void dateInit(){
        Globals.memory = getSharedPreferences(Constants.PREFS_NAME, 0);
        Globals.editor = Globals.memory.edit();

        String savedDate = Globals.memory.getString(Constants.PREFS_KEY_DATE,
                Constants.DATE_NOT_EXISTS);
        int savedSession = Globals.memory.getInt(Constants.PREFS_KEY_SESSION,
                Constants.SESH_NOT_EXISTS);
        String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());

        if(savedDate.equals(Constants.DATE_NOT_EXISTS)){
            Globals.editor.putString(Constants.PREFS_KEY_DATE, today);
            Globals.editor.putInt(Constants.PREFS_KEY_SESSION, 0);
            Globals.editor.apply();
        } else if(!savedDate.equals(today)){
            Globals.editor.putString(Constants.PREFS_KEY_DATE, today);
            Globals.editor.putInt(Constants.PREFS_KEY_SESSION, 0);
            Globals.editor.apply();
        } else if (savedSession == Constants.SESH_NOT_EXISTS){
            Globals.editor.putInt(Constants.PREFS_KEY_SESSION, 0);
            Globals.editor.apply();
            }


    }

}
