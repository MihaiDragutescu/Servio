package com.servio.recyclerviews.dishOrder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;
import com.servio.fragments.SharedViewModel;
import com.servio.models.Dish;

import java.util.List;

public class DishOrderAdapter extends RecyclerView.Adapter<DishOrderViewHolder> {
    private SharedViewModel viewModel;
    private Context context;
    private List<Dish> dishList;

    public DishOrderAdapter(Context context, List<Dish> dishList) {
        this.context = context;
        this.dishList = dishList;
        viewModel = ViewModelProviders.of((FragmentActivity) context).get(SharedViewModel.class);
    }

    @Override
    public void onBindViewHolder(@NonNull DishOrderViewHolder holder, int position) {
        final Dish dish = dishList.get(position);
        if (dish != null) {
            if (dish.getDishName() != null) {
                holder.getDishName().setText(dish.getDishName());
            }
            if (dish.getDishQuantity() != 0) {
                String quantity = "X " + dish.getDishQuantity();
                holder.getDishQuantity().setText(quantity);
            }
        }

        setClickListeners(holder);
    }

    private void setClickListeners(@NonNull final DishOrderViewHolder dishViewHolder) {
        dishViewHolder.getButtonRemove().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = dishViewHolder.getAdapterPosition();
                        dishList.remove(position);
                        viewModel.setDishList(dishList);
                        notifyItemRemoved(position);
                    }
                }
        );
    }

    @NonNull
    @Override
    public DishOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_order_view, parent, false);
        return new DishOrderViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (dishList != null) {
            return dishList.size();
        }
        return 0;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
