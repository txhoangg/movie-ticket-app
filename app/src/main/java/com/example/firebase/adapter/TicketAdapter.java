package com.example.firebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.model.Ticket;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final Context context;
    private List<Ticket> tickets;

    public TicketAdapter(Context context) {
        this.context = context;
        this.tickets = new ArrayList<>();
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        holder.tvMovieTitle.setText(ticket.getMovieTitle());
        holder.tvTheater.setText(ticket.getTheaterName());
        holder.tvDate.setText(ticket.getDate());
        holder.tvTime.setText(ticket.getTime());
        holder.tvSeatCount.setText(ticket.getSeats().size() + " ghế");

        String seatsStr = "Ghế: " + String.join(", ", ticket.getSeats());
        holder.tvSeats.setText(seatsStr);

        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvTotalPrice.setText(fmt.format((long) ticket.getTotalPrice()) + " đ");

        holder.tvStatus.setText("Đã xác nhận");
    }

    @Override
    public int getItemCount() { return tickets.size(); }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvTheater, tvDate, tvTime, tvSeatCount, tvSeats, tvTotalPrice, tvStatus;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvTicketMovieTitle);
            tvTheater = itemView.findViewById(R.id.tvTicketTheater);
            tvDate = itemView.findViewById(R.id.tvTicketDate);
            tvTime = itemView.findViewById(R.id.tvTicketTime);
            tvSeatCount = itemView.findViewById(R.id.tvTicketSeatCount);
            tvSeats = itemView.findViewById(R.id.tvTicketSeats);
            tvTotalPrice = itemView.findViewById(R.id.tvTicketTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvTicketStatus);
        }
    }
}
