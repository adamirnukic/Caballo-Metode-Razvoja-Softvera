package main.caballo.model;

public class Pice extends MenuItem {
    private int currentQty;

    public Pice(long id, String name, String description, double price, String category, int currentQty, boolean active) {
        super(id, name, description, price, category, active);
        this.currentQty = currentQty;
    }

    public Pice(long id, String name, String description, double price, String category, int currentQty) {
        super(id, name, description, price, category);
        this.currentQty = currentQty;
    }

    public int getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(int currentQty) {
        this.currentQty = currentQty;
    }
}
