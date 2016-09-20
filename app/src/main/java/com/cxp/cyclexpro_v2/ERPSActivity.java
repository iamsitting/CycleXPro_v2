package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ERPSActivity extends TitleBarActivity implements View.OnClickListener {
    static TextView tvTimer;
    static Button btCancel;
    private CountDownTimer countDownTimer;
    private final long startTime = 20*1000;
    private final long interval = 1 * 1000;
    private boolean timerStarted = false;
    private boolean timerFinished = false;
    byte[] byteArray;
    String timeOfAccident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erps);
        init();
        if(!timerStarted){
            countDownTimer.start();
            Bundle extras = getIntent().getExtras();
            byteArray = extras.getByteArray("erpsData");
            timeOfAccident = extras.getString("currentTime");
        }
        go();



    }

    public void init(){
        this.tvTitle.setText("Your Metrics");
        this.tvTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        btCancel.setOnClickListener(this);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        countDownTimer = new CustomCountDownTimer(startTime, interval);

    }

    public void go(){

        //TODO: ERPS parser untested
        Log.d("H_array", Globals.getHexString(byteArray));

        //Latitude
        float latitude = ByteBuffer.wrap(byteArray, 0, 4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String lat = String.format("%.6f", latitude);

        //Longitude
        float longitude = ByteBuffer.wrap(byteArray, 4,4)
                .order(ByteOrder.BIG_ENDIAN).getFloat();
        String longi = String.format("%.6f", longitude);

        while(!timerFinished){

        }
        activateERPS(lat, longi);
        timerFinished = false;
    }

    public void activateERPS(String latitude, String longitude){
        //TODO: phoneNo must come from saved emergency contacts
        String phoneNo = "9792042437";
        String message = timeOfAccident+": Carlos has been in an accident. See: "
                + "http://maps.google.com/maps?q="
                +latitude
                +","+longitude;
        //TODO: The SMS code is untested.
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed!", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btCancel:
                if(timerStarted){
                    countDownTimer.cancel();
                }
        }
    }

    public class CustomCountDownTimer extends CountDownTimer {
        //TODO: Timer untested
        public CustomCountDownTimer(long lstartTime, long linterval) {
            super(lstartTime, linterval);
        }

        @Override
        public void onFinish() {
            timerFinished = true;
            tvTimer.setText("Activated!");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvTimer.setText(""+ millisUntilFinished/1000);
        }
    }
    //TODO: What is the exit condition?
}
