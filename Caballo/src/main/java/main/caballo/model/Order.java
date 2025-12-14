package main.caballo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private long id;
    private long userId;
    private LocalDateTime datum;
    private double ukupno;
    private final List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(long id, long userId, LocalDateTime datum, double ukupno) {
        this.id = id;
        this.userId = userId;
        this.datum = datum;
        this.ukupno = ukupno;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }

    public double getUkupno() {
        return ukupno;
    }

    public void setUkupno(double ukupno) {
        this.ukupno = ukupno;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
