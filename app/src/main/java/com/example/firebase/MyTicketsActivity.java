package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.adapter.TicketAdapter;
import com.example.firebase.model.Ticket;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private TicketAdapter ticketAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        rvTickets = findViewById(R.id.rvTickets);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        ticketAdapter = new TicketAdapter(this);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        rvTickets.setAdapter(ticketAdapter);

        loadTickets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTickets();
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

    private void loadTickets() {
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        progressBar.setVisibility(View.VISIBLE);

        db.collection("tickets")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(snap -> {
                    progressBar.setVisibility(View.GONE);
                    List<Ticket> tickets = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Ticket ticket = doc.toObject(Ticket.class);
                        ticket.setId(doc.getId());
                        tickets.add(ticket);
                    }
                    // Sort client-side: vé mới nhất lên trên
                    tickets.sort((a, b) -> Long.compare(b.getBookedAt(), a.getBookedAt()));
                    ticketAdapter.setTickets(tickets);
                    if (tickets.isEmpty()) {
                        rvTickets.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvTickets.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tải danh sách vé", Toast.LENGTH_SHORT).show();
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvTickets.setVisibility(View.GONE);
                });
    }
}
