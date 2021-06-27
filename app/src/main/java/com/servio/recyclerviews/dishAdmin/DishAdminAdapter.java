package com.servio.recyclerviews.dishAdmin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.servio.R;
import com.servio.activities.DishInfoActivity;
import com.servio.activities.EditDishActivity;
import com.servio.models.Dish;

import org.jetbrains.annotations.NotNull;

public class DishAdminAdapter extends FirestoreRecyclerAdapter<Dish, DishAdminViewHolder> {
    private final Context context;

    public DishAdminAdapter(@NonNull FirestoreRecyclerOptions<Dish> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull DishAdminViewHolder dishAdminViewHolder, int i, @NotNull Dish dish) {
        dishAdminViewHolder.setDishName(dish.getDishName());
        dishAdminViewHolder.setDishCategory(dish.getDishCategory());

        setClickListeners(dishAdminViewHolder, dish);
    }

    private void setClickListeners(@NonNull final DishAdminViewHolder dishAdminViewHolder, final Dish dish) {
        dishAdminViewHolder.getButtonRemove().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Confirmare stergere produs");
                        alertDialog.setMessage("Doriti sa stergeti produsul " + dish.getDishName() + "?");

                        alertDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSnapshots().getSnapshot(dishAdminViewHolder.getAdapterPosition()).getReference().delete();
                                Toast.makeText(context, "Produsul a fost sters cu succes", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialog.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }
                }
        );

        dishAdminViewHolder.getButtonEdit().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context.getApplicationContext(),
                                EditDishActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dish", dish);
                        intent.putExtras(bundle);

                        context.startActivity(intent);
                    }
                }
        );

        dishAdminViewHolder.getButtonInfo().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context.getApplicationContext(),
                                DishInfoActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dish", dish);
                        intent.putExtras(bundle);

                        context.startActivity(intent);
                    }
                }
        );
    }

    @NonNull
    @Override
    public DishAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_admin_view, parent, false);
        return new DishAdminViewHolder(view);
    }
}