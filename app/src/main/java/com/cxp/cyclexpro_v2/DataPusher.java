package com.cxp.cyclexpro_v2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Carlos on 6/26/2016.
 */
public class DataPusher extends Thread {
    File file;
    BufferedReader bReader;
    Context con;
    String filename;

    public DataPusher(String fName, Context context) {
        con = context;
        file = new File(con.getFilesDir(), fName);
        filename = fName;
    }

    public void run() {
        //TODO: Handle HTTP response to know what do next
        int status = sendToServer();
        Log.i("HTTP:", Integer.toString(status));
        //Toast.makeText(con.getApplicationContext(), Integer.toString(status), Toast.LENGTH_SHORT).show();

    }

    /**
     * Sends the csv File in which this Thread was initialized with to a server
     *
     * @return  respCode - an int that represents the results of the transaction
     */
    public int sendToServer(){
        JSONArray jArr = csvToJson();
        String jsonString = jArr.toString();
        HttpURLConnection httpConnection = getHttpConnection();
        int respCode = -1;

        try{
            OutputStream os = httpConnection.getOutputStream();
            os.write(jsonString.getBytes("UTF-8"));
            os.close();

            respCode = getHttpResponse(httpConnection);

        } catch(IOException e){
            Log.e("Except", "getOutStream failed"+e.toString());
        }

        return respCode;
    }

    /**
     * Converts the CSV data into JSON format
     *
     * @return      a JSONArray format of the CSV file
     */
    public JSONArray csvToJson(){
        JSONObject nameJsonObj = new JSONObject();
        if(file.exists()) {
            try {
                bReader = new BufferedReader(new FileReader(file));
                nameJsonObj.put("title", filename);
            } catch (IOException e) {
                Log.e("Except", "File open failed"+e.toString());
            } catch (JSONException e) {
                Log.e("Except", "Json title:"+e.toString());
            }

        }
        String line;
        String[] keys;
        String[] values;
        JSONArray retJsonArray = new JSONArray();
        retJsonArray.put(nameJsonObj);

        try {
            keys = bReader.readLine().split(","); //first line of CSV contains keys for JSON
            while((line=bReader.readLine()) != null) {
                values = line.split(",");
                JSONObject tempJsonObj = new JSONObject();
                if(keys.length == values.length){
                    for (int i=0; i < keys.length; i++) {
                        tempJsonObj.put(keys[i], values[i]);
                    }
                }
                retJsonArray.put(tempJsonObj);
            }

        } catch (IOException e) {
            Log.e("Except", "File read failed"+e.toString());
        } catch (JSONException e) {
            Log.e("Except", "Json generation failed"+e.toString());
        }
        return retJsonArray;
    }

    /**
     * This defines the HTTP Connection with Request destination, property, and method
     * @return  the HTTP connection
     */
    public HttpURLConnection getHttpConnection() {
        String postUrl = Constants.HTTP_URL;
        HttpURLConnection conn = null;

        try{
            URL urlObject = new URL(postUrl);
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-type",
                    "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

        } catch(MalformedURLException e){
            Log.e("Except", "URL formation failed"+e.toString());
        } catch (IOException e){
            Log.e("Except", "URL connection failed"+e.toString());
        }

        return conn;
    }

    /**
     * Gets a responses from the HTTP connection and translates to an integer code
     * @param conn - no description
     * @return - no description
     */
    public int getHttpResponse(HttpURLConnection conn) {
        String response = "000";
        try {
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = streamToString(in);
        } catch(IOException e){}

        switch (response){
            case "000":
                return 0;
            case "200":
                return 1;
            case "201":
                return 10;
            case "500":
                return 2;
            case "404":
                return 3;
            default:
                return 100;
        }

    }

    /**
     * Converts an InputStream object to a string object
     * @param is - no description
     * @return - no description
     */
    public String streamToString(InputStream is){
        Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}
