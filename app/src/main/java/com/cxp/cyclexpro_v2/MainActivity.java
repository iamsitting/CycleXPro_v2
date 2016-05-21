package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends TitleBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tvTitle.setText("Cycle-X Pro");

        Button solobtn = (Button) findViewById(R.id.solobtn);
        Button trainbtn = (Button) findViewById(R.id.trainbtn);
        Button racebtn = (Button) findViewById(R.id.racebtn);

        //solobtn listener
        solobtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MetricsActivity.class);
                startActivity(intent);
            }
        });

        //trainbtn listener
        trainbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MetricsActivity.class);
                startActivity(intent);
            }
        });

        //racebtn listener
        racebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MetricsActivity.class);
                startActivity(intent);
            }
        });
    }
}
