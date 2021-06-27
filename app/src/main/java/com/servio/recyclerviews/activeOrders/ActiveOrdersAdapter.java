package com.servio.recyclerviews.activeOrders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;
import com.servio.models.Order;

import java.util.List;

public class ActiveOrdersAdapter extends RecyclerView.Adapter<ActiveOrdersViewHolder> {
    private final List<Order> activeOrdersList;

    public ActiveOrdersAdapter(List<Order> activeOrdersList) {
        this.activeOrdersList = activeOrdersList;
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveOrdersViewHolder holder, int position) {
        final Order order = activeOrdersList.get(position);
        if (order != null) {
            if (order.getOrderName() != null) {
                holder.getOrderName().setText(order.getOrderName());
            }
        }

        holder.getCardView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }

    @NonNull
    @Override
    public ActiveOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_orders_view, parent, false);
        return new ActiveOrdersViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (activeOrdersList != null) {
            return activeOrdersList.size();
        }
        return 0;
    }
}
