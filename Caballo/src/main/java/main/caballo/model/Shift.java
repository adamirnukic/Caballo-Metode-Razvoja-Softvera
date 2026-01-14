package main.caballo.model;

import java.time.LocalDate;

public class Shift {
    private long id;
    private long userId;
    private LocalDate date;
    private ShiftType type;
    private String note;

    public Shift() {}

    public Shift(long id, long userId, LocalDate date, ShiftType type, String note) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.type = type;
        this.note = note;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ShiftType getType() {
        return type;
    }

    public void setType(ShiftType type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
