package com.netoneze.ambientesreserva.modelo;

import java.util.Date;

public class Reservation {
    private String date;
    private String time;
    private String room;
    private String userId;

    public Reservation(String date, String time, String room, String userId) {
        this.date = date;
        this.time = time;
        this.room = room;
        this.userId = userId;
    }

    public Reservation() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
