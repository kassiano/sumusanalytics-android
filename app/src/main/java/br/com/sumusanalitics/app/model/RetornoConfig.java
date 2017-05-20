package br.com.sumusanalitics.app.model;

/**
 * Created by kassianoresende on 28/04/17.
 */

public class RetornoConfig {

    private Boolean success;
    private ConfigData data;

    public String getLink(){

        if(data != null){

            return data.getServerAddress()+":" + data.getServerPort();
        }
        return "";
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ConfigData getData() {
        return data;
    }

    public void setData(ConfigData data) {
        this.data = data;
    }
}
