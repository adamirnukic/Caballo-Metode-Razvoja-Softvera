package main.caballo.model;

public class OrderItem {
    private long id;
    private long orderId;
    private long itemId;
    private String itemName;
    private int kolicina;
    private double cijena;

    public OrderItem(long id, long orderId, long itemId, String itemName, int kolicina, double cijena) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.kolicina = kolicina;
        this.cijena = cijena;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public double getUkupno() {
        return Math.round((cijena * kolicina) * 100.0) / 100.0;
    }
}
