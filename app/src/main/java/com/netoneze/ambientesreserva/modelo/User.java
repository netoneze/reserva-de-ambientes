package com.netoneze.ambientesreserva.modelo;

public class User {

    private int userId;
    private String nome;
    private String email;
    private String ra;

    public User(int userId, String nome, String email, String ra) {
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.ra = ra;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

}
