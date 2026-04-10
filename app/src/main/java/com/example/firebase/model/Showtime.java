package com.example.firebase.model;

import java.util.ArrayList;
import java.util.List;

public class Showtime {
    private String id;
    private String movieId;
    private String theaterName;
    private String address;
    private String date;
    private String time;
    private int totalSeats;
    private List<String> bookedSeats;
    private double price;

    public Showtime() {
        bookedSeats = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public List<String> getBookedSeats() { return bookedSeats != null ? bookedSeats : new ArrayList<>(); }
    public void setBookedSeats(List<String> bookedSeats) { this.bookedSeats = bookedSeats; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableSeats() {
        return totalSeats - (bookedSeats != null ? bookedSeats.size() : 0);
    }
}
