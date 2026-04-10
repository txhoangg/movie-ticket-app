package com.example.firebase.model;

import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private String id;
    private String userId;
    private String movieId;
    private String movieTitle;
    private String showtimeId;
    private String theaterName;
    private String date;
    private String time;
    private List<String> seats;
    private double totalPrice;
    private String status;
    private long bookedAt;
    private String posterUrl;

    public Ticket() {
        seats = new ArrayList<>();
        status = "CONFIRMED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }
    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public List<String> getSeats() { return seats != null ? seats : new ArrayList<>(); }
    public void setSeats(List<String> seats) { this.seats = seats; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getBookedAt() { return bookedAt; }
    public void setBookedAt(long bookedAt) { this.bookedAt = bookedAt; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
}
