package com.comp.completely_decentralized_peer_to_peer_network.p2p;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.comp.completely_decentralized_peer_to_peer_network.MainActivity;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Record;
import com.comp.completely_decentralized_peer_to_peer_network.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientBootstrap extends MainActivity{

    private Socket clientSocket;//客戶端的socket

    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    public final static int FILE_SIZE = 6022386;

    MainActivity activity;
    Handler handler;

    public ClientBootstrap(MainActivity activity) {
        this.activity = activity;

    }

    public void ClientBootstrapThread(){



        Thread thread = new Thread(Connection);
        thread.start();
    }

    private final Runnable Connection=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                //bootstrap node ip address and port number
                InetAddress serverIp = InetAddress.getByName("192.168.40.179");
                int serverPort = 2020;

                //establish connection
                clientSocket = new Socket(serverIp, serverPort);
                System.out.println("Connecting...");

//                BufferedReader inFromServer =
//                        new BufferedReader(new
//                                InputStreamReader(clientSocket.getInputStream()));

                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                dataOutputStream.writeBytes("/P2P/peers.txt\n");
                dataOutputStream.flush();
                System.out.println("request sent");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // request message
                        Record record = new Record();
                        record.setRecordType("BootstrapRequestSent");
                        record.setFilename("peers.txt");
                        record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                        record.setServerIpPortNumber(serverPort);
                        activity.arrRecords.add(record);
                        activity.adapterRecords.notifyDataSetChanged();
                    }
                });


//                String response = inFromServer.readLine();
//                System.out.println("response: " + response);

//                dataOutputStream.close();
//                inFromServer.close();
//
//                clientSocket.close();

                // receive file
                byte [] mybytearray  = new byte [FILE_SIZE];
                InputStream is = clientSocket.getInputStream();
                String path = Environment.getExternalStorageDirectory() + "/P2P/peers.txt";
                fos = new FileOutputStream(path);
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(mybytearray,0,mybytearray.length);
                System.out.println("file received");
                current = bytesRead;

                bos.write(mybytearray, 0 , current);
                bos.flush();
                System.out.println("File " + path
                        + " downloaded (" + current + " bytes read)");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // downloaded file message
                        Record record = new Record();
                        record.setRecordType("BootstrapPeerListDownloaded");
                        record.setFilename("peers.txt");
                        record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                        record.setServerIpPortNumber(serverPort);
                        activity.arrRecords.add(record);
                        activity.adapterRecords.notifyDataSetChanged();
                    }
                });



            }catch(Exception e){
                //connection fail error
                e.printStackTrace();
                Log.e("text","Socket連線="+e.toString());
                finish();
            }
            finally {
                if (fos != null && bos != null && clientSocket != null) {
                    try {
                        fos.close();
                        bos.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    };

}
