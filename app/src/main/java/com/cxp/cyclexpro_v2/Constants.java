/*
 * Copyright (C) 2016 Carlos Salamanca (@iamsitting)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy modify, merge publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTIBILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

/*
 * @author Carlos Salamanca
 * @version 2.0.0
 */
package com.cxp.cyclexpro_v2;

import java.util.UUID;

public final class Constants {

    private Constants(){
        //restrict instantiation
    }
    /** Bluetooth Constants */
    protected static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /** Modes */
    protected static final int MODE_NONE = 0;
    protected static final int MODE_SOLO = 1;
    protected static final int MODE_ATHLETE = 2;
    protected static final int MODE_COACH = 3;
    protected static final int MODE_RACE = 4;
    /** CXP Protocols */
    protected static final int SUCCESS_CONNECT = 63;
    protected static final int DATA_READ = 1; //Also ATHLETE read
    protected static final int HEADER_READ = 2;
    protected static final int ERPS_READ = 3;
    protected static final int RACE_READ = 4;
    protected static final int IDLE_READ = 27;
    protected static final int COACH_READ = 5;
    protected static final int XB_CONNECT = 6;

    /** Bluetooth Feedback Characters */
    protected static final String CONNECT_CONFIRM = "C\n";
    protected static final String ERPS_ACK = "E\n";
    protected static final String END_SESSION = "Q\n";
    protected static final String SEND_NEXT_SAMPLE = "K\n";
    protected static final String RETRY_NEW_SESSION = "N\n";
    protected static final String SOLO_SESSION = "W\n";
    protected static final String ATHLETE_SESSION = "X\n";
    protected static final String COACH_SESSION = "Y\n";
    protected static final String RACE_SESSION = "Z\n";

    protected static final String XB_PAIR = "XB\n";

    protected static final int XB_TIMEOUT = 10000;

    /** Preference Constants */
    public static final String PREFS_NAME = "CXPPreferences";
    public static final String PREFS_KEY_DATE = "Date";
    public static final String DATE_NOT_EXISTS = "null";
    public static final String PREFS_KEY_SESSION =  "Session";
    public static final int SESH_NOT_EXISTS = -1;

    /** Web App Constants */
    public static final String HTTP_URL = "http://cyclexpro.com/api/";
}
