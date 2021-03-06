package com.example.nishthap.timestampgenerator;

import android.graphics.Path;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.widget.TableRow.LayoutParams;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    Button btn_save, btn_timestamp, btn_unexpected;
    int click = 0;
    String Message,saveName, result, step = "", showTime;
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
    long currentTime;
    BufferedReader br = null;
    String protocolString = null;
    File directory, outputDirectory;

    List<EditText> allTitles = new ArrayList<EditText>();
    List<TextView> allTs = new ArrayList<TextView>();
    List<Long> epochList = new ArrayList<Long>();
    List<String> allSteps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_timestamp = (Button) findViewById(R.id.btn_2);
        btn_timestamp.setOnClickListener(this);
        btn_save = (Button) findViewById(R.id.btn_3);
        btn_save.setOnClickListener(this);
        btn_unexpected = (Button) findViewById(R.id.button);
        btn_unexpected.setOnClickListener(this);
        protocols = (Spinner) findViewById(R.id.spinner);
        getFilenames();
    }
    public void getRowWithCurrentTimeStamp()
    {
        //set text for each column of the table layout
        tl = (TableLayout) findViewById(R.id.mytable);
        title = new EditText(this);
        timeStamp = new TextView(this);
        tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        //Getting currentTime in Unix
        currentTime=System.currentTimeMillis();

        //converting it in to user readable format
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        showTime=String.format("%1$tm/%1$td/%1$tY %1$tI:%1$tM:%1$tS%1$Tp", cal);//shows time in format 10:30:45am

    }
    public void addRowToTable()
    {
        // Add each field to the table layout
        tr.addView(title);
        tr.addView(timeStamp);
        tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        //Add each entry to an array list to save all the dynamic fields created
        allTitles.add(title);
        allTs.add(timeStamp);
        epochList.add(currentTime);
    }
    public void getFilenames() {
        String protocolDirName="Protocols_List";
        directory = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),protocolDirName);
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
    private OutputStreamWriter createFile(String fileName) throws IOException
    {
        String dirName = " Data_Collection_TimeStamp";
        outputDirectory = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dirName);
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
        getFilenames();
        protocolString = protocols.getSelectedItem().toString();
        File protocolFile = new File(protocolString);
        try {
            br = new BufferedReader(new FileReader(protocolFile));
            String str = null;
            while((str = br.readLine()) != null)
            {
                String[] linesArray = str.split(";");
                step = linesArray[id];
            }
            br.close();

        } catch (IOException e) {
            System.out.println("File Read Error");
        }
        switch (v.getId()) {
            case R.id.btn_2:
                getRowWithCurrentTimeStamp();
                timeStamp.setText(showTime);
                title.setText(step);
                addRowToTable();
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
          case R.id.button:
              getRowWithCurrentTimeStamp();
              timeStamp.setText(showTime);
              title.setText("Enter custom step");
              addRowToTable();
              break;

        }
    }


}
