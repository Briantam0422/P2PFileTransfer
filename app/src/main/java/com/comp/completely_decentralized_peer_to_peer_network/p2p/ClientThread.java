package com.comp.completely_decentralized_peer_to_peer_network.p2p;

import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.comp.completely_decentralized_peer_to_peer_network.MainActivity;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Record;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Request;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThread extends MainActivity{

    private Socket clientSocket;

    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    public final static int FILE_SIZE = 6022386;

    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    String fileName;
    MainActivity activity;


    public ClientThread(MainActivity activity, String fileName) {
        this.activity = activity;
        this.fileName = fileName;
    }

    public void ClientThread(){
        Thread thread = new Thread(Connection);
        thread.start();
    }

    private final Runnable Connection=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                //destination IP address
//                InetAddress serverIp = InetAddress.getByName("192.168.40.179");
                InetAddress serverIp = InetAddress.getByName("192.168.40.161");
                //destination port number
//                int serverPort = 59752;
                int serverPort = 54802;
                //connection
                clientSocket = new Socket(serverIp, serverPort);
                System.out.println("Connecting...");

                //in and out
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                Request request = new Request();
                request.setType("query");
                request.setFileName(fileName);
                request.setTTL(1);

                objectOutputStream.writeObject(request);
                objectOutputStream.flush();

                System.out.println("Client requesting: " + fileName);
                System.out.println("request sent");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // request message
                        Record record = new Record();
                        record.setRecordType("BootstrapRequestSent");
                        record.setFilename(fileName + ".txt");
                        record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                        record.setServerIpPortNumber(serverPort);
                        activity.arrRecords.add(record);
                        activity.adapterRecords.notifyDataSetChanged();
                    }
                });

                Response response = (Response) objectInputStream.readObject();
                String type = response.getType();

                if (type.equals("queryHit")){
                    System.out.println("queryHit response received");
                    //receive file
                    ReceiveFile(serverIp, serverPort);
                }

                if (type.equals("pong")){

                    //if TTL = 0, it means no queryHit IP and port number, File not found
                    if (response.getQueryHitIP() == null && response.getQueryHitPort() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // request message
                                Record record = new Record();
                                record.setRecordType("PongResponseFileNotFound");
                                record.setFilename(fileName + ".txt");
                                record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                                record.setServerIpPortNumber(serverPort);
                                activity.arrRecords.add(record);
                                activity.adapterRecords.notifyDataSetChanged();
                            }
                        });

                    }else{
                        //file Found
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // request message
                                Record record = new Record();
                                record.setRecordType("PongResponse");
                                record.setFilename(fileName + ".txt");
                                record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                                record.setServerIpPortNumber(serverPort);
                                activity.arrRecords.add(record);
                                activity.adapterRecords.notifyDataSetChanged();
                            }
                        });


                        clientSocket.close();
                        System.out.println("pong");

                        InetAddress queryHitIp = InetAddress.getByName(response.getQueryHitIP());
                        int queryHitPort = Integer.parseInt(response.getQueryHitPort());

                        clientSocket = new Socket(queryHitIp, queryHitPort);

                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                        request = new Request();
                        request.setType("query");
                        request.setFileName(fileName);

                        objectOutputStream.writeObject(request);
                        objectOutputStream.flush();

                        System.out.println("Client requesting: " + fileName);
                        System.out.println("request sent");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // request message
                                Record record = new Record();
                                record.setRecordType("BootstrapRequestSent");
                                record.setFilename(fileName + ".txt");
                                record.setServerIpAddress(String.valueOf(queryHitIp.getHostAddress()));
                                record.setServerIpPortNumber(queryHitPort);
                                activity.arrRecords.add(record);
                                activity.adapterRecords.notifyDataSetChanged();
                            }
                        });

                        //received file!!!!!!!!!!!!!!!!!!!!!!!!
                        ReceiveFile(queryHitIp, queryHitPort);
                    }


                }

            }catch(Exception e){
                //connection fail error
                e.printStackTrace();
                Log.e("text","Socket連線="+e.toString());
                finish();
            }
            finally {
                if (fos != null && bos != null ) {
                    try {
                        fos.close();
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (clientSocket != null){

                        try {
                            objectInputStream.close();
                            objectOutputStream.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    };

    private void ReceiveFile(InetAddress serverIp, int serverPort) throws IOException {

        // receive file
        byte [] mybytearray  = new byte [FILE_SIZE];
        InputStream is = clientSocket.getInputStream();
        File shareFolder = new File(Environment.getExternalStorageDirectory(), "/P2P/Share");
        String path = Environment.getExternalStorageDirectory() + "/P2P/Share/"+ fileName + ".txt";
        if (shareFolder.exists()){
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
                    record.setFilename(fileName + ".txt");
                    record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                    record.setServerIpPortNumber(serverPort);
                    activity.arrRecords.add(record);
                    activity.adapterRecords.notifyDataSetChanged();
                }
            });
        }else{
            shareFolder.mkdirs();
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
                    record.setFilename(fileName + ".txt");
                    record.setServerIpAddress(String.valueOf(serverIp.getHostAddress()));
                    record.setServerIpPortNumber(serverPort);
                    activity.arrRecords.add(record);
                    activity.adapterRecords.notifyDataSetChanged();
                }
            });
        }

    }
}
