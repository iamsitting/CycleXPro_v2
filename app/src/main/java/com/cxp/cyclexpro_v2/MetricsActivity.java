package com.cxp.cyclexpro_v2;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

public class MetricsActivity extends TitleBarActivity {
    public static Handler mHandler;

    //setup GraphView
    static LinearLayout GraphView;
    static GraphView graphView;
    static Series mSeries;
    private static double graph2LastXValue = 0;
    private static int Xview = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);

        LinearLayout background = (LinearLayout) findViewById(R.id.bg);
        background.setBackground(Color.BLACK);

        init();
    }

    void init(){
        GraphView = (LinearLayout) findViewById(R.id.Graph);
        Series = new Series("Signal", )
    }
}
