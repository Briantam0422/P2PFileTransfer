package com.comp.completely_decentralized_peer_to_peer_network.Model;

import java.io.Serializable;

public class Response implements Serializable {

    private String type;
    private String senderIP;
    private String senderPort;
    private String queryHitIP;
    private String queryHitPort;

    public Response(){};

    public Response(String type, String senderIP, String senderPort, String queryHitIP, String queryHitPort) {
        this.type = type;
        this.senderIP = senderIP;
        this.senderPort = senderPort;
        this.queryHitIP = queryHitIP;
        this.queryHitPort = queryHitPort;
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

    public String getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(String senderPort) {
        this.senderPort = senderPort;
    }

    public String getQueryHitIP() {
        return queryHitIP;
    }

    public void setQueryHitIP(String queryHitIP) {
        this.queryHitIP = queryHitIP;
    }

    public String getQueryHitPort() {
        return queryHitPort;
    }

    public void setQueryHitPort(String queryHitPort) {
        this.queryHitPort = queryHitPort;
    }
}
