package com.comp.completely_decentralized_peer_to_peer_network.Model;

import java.io.Serializable;

public class Record implements Serializable {

    private String recordType;

    private String localHostIpAddress;
    private int localPortNumber;

    private String clientIpAddress;
    private int clientPortNumber;

    private String serverIpAddress;
    private int serverIpPortNumber;

    private String filename;
    private boolean fileExist;

    private String recordMessage;

    public Record(){}

    public Record(String recordType, String localHostIpAddress, int localPortNumber, String clientIpAddress, int clientPortNumber, String serverIpAddress, int serverIpPortNumber, String filename, boolean fileExist, String recordMessage) {
        this.recordType = recordType;
        this.localHostIpAddress = localHostIpAddress;
        this.localPortNumber = localPortNumber;
        this.clientIpAddress = clientIpAddress;
        this.clientPortNumber = clientPortNumber;
        this.serverIpAddress = serverIpAddress;
        this.serverIpPortNumber = serverIpPortNumber;
        this.filename = filename;
        this.fileExist = fileExist;
        this.recordMessage = recordMessage;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getLocalHostIpAddress() {
        return localHostIpAddress;
    }

    public void setLocalHostIpAddress(String localHostIpAddress) {
        this.localHostIpAddress = localHostIpAddress;
    }

    public int getLocalPortNumber() {
        return localPortNumber;
    }

    public void setLocalPortNumber(int localPortNumber) {
        this.localPortNumber = localPortNumber;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public int getClientPortNumber() {
        return clientPortNumber;
    }

    public void setClientPortNumber(int clientPortNumber) {
        this.clientPortNumber = clientPortNumber;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public int getServerIpPortNumber() {
        return serverIpPortNumber;
    }

    public void setServerIpPortNumber(int serverIpPortNumber) {
        this.serverIpPortNumber = serverIpPortNumber;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isFileExist() {
        return fileExist;
    }

    public void setFileExist(boolean fileExist) {
        this.fileExist = fileExist;
    }

    public String getRecordMessage() {
        return recordMessage;
    }

    public void setRecordMessage(String recordMessage) {
        this.recordMessage = recordMessage;
    }
}
