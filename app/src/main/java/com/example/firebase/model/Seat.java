package com.example.firebase.model;

public class Seat {
    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_SELECTED = 1;
    public static final int STATUS_BOOKED = 2;

    private String id;
    private int status;

    public Seat(String id, int status) {
        this.id = id;
        this.status = status;
    }

    public String getId() { return id; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
