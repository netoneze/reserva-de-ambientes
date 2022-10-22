package com.netoneze.ambientesreserva.modelo;

public class User {

    private String userId;
    private String username;
    private String type;
    private Integer positionInSpinner;

    public User(String userId, String username, String type, Integer positionInSpinner) {
        this.userId = userId;
        this.username = username;
        this.type = type;
        this.positionInSpinner = positionInSpinner;
    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPositionInSpinner() {
        return positionInSpinner;
    }

    public void setPositionInSpinner(Integer positionInSpinner) {
        this.positionInSpinner = positionInSpinner;
    }
}
