package com.netoneze.ambientesreserva.modelo;

public class Ambiente {

    private String nome;
    private String tipo;

    public Ambiente (String nome, String tipo){
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
