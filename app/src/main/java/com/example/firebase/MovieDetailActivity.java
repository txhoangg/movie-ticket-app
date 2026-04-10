package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase.adapter.ShowtimeAdapter;
import com.example.firebase.model.Showtime;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private String movieId, movieTitle;
    private FirebaseFirestore db;
    private ShowtimeAdapter showtimeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getStringExtra("movieId");
        movieTitle = getIntent().getStringExtra("movieTitle");

        db = FirebaseFirestore.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(movieTitle != null ? movieTitle : "");

        RecyclerView rvShowtimes = findViewById(R.id.rvShowtimes);
        ProgressBar progressShowtimes = findViewById(R.id.progressShowtimes);
        TextView tvNoShowtimes = findViewById(R.id.tvNoShowtimes);

        showtimeAdapter = new ShowtimeAdapter(this, showtime -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("showtimeId", showtime.getId());
            intent.putExtra("movieId", movieId);
            intent.putExtra("movieTitle", movieTitle);
            intent.putExtra("theaterName", showtime.getTheaterName());
            intent.putExtra("date", showtime.getDate());
            intent.putExtra("time", showtime.getTime());
            intent.putExtra("price", showtime.getPrice());
            startActivity(intent);
        });

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        rvShowtimes.setAdapter(showtimeAdapter);
        rvShowtimes.setNestedScrollingEnabled(false);

        loadMovieDetail();
        loadShowtimes(progressShowtimes, tvNoShowtimes);
    }

    private void loadMovieDetail() {
        if (movieId == null) return;

        db.collection("movies").document(movieId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ImageView ivPoster = findViewById(R.id.ivPosterDetail);
                        TextView tvTitle = findViewById(R.id.tvMovieTitle);
                        TextView tvRating = findViewById(R.id.tvMovieRating);
                        TextView tvDuration = findViewById(R.id.tvMovieDuration);
                        TextView tvLanguage = findViewById(R.id.tvMovieLanguage);
                        TextView tvGenre = findViewById(R.id.tvMovieGenre);
                        TextView tvDescription = findViewById(R.id.tvMovieDescription);

                        String title = doc.getString("title");
                        String posterUrl = doc.getString("posterUrl");
                        Double rating = doc.getDouble("rating");
                        Long duration = doc.getLong("duration");
                        String language = doc.getString("language");
                        String genre = doc.getString("genre");
                        String description = doc.getString("description");

                        if (tvTitle != null) tvTitle.setText(title);
                        if (tvRating != null && rating != null) tvRating.setText(String.format("%.1f", rating));
                        if (tvDuration != null && duration != null) tvDuration.setText(duration + " phút");
                        if (tvLanguage != null) tvLanguage.setText(language);
                        if (tvGenre != null) tvGenre.setText(genre);
                        if (tvDescription != null) tvDescription.setText(description);

                        if (ivPoster != null && posterUrl != null) {
                            Glide.with(this).load(posterUrl).centerCrop().into(ivPoster);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải thông tin phim", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadShowtimes(ProgressBar progress, TextView tvEmpty) {
        if (movieId == null) return;
        progress.setVisibility(View.VISIBLE);

        // Bỏ .orderBy() để tránh yêu cầu Composite Index trong Firestore
        // Sort theo date+time phía client thay thế
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .get()
                .addOnSuccessListener(snap -> {
                    progress.setVisibility(View.GONE);
                    List<Showtime> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Showtime st = doc.toObject(Showtime.class);
                        st.setId(doc.getId());
                        list.add(st);
                    }
                    // Sort client-side theo date rồi time
                    list.sort((a, b) -> {
                        String keyA = (a.getDate() != null ? a.getDate() : "") + (a.getTime() != null ? a.getTime() : "");
                        String keyB = (b.getDate() != null ? b.getDate() : "") + (b.getTime() != null ? b.getTime() : "");
                        return keyA.compareTo(keyB);
                    });
                    showtimeAdapter.setShowtimes(list);
                    tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Lỗi tải lịch chiếu", Toast.LENGTH_SHORT).show();
                });
    }
}
