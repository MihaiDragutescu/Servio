package com.servio.recyclerviews.activeOrders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

class ActiveOrdersViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout constraintLayout;

    private TextView orderName;

    ActiveOrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        constraintLayout =itemView.findViewById(R.id.constraintLayout);
        orderName = itemView.findViewById(R.id.orderNameRecyclerView);
    }

    void setOrderName(String name) {
        orderName.setText(name);
    }

    TextView getOrderName(){return orderName;}

    ConstraintLayout getCardView(){return constraintLayout;}
}
