package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TitleBarActivity extends Activity {
    protected Button conbtn;
    protected TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_title_bar);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);

        conbtn = (Button) findViewById(R.id.conbtn);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
    }
}
