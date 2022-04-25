package com.netoneze.ambientesreserva.modelo;

public class Ambiente {

    private String name;
    private String type;
    private String block;
    private String department;
    private String details;

    public Ambiente (String name, String type, String block, String department){
        this.name = name;
        this.type = type;
        this.block = block;
        this.department = department;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
