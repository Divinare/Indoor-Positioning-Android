package com.joe.indoorlocalization.Calibration;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;
import com.joe.indoorlocalization.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportDatabase extends ListActivity {

    static String TAG = ImportDatabase.class.getSimpleName();

    private File currentDir;
    private FileArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/sdcard/Android/data/com.joe.indoorlocalization/files/Documents/");
        fill(currentDir);
    }

    private void fill(File f) {
        File[]dirs = f.listFiles();
        this.setTitle("Current Dir: "+f.getName());
        List<Option> dir = new ArrayList<Option>();
        List<Option>fls = new ArrayList<Option>();

        adapter = new FileArrayAdapter(ImportDatabase.this,R.layout.file_view,dir);
        this.setListAdapter(adapter);

        try {
            for(File ff: dirs) {
                if(ff.isDirectory())
                dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
            }
        }catch(Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase("sdcard"))
        dir.add(0,new Option("..","Parent Directory",f.getParent()));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }

    private void onFileClick(Option o) {
        File file = new File(o.getPath());
        Log.d(TAG, o.getPath());
        StringBuilder fileContent = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                fileContent.append(line);
                fileContent.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            Log.d(TAG, "Reading file from path " + o.getPath() + " failed!");
            Toast.makeText(this, "Reading file " + o.getName() + " failed.", Toast.LENGTH_SHORT).show();
        }
        emptyDatabase();
        importIntoDatabase(fileContent, o.getName());
    }

    private void emptyDatabase() {
        FingerPrint.deleteAll(FingerPrint.class);
        Scan.deleteAll(Scan.class);
        Log.d(TAG, "Database emptied");
    }

    private void importIntoDatabase(StringBuilder fileContent, String fileName) {
        Log.d(TAG, "Importing into database " + fileName);
        String[] rows = fileContent.toString().split("\\n");
        for(String row: rows) {
            //Log.i(TAG, row);
            String[] array = row.split(";"); // Each row is now in a row array
                int z = Integer.parseInt(array[0]);
                float x = Float.parseFloat(array[1]);
                float y = Float.parseFloat(array[2]);

                Scan scan = new Scan(z, x, y);
                scan.save();
                List<FingerPrint> fingerPrints = new ArrayList<>();

                for(int i = 3; i < array.length-1; i=i+2) {
                    String mac = array[i]; // list goes mac;rssi;mac;rssi...
                    String RSSI = array[i+1];
                    Long scanId = scan.getId();
                    Log.i(TAG, "" + scanId);
                    FingerPrint fp = new FingerPrint(scanId, mac, RSSI);
                    //Scan s = fp.getScan();
                    //float test = s.getX();
                    //Log.i(TAG, "" + test);
                    fp.save();
                    fingerPrints.add(fp);
                }

        }

        Toast.makeText(this, "File " + fileName + " imported to database succesfully.", Toast.LENGTH_SHORT).show();



    }

    public class Option implements Comparable<Option>{
        private String name;
        private String data;
        private String path;

        public Option(String n,String d,String p) {
            name = n;
            data = d;
            path = p;
        }
        public String getName() {
            return name;
        }
        public String getData() {
            return data;
        }
        public String getPath() {
            return path;
        }
        @Override
        public int compareTo(Option o) {
            if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
            else {
                throw new IllegalArgumentException();
            }
        }
    }

    public class FileArrayAdapter extends ArrayAdapter<Option> {
        private Context c;
        private int id;
        private List<Option>items;

        public FileArrayAdapter(Context context, int textViewResourceId, List<Option> objects) {
            super(context, textViewResourceId, objects);
            c = context;
            id = textViewResourceId;
            items = objects;
        }

        public Option getItem(int i) {
            return items.get(i);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(id, null);
            }
            final Option o = items.get(position);
            if (o != null) {
                TextView t1 = (TextView) v.findViewById(R.id.DisplayDataView1);
                TextView t2 = (TextView) v.findViewById(R.id.DisplayDataView2);

                if(t1!=null)
                t1.setText(o.getName());
                if(t2!=null)
                t2.setText(o.getData());
            }
            return v;
        }
    }
}


