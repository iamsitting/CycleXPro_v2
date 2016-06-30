package com.cxp.cyclexpro_v2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //TODO: Add Thread to push data to webserver over REST API
        //dp = new DataPusher(FilesInFolder.get(position), getApplicationContext());
        //dp.start();
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
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }
}
