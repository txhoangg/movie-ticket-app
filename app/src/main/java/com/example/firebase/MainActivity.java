package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.adapter.MovieAdapter;
import com.example.firebase.model.Movie;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private MovieAdapter movieAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        rvMovies = findViewById(R.id.rvMovies);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        movieAdapter = new MovieAdapter(this, movie -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getId());
            intent.putExtra("movieTitle", movie.getTitle());
            startActivity(intent);
        });

        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        rvMovies.setAdapter(movieAdapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_tickets) {
                startActivity(new Intent(this, MyTicketsActivity.class));
                bottomNav.setSelectedItemId(R.id.nav_movies);
            }
            return true;
        });

        // FIX: Kiểm tra flag trước khi seed để tránh seed trùng dữ liệu
        checkAndSeedData();
    }

    // FIX: Dùng flag "seeded" trong Firestore để chỉ seed 1 lần duy nhất
    private void checkAndSeedData() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("config").document("appConfig").get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists() || !Boolean.TRUE.equals(doc.getBoolean("seeded"))) {
                        seedMovieData();
                    } else {
                        loadMovies();
                    }
                })
                .addOnFailureListener(e -> loadMovies());
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("movies")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    List<Movie> movies = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Movie movie = doc.toObject(Movie.class);
                        movie.setId(doc.getId());
                        movies.add(movie);
                    }
                    movieAdapter.setMovies(movies);
                    tvEmpty.setVisibility(movies.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tải danh sách phim", Toast.LENGTH_SHORT).show();
                });
    }

    private void seedMovieData() {
        List<Map<String, Object>> movies = new ArrayList<>();

        movies.add(createMovie("Avengers: Endgame",
                "Hành động, Khoa học viễn tưởng",
                "Sau thảm họa do Thanos gây ra, các Avengers còn lại tập hợp để đảo ngược hành động của hắn và khôi phục trật tự vũ trụ.",
                8.4, 181,
                "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
                "Tiếng Anh", "2019"));

        movies.add(createMovie("The Dark Knight",
                "Hành động, Tội phạm",
                "Khi Joker gieo rắc hỗn loạn và tàn phá ở Gotham City, Batman phải đối mặt với một trong những thử thách tâm lý lớn nhất.",
                9.0, 152,
                "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                "Tiếng Anh", "2008"));

        movies.add(createMovie("Inception",
                "Khoa học viễn tưởng, Hành động",
                "Một tên trộm đặc biệt sử dụng công nghệ xâm nhập giấc mơ để lấy cắp bí mật từ tiềm thức của các mục tiêu.",
                8.8, 148,
                "https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",
                "Tiếng Anh", "2010"));

        movies.add(createMovie("Interstellar",
                "Khoa học viễn tưởng, Phiêu lưu",
                "Một nhóm phi hành gia vượt qua hố giun để tìm kiếm ngôi nhà mới cho nhân loại khi Trái Đất đang hấp hối.",
                8.6, 169,
                "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                "Tiếng Anh", "2014"));

        movies.add(createMovie("Spider-Man: No Way Home",
                "Hành động, Phiêu lưu",
                "Peter Parker cầu xin Doctor Strange giúp mọi người quên rằng anh là Spider-Man, dẫn đến sự xâm nhập của kẻ phản diện từ các vũ trụ song song.",
                8.2, 148,
                "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
                "Tiếng Anh", "2021"));

        movies.add(createMovie("Dune: Part Two",
                "Khoa học viễn tưởng, Phiêu lưu",
                "Paul Atreides hợp nhất với người Fremen trong khi trên đường trả thù những kẻ đã phá hủy gia đình mình.",
                8.5, 166,
                "https://image.tmdb.org/t/p/w500/cdqLnri3NEGcmfnqwk2TSIYtddg.jpg",
                "Tiếng Anh", "2024"));

        final int[] count = {0};
        for (Map<String, Object> movie : movies) {
            db.collection("movies").add(movie)
                    .addOnSuccessListener(ref -> {
                        count[0]++;
                        if (count[0] == movies.size()) {
                            seedShowtimes();
                        }
                    });
        }
    }

    private Map<String, Object> createMovie(String title, String genre, String desc,
                                             double rating, int duration, String poster,
                                             String lang, String year) {
        Map<String, Object> m = new HashMap<>();
        m.put("title", title);
        m.put("genre", genre);
        m.put("description", desc);
        m.put("rating", rating);
        m.put("duration", duration);
        m.put("posterUrl", poster);
        m.put("language", lang);
        m.put("releaseYear", year);
        return m;
    }

    private void seedShowtimes() {
        db.collection("movies").get().addOnSuccessListener(snap -> {
            String[] theaters = {
                    "CGV Vincom Center", "Lotte Cinema Landmark", "Galaxy Nguyễn Du"
            };
            String[] addresses = {
                    "Tầng 6, Vincom Center, Q.1",
                    "Tầng 5, Landmark 81, Bình Thạnh",
                    "116 Nguyễn Du, Q.1"
            };
            // FIX: Tạo ngày động từ hôm nay +1, +2, +3 thay vì hardcode 2025
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String[] dates = new String[3];
            for (int i = 0; i < 3; i++) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                dates[i] = sdf.format(cal.getTime());
            }

            String[][] times = {
                    {"10:00", "13:30", "17:00", "20:30"},
                    {"11:00", "14:00", "18:30", "21:00"},
                    {"09:30", "12:30", "16:00", "19:30"}
            };
            double[] prices = {85000, 95000, 75000};

            for (QueryDocumentSnapshot movieDoc : snap) {
                String movieId = movieDoc.getId();
                for (int t = 0; t < theaters.length; t++) {
                    for (String date : dates) {
                        String time = times[t][(int) (Math.random() * times[t].length)];
                        Map<String, Object> showtime = new HashMap<>();
                        showtime.put("movieId", movieId);
                        showtime.put("theaterName", theaters[t]);
                        showtime.put("address", addresses[t]);
                        showtime.put("date", date);
                        showtime.put("time", time);
                        showtime.put("totalSeats", 40);
                        showtime.put("bookedSeats", new ArrayList<>());
                        showtime.put("price", prices[t]);
                        db.collection("showtimes").add(showtime);
                    }
                }
            }

            // FIX: Đánh dấu đã seed để không seed lại lần sau
            Map<String, Object> config = new HashMap<>();
            config.put("seeded", true);
            config.put("seededAt", System.currentTimeMillis());
            db.collection("config").document("appConfig").set(config)
                    .addOnSuccessListener(v -> loadMovies());
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (d, w) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
