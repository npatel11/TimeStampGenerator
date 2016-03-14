package com.example.nishthap.timestampgenerator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Path;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import android.widget.TableRow.LayoutParams;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    Button btn_save, btn_timestamp;
    int click = 0;
    String Message,saveName, result, step = "";
    TableLayout tl;
    TableRow tr;
    TextView timeStamp;
    EditText title;
    Spinner protocols;
    private File folder;
    File myFile;
    String[] linesArray;
    ArrayList<String> lines;
    File protocolFile;
    Path FilePath = null;

    List<EditText> allTitles = new ArrayList<EditText>();
    List<TextView> allTs = new ArrayList<TextView>();
    List<Long> epochList = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_timestamp = (Button) findViewById(R.id.btn_2);
        btn_timestamp.setOnClickListener(this);
        btn_save = (Button) findViewById(R.id.btn_3);
        btn_save.setOnClickListener(this);
        protocols = (Spinner) findViewById(R.id.spinner);
        getFilenames();
    }

    public void getFilenames() {
        String protocolDirName="Protocols_List";
        File directory = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),protocolDirName);
        File fileList[] = directory.listFiles();
        String[] list = new String[fileList.length];
        String[] spinnerList = new String[fileList.length];
        for(int i=0; i < fileList.length; i++)
        {
            list[i]=fileList[i].getAbsolutePath().toString();
            spinnerList[i]=list[i].substring(list[i].lastIndexOf("/")+1);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String> (this,android.R.layout.simple_spinner_item,spinnerList);
        protocols.setAdapter(adapter);
    }

    public void getFileNames()
    {

    }

    private OutputStreamWriter createFile(String fileName) throws IOException
    {
        String dirName = " Data_Collection_TimeStamp";
        File outputDirectory = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dirName);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
        File file = new File(outputDirectory, fileName);
        file.createNewFile();
        FileOutputStream fOut = new FileOutputStream(file);
        return new OutputStreamWriter(fOut);
    }

    @Override
    public void onClick(View v) {
        int id = 0;
        id++;
        lines = new ArrayList<String>();
        BufferedReader br = null;
        String protocolString = null;
        protocolString = protocols.getSelectedItem().toString();
        File protocolFile = new File(protocolString);
        try {
            br = new BufferedReader(new FileReader(protocolFile));
            String str;
            str = null;
            ArrayList<String> lines = new ArrayList<String>();
           /*while((str = br.readLine()) != null){
               lines.add(str);
               String[] linesArray = lines.toArray(new String[lines.size()]);
               step = linesArray[id];
           }*/
           while((str = br.readLine()) != null)
            {
                String[] linesArray = str.split(",");
                step = linesArray[id];
            }
            br.close();
        } catch (IOException e) {
            System.out.println("File Read Error");
        }

      switch (v.getId()) {
            case R.id.btn_2:

                tl = (TableLayout) findViewById(R.id.mytable);
                title = new EditText(this);
                timeStamp = new TextView(this);
                tr = new TableRow(this);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                //Getting currentTime in Unix
                long currentTime=System.currentTimeMillis();

                //converting it in to user readable format
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(currentTime);
                String showTime=String.format("%1$tm/%1$td/%1$tY %1$tI:%1$tM:%1$tS%1$Tp", cal);//shows time in format 10:30:45am

                 //set text for each column of the table layout

                timeStamp.setText(showTime);
                title.setText(step);

                // Add each field to the table layout
                tr.addView(title);
                tr.addView(timeStamp);
                tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                tr = new TableRow(this);

                //Add each entry to an array list to save all the dynamic fields created
                allTitles.add(title);
                allTs.add(timeStamp);
                epochList.add(currentTime);
            break;
            case R.id.btn_3:
               try {
                   OutputStreamWriter osw = null;
                   osw = createFile("TimeStamp_" + epochList.get(0).toString() + ".csv");
                   for (int i = 0; i < allTitles.size(); i++)
                    {
                        osw.write(allTitles.get(i).getText() + "," + epochList.get(i).toString() + "\n");
                        osw.flush();
                        Toast.makeText(getBaseContext(), "File Created", Toast.LENGTH_LONG).show();
                    }
               }catch (IOException e)
                {
                e.printStackTrace();
                }
            break;

        }
    }


}
