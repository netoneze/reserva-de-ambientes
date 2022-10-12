package com.netoneze.ambientesreserva.modelo;

public class Reservation {
    private String date;
    private String purpose;
    private String startTime;
    private String endTime;
    private String room;
    private String userId;

    public Reservation(String date, String purpose, String startTime, String endTime, String room, String userId) {
        this.date = date;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.userId = userId;
    }

    public Reservation() {

    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
