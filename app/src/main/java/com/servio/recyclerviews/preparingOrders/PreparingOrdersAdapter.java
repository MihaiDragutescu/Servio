package com.servio.recyclerviews.preparingOrders;

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

import java.io.Serializable;
import java.util.List;

public class PreparingOrdersAdapter extends RecyclerView.Adapter<PreparingOrdersViewHolder> {
    private List<Order> preparingOrdersList;
    private Context context;

    public PreparingOrdersAdapter(List<Order> preparingOrdersList, Context context) {
        this.preparingOrdersList = preparingOrdersList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final PreparingOrdersViewHolder holder, int position) {
        final Order order = preparingOrdersList.get(position);
        if (order != null) {
            FirebaseDatabaseHelper firebaseDatabaseHelper;
            FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, context);

            firebaseDatabaseHelper.getSingleLongFieldValueWithCondition("Halls", "hallNumber", "hallName", order.getHallName(),
                    new SimpleCallback<Long>() {
                        @Override
                        public void callback(Long hallNumber) {
                            if (order.getTableNumber() < 10) {
                                String orderNumber = String.valueOf(hallNumber) + '0' + order.getTableNumber();
                                holder.getOrderNumber().setText(orderNumber);
                            } else {
                                String orderNumber = String.valueOf(hallNumber) + order.getTableNumber();
                                holder.getOrderNumber().setText(orderNumber);
                            }
                        }
                    });
        }

        holder.getCardView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("preparingOrder-object");
                        intent.putExtra("preparingOrder", (Serializable) order);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
        );
    }

    @NonNull
    @Override
    public PreparingOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_in_preparation_view, parent, false);
        return new PreparingOrdersViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (preparingOrdersList != null) {
            return preparingOrdersList.size();
        }
        return 0;
    }
}
