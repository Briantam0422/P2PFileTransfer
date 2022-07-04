package com.comp.completely_decentralized_peer_to_peer_network.p2p;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.comp.completely_decentralized_peer_to_peer_network.MainActivity;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Record;
import com.comp.completely_decentralized_peer_to_peer_network.ShareFunctions.ShareFunctionsClass;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BootstrapNode extends MainActivity {

    private static int count=0;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    Socket connectionSocket;
    String localPortNumber;
    MainActivity activity;
    Record record = new Record();

    private static ArrayList clients = new ArrayList();

    public BootstrapNode(String localPortNumber, MainActivity activity) {
        this.localPortNumber = localPortNumber;
        this.activity = activity;
    }

    public void BootstrapThread(){
        Thread thread = new Thread(Connection);
        thread.start();
    }

    private final Runnable Connection=new Runnable(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {


                //bootstrap server port number
                ServerSocket welcomeSocket = new ServerSocket(2020);
                System.out.println(localPortNumber);
                while(true) {

                    //connection
                    connectionSocket = welcomeSocket.accept();
                    System.out.println("Accepted connection : " + connectionSocket);

                    //in and out
                    BufferedReader dataInputStream = new BufferedReader(new
                            InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());

                    String fileName = dataInputStream.readLine();
                    System.out.println("request: " + fileName);

//                    dataOutputStream.writeBytes("peers.txt\n");
//                    dataOutputStream.flush();
//                    System.out.println("response: file found");

//                    dataInputStream.close();
//                    dataOutputStream.close();

                    // send file
                    File myFile = new File (Environment.getExternalStorageDirectory(), fileName);
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    os = connectionSocket.getOutputStream();
                    System.out.println("Sending " + "peers.txt" + "(" + mybytearray.length + " bytes)");
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();
                    System.out.println("Done.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // response message
                            record.setRecordType("BootstrapResponse");
                            record.setFilename("peers.txt");
                            activity.arrRecords.add(record);
                            activity.adapterRecords.notifyDataSetChanged();

                        }
                    });
                }

                } catch (IOException e) {
                System.out.println("Server Socket ERROR");
            }
            finally {
                try {
                    if (bis != null && os != null && connectionSocket != null){
                        bis.close();
                        os.close();
                        connectionSocket.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
