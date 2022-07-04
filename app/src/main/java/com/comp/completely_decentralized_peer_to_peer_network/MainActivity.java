package com.comp.completely_decentralized_peer_to_peer_network;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.comp.completely_decentralized_peer_to_peer_network.Adapter.AdapterRecord;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Record;
import com.comp.completely_decentralized_peer_to_peer_network.ShareFunctions.ShareFunctionsClass;
import com.comp.completely_decentralized_peer_to_peer_network.p2p.ClientBootstrap;
import com.comp.completely_decentralized_peer_to_peer_network.p2p.BootstrapNode;
import com.comp.completely_decentralized_peer_to_peer_network.p2p.ClientThread;
import com.comp.completely_decentralized_peer_to_peer_network.p2p.ServerThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    public static final int PICKFILE_RESULT_CODE = 1;

    Button btnShareFile, btnRequestFile;
    public ArrayList<Record> arrRecords = new ArrayList<Record>();

    private Uri fileUri;
    private String filePath;

    RecyclerView rvRecords;
    public RecyclerView.Adapter adapterRecords;
    LinearLayoutManager linearLayoutManagerRecords;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button ID
        btnShareFile = findViewById(R.id.btn_share_file);
        btnRequestFile = findViewById(R.id.btn_request_file);

        //RecyclerView ID and Set adapter and linear manager
        rvRecords = findViewById(R.id.rv_records);
        adapterRecords = new AdapterRecord(this, arrRecords);
        linearLayoutManagerRecords = new LinearLayoutManager(this);
        rvRecords.setAdapter(adapterRecords);
        rvRecords.setLayoutManager(linearLayoutManagerRecords);

        //Initial Setup
        try {
            initialSetup();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Handling upload share file
        ShareFile();

        //Handling request
        RequestFile();

     

    }

    //Initial Setup
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialSetup() throws UnknownHostException {
        //Permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
            Toast.makeText(MainActivity.this, "Internet permission. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }

        //check config file and peers list file
        File folder = new File(Environment.getExternalStorageDirectory(), "P2P");
        try {
            if (!folder.exists()){

                //getLocalIpAddress and random portNumber
                ShareFunctionsClass shareFunctionsClass = new ShareFunctionsClass();
                String localIpAddress = shareFunctionsClass.getLocalIpAddress();
                int localPortNumber = shareFunctionsClass.getPortNumber();

                //set record
                //initial record
                Record initialRecord = new Record();
                initialRecord.setRecordType("initialRecord");
                initialRecord.setLocalHostIpAddress(localIpAddress);
                initialRecord.setLocalPortNumber(localPortNumber);
                //display record message
                arrRecords.add(initialRecord);
                adapterRecords.notifyDataSetChanged();

                //create folder
                folder.mkdirs();
                //create config file
                File configFile = new File(folder, "/config.txt");
                if (!configFile.exists()){
                    FileWriter writer = new FileWriter(configFile);
                    writer.append(localIpAddress + " " + localPortNumber);
                    writer.flush();
                    writer.close();
                }

                //get peers list from bootstrap node
                ClientBootstrap clientBootstrap = new ClientBootstrap(MainActivity.this);
                clientBootstrap.ClientBootstrapThread();

                //open server service
                ServerThread serverThread = new ServerThread(String.valueOf(localPortNumber),localIpAddress, this);
                serverThread.ServerThread();

            }else{

                //read config file
                ShareFunctionsClass shareFunctionsClass = new ShareFunctionsClass();
                ArrayList<String> arrConfigRecords = shareFunctionsClass.readConfig();

                //get local ip address and port number
                String[] configRecords = arrConfigRecords.get(0).split(" ");
                String localIpAddress = configRecords[0];
                String localPortNumber = configRecords[1];

                //initial record
                Record initialRecord = new Record();
                initialRecord.setRecordType("initialRecord");
                initialRecord.setLocalHostIpAddress(localIpAddress);
                initialRecord.setLocalPortNumber(Integer.parseInt(localPortNumber));
                //display record message
                arrRecords.add(initialRecord);
                adapterRecords.notifyDataSetChanged();

                //open server service
                ServerThread serverThread = new ServerThread(localPortNumber, localIpAddress, this);
                serverThread.ServerThread();


                BootstrapNode bootstrapNode = new BootstrapNode(localPortNumber, this);
                bootstrapNode.BootstrapThread();

                shareFunctionsClass.getDefaultPeersList();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Handling upload share file
    private void ShareFile(){

        btnShareFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Choose file for sharing
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);

            }
        });
    }

    //Handling request
    private void RequestFile(){

        //request button
        btnRequestFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopUpDialog();

            }
        });
    }

    private void PopUpDialog(){

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View dialogView = inflater.inflate(R.layout.dialog_request_file, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter File Name")
                .setView(dialogView)
                .setPositiveButton("request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etRequestFileName = (EditText) (dialogView.findViewById(R.id.et_dialog_request_file_name));
                        Toast.makeText(getApplicationContext(), "Requested File: " +

                                etRequestFileName.getText().toString(), Toast.LENGTH_SHORT).show();
                        //client initial connection
                        String requestFileName = etRequestFileName.getText().toString();
                        ClientThread clientThread = new ClientThread(MainActivity.this, requestFileName);
                        clientThread.ClientThread();

                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    ShareFunctionsClass shareFunctionsClass = new ShareFunctionsClass();
                    fileUri = data.getData();
                    filePath = fileUri.getPath();

                    if(filePath !=null){
                        //copy a local file to p2p share file

                        File srcFilePath = new File(filePath);

                        File dstFolderPath = new File(Environment.getExternalStorageDirectory(), "P2P/Share");
                        if (dstFolderPath.exists()){
                            try {
                                shareFunctionsClass.shareFile(fileUri, srcFilePath, MainActivity.this.getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Shared", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            dstFolderPath.mkdirs();
                            try {
                                shareFunctionsClass.shareFile(fileUri, srcFilePath, MainActivity.this.getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Shared", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

                break;
        }
    }

}