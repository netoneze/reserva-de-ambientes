package com.netoneze.ambientesreserva.modelo;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private String userId;
    private String nome;
    private String email;
    private String ra;

    public User(FirebaseUser user, int userId, String nome, String email, String ra) {
        this.userId = user.getUid();
        this.email = user.getEmail();
        this.nome = user.getDisplayName();
        this.ra = user.getPhoneNumber();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
