package com.cxp.cyclexpro_v2;

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

}
