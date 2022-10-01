package com.netoneze.ambientesreserva.modelo;

import java.util.Map;

public class Room {
    private Boolean automaticApproval;
    private String details;
    private Map<String, Boolean> specifications;
    private String name;
    private String responsibleUid;
    private String type;

    public Room(Boolean automaticApproval, String details, Map<String, Boolean> specifications, String name, String responsibleUid, String type) {
        this.automaticApproval = automaticApproval;
        this.details = details;
        this.specifications = specifications;
        this.name = name;
        this.responsibleUid = responsibleUid;
        this.type = type;
    }

    public Room() {

    }

    public Boolean getAutomaticApproval() {
        return automaticApproval;
    }

    public void setAutomaticApproval(Boolean automaticApproval) {
        this.automaticApproval = automaticApproval;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, Boolean> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, Boolean> specifications) {
        this.specifications = specifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponsibleUid() {
        return responsibleUid;
    }

    public void setResponsibleUid(String responsibleUid) {
        this.responsibleUid = responsibleUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
