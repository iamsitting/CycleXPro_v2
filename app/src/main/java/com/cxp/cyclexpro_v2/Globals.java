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
    protected static String sUsername = "null";
    protected static int sWeight = 0;
    protected static String sContact = "8888888888";
    protected static String sMyTRIOid = "1";
    protected static String sDestTRIOid = "2";



    //DataLogger
    protected static boolean sNewData = false;
    protected static String sDataString = "";
    protected static byte[] sBuffer;

    //Bluetooth
    protected static boolean sGoodHeaderRead = true;
    protected static boolean sERPSFlag = false;
    protected static boolean sBtConnected = false;
    protected static boolean sXbConnected = false;
    protected static boolean sSessionOn = false;

    //Modes
    protected static int sMode = 0;
    //Debug
    protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String getHexString(byte[] bytes, int len){
        char[] hexChars = new char[2*len];
        for(int j = 0; j < len; j++){
            int v = bytes[j] & 0xFF;
            hexChars[2*j] = hexArray[v >>> 4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
