package com.comp.completely_decentralized_peer_to_peer_network.Model;

import java.io.Serializable;

public class Request implements Serializable {
    
    private String type;
    private String senderIP;
    private String senderPortNumber;
    private String fileName;
    private int TTL;

    public Request(){};

    public Request(String type, String senderIP, String senderPortNumber, String fileName, int TTL) {
        this.type = type;
        this.senderIP = senderIP;
        this.senderPortNumber = senderPortNumber;
        this.fileName = fileName;
        this.TTL = TTL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public String getSenderPortNumber() {
        return senderPortNumber;
    }

    public void setSenderPortNumber(String senderPortNumber) {
        this.senderPortNumber = senderPortNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }
}
