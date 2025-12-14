package main.caballo.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private long id;
    private long tableId;
    private String imeGosta;
    private String brojTelefona;
    private LocalDate datumRezervacije;
    private LocalTime vrijemeDolaska;
    private int brojOsoba;
    private String napomena;

    public Reservation(long id, long tableId, String imeGosta, String brojTelefona, LocalDate datumRezervacije, LocalTime vrijemeDolaska, int brojOsoba, String napomena) {
        this.id = id;
        this.tableId = tableId;
        this.imeGosta = imeGosta;
        this.brojTelefona = brojTelefona;
        this.datumRezervacije = datumRezervacije;
        this.vrijemeDolaska = vrijemeDolaska;
        this.brojOsoba = brojOsoba;
        this.napomena = napomena;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getImeGosta() {
        return imeGosta;
    }

    public void setImeGosta(String imeGosta) {
        this.imeGosta = imeGosta;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public LocalDate getDatumRezervacije() {
        return datumRezervacije;
    }

    public void setDatumRezervacije(LocalDate datumRezervacije) {
        this.datumRezervacije = datumRezervacije;
    }

    public LocalTime getVrijemeDolaska() {
        return vrijemeDolaska;
    }

    public void setVrijemeDolaska(LocalTime vrijemeDolaska) {
        this.vrijemeDolaska = vrijemeDolaska;
    }

    public int getBrojOsoba() {
        return brojOsoba;
    }

    public void setBrojOsoba(int brojOsoba) {
        this.brojOsoba = brojOsoba;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }
}
