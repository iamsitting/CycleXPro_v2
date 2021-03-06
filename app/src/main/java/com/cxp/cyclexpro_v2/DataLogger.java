package com.cxp.cyclexpro_v2;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
            fName = filename;
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
                if(Globals.sNewData) {
                    try {
                        outStream.write(Globals.sBuffer);
                        Globals.sNewData = false;
                        outStream.flush();
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

    String getFileName() {
        return fName;
    }

    static void finishLog(){
        interrupted = true;
    }

}
