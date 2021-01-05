package com.servio.recyclerviews.preparingOrders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

class PreparingOrdersViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout linearLayout;

    private TextView orderNumber;

    PreparingOrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        linearLayout =itemView.findViewById(R.id.ordersInPreparationLayout);
        orderNumber = itemView.findViewById(R.id.orderNumberTextView);
    }

    void setOrderNumber(String number) {
        orderNumber.setText(number);
    }

    TextView getOrderNumber(){return orderNumber;}

    LinearLayout getCardView(){return linearLayout;}
}
