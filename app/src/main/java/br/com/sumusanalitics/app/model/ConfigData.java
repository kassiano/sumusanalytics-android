package br.com.sumusanalitics.app.model;

/**
 * Created by kassianoresende on 28/04/17.
 */

public class ConfigData {

    private String serverAddress;
    private String serverPort;


    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
