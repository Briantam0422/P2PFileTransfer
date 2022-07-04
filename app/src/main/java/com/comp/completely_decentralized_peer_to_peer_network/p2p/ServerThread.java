package com.comp.completely_decentralized_peer_to_peer_network.p2p;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.comp.completely_decentralized_peer_to_peer_network.MainActivity;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Record;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Request;
import com.comp.completely_decentralized_peer_to_peer_network.Model.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends MainActivity {

    private static int count=0;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    Socket connectionSocket;
    Socket serverSocket;
    String localPortNumber;
    String localIpAddress;
    MainActivity activity;
    Record record = new Record();

    private Socket clientSocket;

    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    public final static int FILE_SIZE = 6022386;
    private static ArrayList clients = new ArrayList();

    public ServerThread(String localPortNumber,String localIpAddress, MainActivity activity) {
        this.localPortNumber = localPortNumber;
        this.activity = activity;
        this.localIpAddress = localIpAddress;
    }

    public void ServerThread(){
        Thread thread = new Thread(Connection);
        thread.start();
    }

    private final Runnable Connection=new Runnable(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {

                //client's server port number
                ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(localPortNumber));
//                ServerSocket welcomeSocket = new ServerSocket(5554);
                System.out.println("localPortNumber: " + localPortNumber);
                while(true) {

                    //connection
                    connectionSocket = welcomeSocket.accept();
                    System.out.println("Accepted connection : " + connectionSocket);

                    //in and out
                    objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
                    objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());


                    //get requested file name
                    Request request = (Request) objectInputStream.readObject();
                    String requestType = request.getType();
                    String fileName = request.getFileName();
                    int TTL = request.getTTL();

                    //direct queryHit
                    if (requestType.equals("query")){
                        System.out.println("query request: " + fileName);

                        //search file in local share folder
                        File shareFolder = new File(Environment.getExternalStorageDirectory(), "/P2P/Share");
                        if (shareFolder.exists()){

                            File searchFile = new File(Environment.getExternalStorageDirectory(), "/P2P/Share/" + fileName + ".txt");
                            if (searchFile.exists()){

                                Response response = new Response();
                                response.setType("queryHit");
                                objectOutputStream.writeObject(response);
                                objectOutputStream.flush();

                                System.out.println("File exists");
                                // send file
                                byte [] mybytearray  = new byte [(int)searchFile.length()];
                                fis = new FileInputStream(searchFile);
                                bis = new BufferedInputStream(fis);
                                bis.read(mybytearray,0,mybytearray.length);
                                os = connectionSocket.getOutputStream();
                                System.out.println("Sending " + fileName + "(" + mybytearray.length + " bytes)");
                                os.write(mybytearray,0,mybytearray.length);
                                os.flush();
                                System.out.println("Done.");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // response message
                                        record.setRecordType("BootstrapResponse");
                                        record.setFilename(fileName);
                                        activity.arrRecords.add(record);
                                        activity.adapterRecords.notifyDataSetChanged();

                                    }
                                });

                            }else {

                                //if TTL = 0, will not continue searching, and send message to client: file not found
                                if (TTL == 0){
                                    Response response = new Response();
                                    response.setType("pong");
                                    objectOutputStream.writeObject(response);
                                    objectOutputStream.flush();
                                }else{

                                    try{
                                        //if TTL > 0 and file not exit, ask another peer server
                                        InetAddress peerIp = InetAddress.getByName("192.168.40.179");
                                        int peerPort = 59752;

                                        serverSocket = new Socket(peerIp, peerPort);
                                        System.out.println("Connecting...");

                                        //in and out
                                        ObjectInputStream objectInputStreamPeer = new ObjectInputStream(serverSocket.getInputStream());
                                        ObjectOutputStream objectOutputStreamPeer= new ObjectOutputStream(serverSocket.getOutputStream());

                                        //send ping request
                                        Request pingRequest = new Request();
                                        pingRequest.setType("ping");
                                        pingRequest.setFileName(fileName);
                                        pingRequest.setTTL(TTL-1);

                                        objectOutputStreamPeer.writeObject(pingRequest);
                                        objectOutputStreamPeer.flush();

                                        System.out.println("ping requesting: " + fileName);
                                        System.out.println("request sent");

                                        //get response from peer server
                                        Response response = (Response) objectInputStreamPeer.readObject();
                                        String type = response.getType();
                                        String queryHitIp = response.getQueryHitIP();
                                        String queryHitPort = response.getQueryHitPort();

                                        if (type.equals("queryHit")){

                                            System.out.println("File exists");
                                            Response queryHitResponse = new Response();
                                            queryHitResponse.setType("pong");
                                            queryHitResponse.setQueryHitIP(queryHitIp);
                                            queryHitResponse.setQueryHitPort(queryHitPort);
                                            objectOutputStream.writeObject(queryHitResponse);
                                            objectOutputStream.flush();

                                        }

                                        if (type.equals("pong")){

                                            //if pong response no queryHit ip and port, it means file not found
                                            if (queryHitIp.isEmpty() && queryHitPort.isEmpty()){

                                                Response pongResponse = new Response();
                                                pongResponse.setType("pong");
                                                objectOutputStream.writeObject(pongResponse);
                                                objectOutputStream.flush();

                                            }else{

                                                //otherwise file found
                                                Response queryHitResponse = new Response();
                                                queryHitResponse.setType("pong");
                                                queryHitResponse.setQueryHitIP(queryHitIp);
                                                queryHitResponse.setQueryHitPort(queryHitPort);
                                                objectOutputStream.writeObject(queryHitResponse);
                                                objectOutputStream.flush();

                                            }

                                        }

                                        serverSocket.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //if server down, send message to tell client
                                        try {

                                            Response response = new Response();
                                            response.setType("pong");
                                            objectOutputStream.writeObject(response);
                                            objectOutputStream.flush();


                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                    }
                                }

                            }

                        }else{

                            System.out.println("folder not exists");

                        }
                    }

                    //if it is ping request, send queryHit or pong response
                    if (requestType.equals("ping")){
                        System.out.println("ping request: " + fileName);

                        //search file in local share folder
                        File shareFolder = new File(Environment.getExternalStorageDirectory(), "/P2P/Share");
                        if (shareFolder.exists()) {

                            File searchFile = new File(Environment.getExternalStorageDirectory(), "/P2P/Share/" + fileName + ".txt");
                            if (searchFile.exists()) {

                                //file exist
                                Response response = new Response();
                                response.setType("queryHit");
                                response.setQueryHitIP(localIpAddress);
                                response.setQueryHitPort(localPortNumber);
                                objectOutputStream.writeObject(response);
                                objectOutputStream.flush();



                            }else{

                                //file not exist
                                if (TTL == 0){
                                    Response response = new Response();
                                    response.setType("pong");
                                    objectOutputStream.writeObject(response);
                                    objectOutputStream.flush();
                                }else{

                                    //send to anther peer
                                    try{
                                        //if TTL > 0 and file not exit, ask another peer server
                                        InetAddress peerIp = InetAddress.getByName("10.0.2.16");
                                        int peerPort = 3672;

                                        serverSocket = new Socket(peerIp, peerPort);
                                        System.out.println("Connecting...");

                                        //in and out
                                        ObjectInputStream objectInputStreamPeer = new ObjectInputStream(serverSocket.getInputStream());
                                        ObjectOutputStream objectOutputStreamPeer= new ObjectOutputStream(serverSocket.getOutputStream());

                                        //send ping request
                                        Request pingRequest = new Request();
                                        pingRequest.setType("ping");
                                        pingRequest.setFileName(fileName);
                                        pingRequest.setTTL(TTL-1);

                                        objectOutputStreamPeer.writeObject(pingRequest);
                                        objectOutputStreamPeer.flush();

                                        System.out.println("ping requesting: " + fileName);
                                        System.out.println("request sent");

                                        //get response from peer server
                                        Response response = (Response) objectInputStreamPeer.readObject();
                                        String type = response.getType();
                                        String queryHitIp = response.getQueryHitIP();
                                        String queryHitPort = response.getQueryHitPort();

                                        if (type.equals("queryHit")){

                                            System.out.println("File exists");
                                            Response queryHitResponse = new Response();
                                            queryHitResponse.setType("pong");
                                            queryHitResponse.setQueryHitIP(queryHitIp);
                                            queryHitResponse.setQueryHitPort(queryHitPort);
                                            objectOutputStream.writeObject(queryHitResponse);
                                            objectOutputStream.flush();

                                        }

                                        if (type.equals("pong")){

                                            //if pong response no queryHit ip and port, it means file not found
                                            if (queryHitIp.isEmpty() && queryHitPort.isEmpty()){

                                                Response pongResponse = new Response();
                                                pongResponse.setType("pong");
                                                objectOutputStream.writeObject(pongResponse);
                                                objectOutputStream.flush();

                                            }else{

                                                //otherwise file found
                                                Response queryHitResponse = new Response();
                                                queryHitResponse.setType("pong");
                                                queryHitResponse.setQueryHitIP(queryHitIp);
                                                queryHitResponse.setQueryHitPort(queryHitPort);
                                                objectOutputStream.writeObject(queryHitResponse);
                                                objectOutputStream.flush();

                                            }

                                        }

                                        serverSocket.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //if server down, send message to tell client
                                        try {

                                            Response response = new Response();
                                            response.setType("pong");
                                            objectOutputStream.writeObject(response);
                                            objectOutputStream.flush();


                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                    }

                                }


                            }

                        }
                    }

                }

                } catch (IOException | ClassNotFoundException e) {
                System.out.println("Server Socket ERROR");
            }
            finally {
                try {
                    if (bis != null && os != null){
                        bis.close();
                        os.close();

                    }
                    if (connectionSocket != null){
                        objectOutputStream.close();
                        objectInputStream.close();
                        connectionSocket.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
