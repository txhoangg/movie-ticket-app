package com.example.firebase.model;

public class Movie {
    private String id;
    private String title;
    private String genre;
    private String description;
    private double rating;
    private int duration;
    private String posterUrl;
    private String language;
    private String releaseYear;

    public Movie() {}

    public Movie(String id, String title, String genre, String description,
                 double rating, int duration, String posterUrl, String language, String releaseYear) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.rating = rating;
        this.duration = duration;
        this.posterUrl = posterUrl;
        this.language = language;
        this.releaseYear = releaseYear;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getReleaseYear() { return releaseYear; }
    public void setReleaseYear(String releaseYear) { this.releaseYear = releaseYear; }
}
