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
    //General
    protected static CustomHandler sHandler;
    protected static SharedPreferences memory = null;
    protected static SharedPreferences.Editor editor;

    //DataLogger
    protected static boolean sNewData = false;
    protected static String sDataString = "";
    protected static byte[] sBuffer;

    //Bluetooth
    protected static boolean sGoodHeaderRead = false;
    protected static boolean sBtConnected = false;
    protected static boolean sXbConnected = false;

    //Debug
    protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String getHexString(byte[] bytes){
        char[] hexChars = new char[bytes.length * 2];
        for(int j = 0; j < bytes.length; j++){
            int v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v >>> 4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
