package com.cxp.cyclexpro_v2;

import android.content.Context;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Carlos on 6/25/2016.
 */
public class DataLogger extends Thread{
    File file;
    String fName;
    static FileOutputStream outStream;
    static Boolean sWriting = false;
    static Boolean interrupted;

    public DataLogger(String filename, Context context) {
        interrupted = false;
        try {
            fName = filename; //TODO: Make sure file is visible in file system
            file = new File(context.getFilesDir(), fName);
            Log.i("CheckP", file.getAbsolutePath());
            outStream = new FileOutputStream(file, true);
        } catch (IOException e) {
            Log.e("Except", "File open failed"+e.toString());
        }
    }

    public void run() {
        while (!interrupted) {
            while (sWriting) {
                if(MetricsActivity.isDataNew()) {
                    try {
                        outStream.write((MetricsActivity.sDataString+"\n").getBytes("UTF-8"));
                        MetricsActivity.dataIsOld();
                    } catch (IOException e) {
                        Log.e("Except", "File write failed"+e.toString());
                    }
                }
            }
        }
        try {
            outStream.close();
        } catch (IOException e) {
            Log.e("Except", "File close failed"+e.toString());
        }
    }

    static void stopWriting(){
        sWriting = false;
    }

    static void startWriting(){
        sWriting = true;
    }

    static void finishLog(){
        interrupted = true;
    }

}
