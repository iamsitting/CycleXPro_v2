package com.cxp.cyclexpro_v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DataListActivity extends TitleBarActivity implements AdapterView.OnItemClickListener {

    ListView lv;
    DataPusher dp;
    ArrayList<String> FilesInFolder;
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);
        FilesInFolder = GetFiles(this.getApplicationContext().getFilesDir().getAbsolutePath());
        init();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
        new AlertDialog.Builder(this)
                .setTitle("Push Data")
                .setMessage("Do you want to push data?")
                .setPositiveButton("Push", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        //TODO: Add Thread to push data to webserver over REST API
                        dp = new DataPusher(FilesInFolder.get(position), getApplicationContext());
                        dp.start();
                    }
                })
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        //Toast.makeText(getApplicationContext(), getApplicationContext().getFilesDir().getAbsolutePath()+FilesInFolder.get(position), Toast.LENGTH_SHORT).show();
                        String name = FilesInFolder.get(position);
                        File delFile = new File(getFilesDir().getAbsolutePath(), name);
                        final boolean delCheck = delFile.delete();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(delCheck){
                                    FilesInFolder.remove(position);
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                        Log.i("Check Delete", "File deleted: "+ name + " "+ delCheck);
                    }
                })
                .show();
    }

    public void init(){
        lv = (ListView)findViewById(R.id.filelist);
        lv.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, FilesInFolder);
        lv.setAdapter(listAdapter);
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++){
                if(files[i].getName().contains("csv")){
                    MyFiles.add(files[i].getName());
                }
            }
        }

        return MyFiles;
    }
}
