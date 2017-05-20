package br.com.sumusanalitics.app.model;

import java.io.Serializable;

/**
 * Created by kassianoresende on 24/07/16.
 */
public class DadosCliente implements Serializable{

    private String url;
    private String titulo;
    private String imagem;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
