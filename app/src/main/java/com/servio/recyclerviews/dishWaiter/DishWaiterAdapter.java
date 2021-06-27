package com.servio.recyclerviews.dishWaiter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;
import com.servio.activities.DishInfoDialogActivity;
import com.servio.fragments.SharedViewModel;
import com.servio.models.Dish;

import java.util.ArrayList;
import java.util.List;

public class DishWaiterAdapter extends RecyclerView.Adapter<DishWaiterViewHolder> {
    private final SharedViewModel viewModel;
    private final Context mContext;
    private List<Dish> dishList;
    private final List<Dish> orderDishList;

    public DishWaiterAdapter(Context context, List<Dish> dishList) {
        mContext = context;
        this.dishList = dishList;

        orderDishList = new ArrayList<>();
        viewModel = ViewModelProviders.of((FragmentActivity) context).get(SharedViewModel.class);
    }

    @Override
    public void onBindViewHolder(@NonNull DishWaiterViewHolder holder, int position) {
        final Dish dish = dishList.get(position);
        if (dish != null) {
            if (dish.getDishName() != null) {
                holder.getDishName().setText(dish.getDishName());
            }
            if (dish.getDishCategory() != null) {
                holder.getDishCategory().setText(dish.getDishCategory());
            }
            if (dish.getDishQuantity() != 0) {
                holder.getDishQuantity().setText(String.valueOf(dish.getDishQuantity()));
            }
        }

        holder.getCardView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext.getApplicationContext(),
                                DishInfoDialogActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dish", dish);
                        intent.putExtras(bundle);

                        mContext.startActivity(intent);
                    }
                }
        );

        setClickListeners(holder, dish);
    }

    private void setClickListeners(@NonNull final DishWaiterViewHolder dishViewHolder, final Dish dish) {
        dishViewHolder.getButtonAdd().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(String.valueOf(dishViewHolder.getDishQuantity().getText()));

                        if (orderDishList.size() != 0) {
                            int valid = 1;
                            int updatedQuantity;
                            for (int i = 0; i < orderDishList.size(); i++) {
                                if (orderDishList.get(i).getDishName().equals(dish.getDishName())) {
                                    valid = 0;
                                    updatedQuantity = quantity + dish.getDishQuantity();
                                    orderDishList.get(i).setDishQuantity(updatedQuantity);
                                }
                            }
                            if (valid == 1) {
                                dish.setDishQuantity(quantity);
                                orderDishList.add(dish);
                            }
                        } else {
                            dish.setDishQuantity(quantity);
                            orderDishList.add(dish);
                        }
                        viewModel.setDishList(orderDishList);
                    }
                }
        );
    }

    @NonNull
    @Override
    public DishWaiterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_waiter_view, parent, false);
        return new DishWaiterViewHolder(view);
    }


    @Override
    public int getItemCount() {
        if (dishList != null) {
            return dishList.size();
        }

        return 0;
    }

    public void filterList(ArrayList<Dish> filteredList) {
        dishList = filteredList;
        notifyDataSetChanged();
    }
}
