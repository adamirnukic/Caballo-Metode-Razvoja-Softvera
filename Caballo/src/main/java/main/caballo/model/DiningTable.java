package main.caballo.model;

public class DiningTable {
    private long id;
    private int brojStola;
    private int brojSjedista;
    private String status;

    public DiningTable(long id, int brojStola, int brojSjedista, String status) {
        this.id = id;
        this.brojStola = brojStola;
        this.brojSjedista = brojSjedista;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBrojStola() {
        return brojStola;
    }

    public void setBrojStola(int brojStola) {
        this.brojStola = brojStola;
    }

    public int getBrojSjedista() {
        return brojSjedista;
    }

    public void setBrojSjedista(int brojSjedista) {
        this.brojSjedista = brojSjedista;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Sto #" + brojStola + " (" + brojSjedista + " sjedista) - " + status;
    }
}
