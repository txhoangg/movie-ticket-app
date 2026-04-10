package com.example.firebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.model.Showtime;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private final Context context;
    private List<Showtime> showtimes;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Showtime showtime);
    }

    public ShowtimeAdapter(Context context, OnBookClickListener listener) {
        this.context = context;
        this.showtimes = new ArrayList<>();
        this.listener = listener;
    }

    public void setShowtimes(List<Showtime> showtimes) {
        this.showtimes = showtimes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);

        holder.tvTheaterName.setText(showtime.getTheaterName());
        holder.tvDate.setText(showtime.getDate());
        holder.tvTime.setText(showtime.getTime());

        int available = showtime.getAvailableSeats();
        holder.tvAvailableSeats.setText(available + " ghế trống");
        holder.tvAvailableSeats.setTextColor(
                available > 0 ? context.getColor(R.color.colorSeatAvailable) : context.getColor(R.color.colorTextSecondary)
        );

        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(fmt.format((long) showtime.getPrice()) + " đ");

        holder.btnBookNow.setEnabled(available > 0);
        holder.btnBookNow.setOnClickListener(v -> listener.onBookClick(showtime));
    }

    @Override
    public int getItemCount() { return showtimes.size(); }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTheaterName, tvDate, tvTime, tvAvailableSeats, tvPrice;
        MaterialButton btnBookNow;

        ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}
