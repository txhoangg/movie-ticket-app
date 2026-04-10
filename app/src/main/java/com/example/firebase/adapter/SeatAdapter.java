package com.example.firebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.model.Seat;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private final Context context;
    private List<Seat> seats;
    private OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat, int position);
    }

    public SeatAdapter(Context context, OnSeatClickListener listener) {
        this.context = context;
        this.seats = new ArrayList<>();
        this.listener = listener;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seats.get(position);
        holder.tvSeat.setText(seat.getId());

        switch (seat.getStatus()) {
            case Seat.STATUS_AVAILABLE:
                holder.tvSeat.setBackground(context.getDrawable(R.drawable.bg_seat_available));
                holder.tvSeat.setTextColor(context.getColor(R.color.white));
                holder.itemView.setEnabled(true);
                break;
            case Seat.STATUS_SELECTED:
                holder.tvSeat.setBackground(context.getDrawable(R.drawable.bg_seat_selected));
                holder.tvSeat.setTextColor(context.getColor(R.color.white));
                holder.itemView.setEnabled(true);
                break;
            case Seat.STATUS_BOOKED:
                holder.tvSeat.setBackground(context.getDrawable(R.drawable.bg_seat_booked));
                holder.tvSeat.setTextColor(context.getColor(R.color.white));
                holder.itemView.setEnabled(false);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (seat.getStatus() != Seat.STATUS_BOOKED) {
                listener.onSeatClick(seat, position);
            }
        });
    }

    @Override
    public int getItemCount() { return seats.size(); }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeat;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeat = itemView.findViewById(R.id.tvSeat);
        }
    }
}
