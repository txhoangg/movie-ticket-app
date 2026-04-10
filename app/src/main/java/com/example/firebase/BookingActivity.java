package com.example.firebase;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.adapter.SeatAdapter;
import com.example.firebase.model.Seat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private static final int COLS = 8;
    private static final char[] ROWS = {'A', 'B', 'C', 'D', 'E'};
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private String showtimeId, movieId, movieTitle, theaterName, date, time;
    private double pricePerSeat;

    private SeatAdapter seatAdapter;
    private List<Seat> seatList = new ArrayList<>();
    private List<String> selectedSeats = new ArrayList<>();

    private TextView tvSelectedSeats, tvTotalPrice;
    private MaterialButton btnConfirm;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // FIX: Null check cho tất cả intent extras
        Intent intent = getIntent();
        showtimeId = intent.getStringExtra("showtimeId");
        movieId = intent.getStringExtra("movieId");
        movieTitle = intent.getStringExtra("movieTitle");
        theaterName = intent.getStringExtra("theaterName");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        pricePerSeat = intent.getDoubleExtra("price", 85000);

        if (showtimeId == null || movieId == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin suất chiếu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvMovieTitle = findViewById(R.id.tvBookingMovieTitle);
        TextView tvTheater = findViewById(R.id.tvBookingTheater);
        TextView tvDateTime = findViewById(R.id.tvBookingDateTime);
        tvMovieTitle.setText(movieTitle != null ? movieTitle : "");
        tvTheater.setText(theaterName != null ? theaterName : "");
        tvDateTime.setText((date != null ? date : "") + "  •  " + (time != null ? time : ""));

        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        RecyclerView rvSeats = findViewById(R.id.rvSeats);
        seatAdapter = new SeatAdapter(this, this::onSeatClick);
        rvSeats.setLayoutManager(new GridLayoutManager(this, COLS));
        rvSeats.setAdapter(seatAdapter);

        createNotificationChannel();
        // FIX: Xin quyền POST_NOTIFICATIONS runtime (Android 13+ bắt buộc)
        requestNotificationPermission();
        loadSeats();

        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void loadSeats() {
        db.collection("showtimes").document(showtimeId).get()
                .addOnSuccessListener(doc -> {
                    List<String> booked = (List<String>) doc.get("bookedSeats");
                    if (booked == null) booked = new ArrayList<>();

                    seatList.clear();
                    for (char row : ROWS) {
                        for (int col = 1; col <= COLS; col++) {
                            String seatId = row + String.valueOf(col);
                            int status = booked.contains(seatId) ? Seat.STATUS_BOOKED : Seat.STATUS_AVAILABLE;
                            seatList.add(new Seat(seatId, status));
                        }
                    }
                    seatAdapter.setSeats(seatList);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải sơ đồ ghế", Toast.LENGTH_SHORT).show()
                );
    }

    private void onSeatClick(Seat seat, int position) {
        if (seat.getStatus() == Seat.STATUS_AVAILABLE) {
            seat.setStatus(Seat.STATUS_SELECTED);
            selectedSeats.add(seat.getId());
        } else if (seat.getStatus() == Seat.STATUS_SELECTED) {
            seat.setStatus(Seat.STATUS_AVAILABLE);
            selectedSeats.remove(seat.getId());
        }
        seatAdapter.notifyItemChanged(position);
        updateBottomPanel();
    }

    private void updateBottomPanel() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Chưa chọn ghế");
            tvTotalPrice.setText("0 đ");
            btnConfirm.setEnabled(false);
        } else {
            tvSelectedSeats.setText("Ghế: " + String.join(", ", selectedSeats));
            double total = selectedSeats.size() * pricePerSeat;
            NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvTotalPrice.setText(fmt.format((long) total) + " đ");
            btnConfirm.setEnabled(true);
        }
    }

    private void confirmBooking() {
        if (selectedSeats.isEmpty()) return;
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang đặt...");

        double totalPrice = selectedSeats.size() * pricePerSeat;
        List<String> seatsToBook = new ArrayList<>(selectedSeats);

        DocumentReference showtimeRef = db.collection("showtimes").document(showtimeId);
        // Pre-generate ticket ID để dùng trong transaction
        DocumentReference ticketRef = db.collection("tickets").document();

        Map<String, Object> ticketData = new HashMap<>();
        ticketData.put("userId", uid);
        ticketData.put("movieId", movieId);
        ticketData.put("movieTitle", movieTitle);
        ticketData.put("showtimeId", showtimeId);
        ticketData.put("theaterName", theaterName);
        ticketData.put("date", date);
        ticketData.put("time", time);
        ticketData.put("seats", seatsToBook);
        ticketData.put("totalPrice", totalPrice);
        ticketData.put("status", "CONFIRMED");
        ticketData.put("bookedAt", System.currentTimeMillis());

        // FIX: Firestore Transaction - đảm bảo atomic:
        // - Kiểm tra ghế chưa bị đặt bởi người khác
        // - Update bookedSeats + tạo ticket trong 1 lần ghi
        // - Nếu bất kỳ bước nào lỗi → tự rollback hoàn toàn
        db.runTransaction(transaction -> {
            DocumentSnapshot showtimeSnap = transaction.get(showtimeRef);

            List<String> currentBooked = (List<String>) showtimeSnap.get("bookedSeats");
            if (currentBooked == null) currentBooked = new ArrayList<>();

            // Kiểm tra xung đột: ghế đã bị đặt bởi người khác chưa?
            for (String seat : seatsToBook) {
                if (currentBooked.contains(seat)) {
                    throw new FirebaseFirestoreException(
                            "Ghế " + seat + " vừa được đặt bởi người khác. Vui lòng chọn ghế khác.",
                            FirebaseFirestoreException.Code.ABORTED
                    );
                }
            }

            // Atomic: cập nhật bookedSeats
            List<String> newBooked = new ArrayList<>(currentBooked);
            newBooked.addAll(seatsToBook);
            transaction.update(showtimeRef, "bookedSeats", newBooked);

            // Atomic: tạo ticket trong cùng 1 transaction
            transaction.set(ticketRef, ticketData);

            return null;
        }).addOnSuccessListener(v -> {
            scheduleNotification();
            Toast.makeText(this, "🎉 Đặt vé thành công!", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            btnConfirm.setEnabled(true);
            btnConfirm.setText("XÁC NHẬN");
            String msg = (e.getMessage() != null) ? e.getMessage() : "Đặt vé thất bại. Vui lòng thử lại.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            // Reload ghế để hiển thị trạng thái mới nhất
            selectedSeats.clear();
            updateBottomPanel();
            loadSeats();
        });
    }

    private void scheduleNotification() {
        try {
            if (date == null || time == null) return;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date showDate = sdf.parse(date + " " + time);
            if (showDate == null) return;

            Calendar cal = Calendar.getInstance();
            cal.setTime(showDate);
            cal.add(Calendar.MINUTE, -30);

            long triggerTime = cal.getTimeInMillis();
            // Vì ngày seed là tương lai nên triggerTime luôn > currentTimeMillis
            if (triggerTime <= System.currentTimeMillis()) return;

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("movieTitle", movieTitle);
            intent.putExtra("theaterName", theaterName);
            intent.putExtra("time", time);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "cinebook_reminders",
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(getString(R.string.notification_channel_desc));
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}
