package com.cxp.cyclexpro_v2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by Carlos on 6/26/2016.
 */
public class Globals {

    private Globals(){
        //restrict instantiation
    }
    //DataLogger
    protected static boolean sNewData = false;
    protected static String sDataString = "";
    protected static byte[] sBuffer;
    protected static boolean sBtConnected = false;
    protected static boolean sXbConnected = false;
    protected static CustomHandler sHandler;
    protected static SharedPreferences memory = null;
    protected static SharedPreferences.Editor editor;

}
