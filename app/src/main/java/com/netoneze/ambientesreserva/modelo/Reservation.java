package com.netoneze.ambientesreserva.modelo;

public class Reservation {
    private String date;
    private String purpose;
    private String startTime;
    private String endTime;
    private String room;
    private String userName;
    private String documentId;

    public Reservation(String date, String purpose, String startTime, String endTime, String room, String userName, String documentId) {
        this.date = date;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.userName = userName;
        this.documentId = documentId;
    }

    public Reservation() {

    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userId) {
        this.userName = userId;
    }
}
