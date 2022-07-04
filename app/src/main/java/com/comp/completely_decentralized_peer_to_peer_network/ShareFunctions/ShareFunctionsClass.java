package com.comp.completely_decentralized_peer_to_peer_network.ShareFunctions;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.comp.completely_decentralized_peer_to_peer_network.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

public class ShareFunctionsClass extends MainActivity{

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getPortNumber(){

        //Random generate listening port number
        Random random = new Random();

        //Log.d("test", "random" + portNumber);

        return random.nextInt(65535);

    }

    public ArrayList<String> readConfig(){

        ArrayList<String> configRecords = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory(), "P2P/config.txt");
        try {
            if (file.exists()){

                //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                        configRecords.add(line);
                    }
                    br.close();
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }
            }else{

                Toast.makeText(this, "config not exist", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configRecords;
    }

    public ArrayList<String> readPeerList(){

        ArrayList<String> configRecords = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory(), "P2P/peers.txt");
        try {
            if (file.exists()){

                //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                        configRecords.add(line);
                    }
                    br.close();
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }
            }else{

                Toast.makeText(this, "config not exist", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configRecords;
    }




    public void shareFile(Uri fileUri, File srcFilePath, Context applicationContext) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            // open the user-picked file for reading:
            in = applicationContext.getContentResolver().openInputStream(fileUri);
            // open the output-file:
            out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "P2P/Share/" + srcFilePath.getName()));
            // copy the content:
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            // Contents are copied!
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void getDefaultPeersList() throws IOException {

        /** create peer list file for bootstrap node (dummy data)**/
        File folder = new File(Environment.getExternalStorageDirectory(), "P2P");
        File peerList = new File(folder, "peers.txt");
        if (!peerList.exists()){
            FileWriter writer = new FileWriter(peerList);
            writer.append("10.0.2.16" + " " + "14796" + "\n");
            writer.append("10.0.2.16" + " " + "3672");
            writer.flush();
            writer.close();
        }

    }

}
