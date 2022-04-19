package com.netoneze.ambientesreserva.modelo;

import java.util.Date;

public class Reserva {
    private Date horario;
    private String solicitante;
    private String ambiente;

    public Reserva(Date horario, String solicitante, String ambiente) {
        this.horario = horario;
        this.solicitante = solicitante;
        this.ambiente = ambiente;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }




}
