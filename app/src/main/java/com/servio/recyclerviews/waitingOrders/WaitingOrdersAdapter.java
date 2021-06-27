package com.servio.recyclerviews.waitingOrders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Order;

import java.util.List;

public class WaitingOrdersAdapter extends RecyclerView.Adapter<WaitingOrdersViewHolder> {
    private final List<Order> waitingOrdersList;
    private final Context context;

    public WaitingOrdersAdapter(List<Order> waitingOrdersList, Context context) {
        this.waitingOrdersList = waitingOrdersList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final WaitingOrdersViewHolder holder, int position) {
        final Order order = waitingOrdersList.get(position);
        if (order != null) {
            FirebaseDatabaseHelper firebaseDatabaseHelper;
            FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, context);

            firebaseDatabaseHelper.getSingleLongFieldValueWithCondition("Halls", "hallNumber", "hallName", order.getHallName(),
                    new SimpleCallback<Long>() {
                        @Override
                        public void callback(Long hallNumber) {
                            String orderNumber;
                            if (order.getTableNumber() < 10) {
                                orderNumber = String.valueOf(hallNumber) + '0' + order.getTableNumber();
                            } else {
                                orderNumber = String.valueOf(hallNumber) + order.getTableNumber();
                            }
                            holder.getOrderNumber().setText(orderNumber);
                        }
                    });
        }

        holder.getCardView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("waitingOrder-object");
                        intent.putExtra("waitingOrder", order);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
        );
    }

    @NonNull
    @Override
    public WaitingOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_in_preparation_view, parent, false);
        return new WaitingOrdersViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (waitingOrdersList != null) {
            return waitingOrdersList.size();
        }
        return 0;
    }
}
