package com.life.equipmentlife.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Regiao implements Serializable {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("sigla")
    private String sigla;

    @JsonProperty("nome")
    private String nome;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
