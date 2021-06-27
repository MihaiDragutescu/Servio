package com.servio.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.activities.TakeOrdersActivity;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.models.Dish;
import com.servio.recyclerviews.dishOrder.DishOrderAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFragment extends Fragment {
    private String hallName;
    private int tableNumber;

    private TextView emptyOrder;
    private RecyclerView recyclerView;
    private List<Dish> dishList;

    private TextView orderPriceTextView;
    private Button saveButton;

    DishOrderAdapter dishOrderAdapter;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    SharedViewModel viewModel;

    private void setLayoutManager() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setAdapter(List<Dish> dishList1) {
        dishOrderAdapter = new DishOrderAdapter(getContext(), dishList1);
        recyclerView.setAdapter(dishOrderAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        emptyOrder = view.findViewById(R.id.emptyOrder);
        recyclerView = view.findViewById(R.id.recyclerViewMenu);
        orderPriceTextView = view.findViewById(R.id.orderPrice);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, getActivity());

        TextView titleTextView = view.findViewById(R.id.titleTxtView);
        Bundle bundle = getArguments();

        dishList = new ArrayList<>();
        viewModel.setDishList(dishList);

        if (bundle != null) {
            tableNumber = bundle.getInt("tableNumber");
            hallName = bundle.getString("hallName");
            dishList = (List<Dish>) getArguments().getSerializable("dishList");
            titleTextView.setText("Comanda masa nr. " + tableNumber);

            if (dishList == null || dishList.isEmpty()) {
                dishList = new ArrayList<>();
                viewModel.setDishList(dishList);
                setLayoutManager();
                setAdapter(dishList);
                emptyOrder.setVisibility(View.VISIBLE);
            } else {
                emptyOrder.setVisibility(View.GONE);

                viewModel.setDishList(dishList);
                setLayoutManager();
                setAdapter(dishList);
            }

        } else {
            setLayoutManager();
            setAdapter(dishList);
        }

        saveButton = view.findViewById(R.id.saveData);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.dishList.observe(getViewLifecycleOwner(),
                new Observer<List<Dish>>() {
                    @Override
                    public void onChanged(final List<Dish> list) {

                        if (list.isEmpty() || list == null) {
                            emptyOrder.setVisibility(View.VISIBLE);
                        } else {
                            emptyOrder.setVisibility(View.GONE);

                            setAdapter(list);

                            double sum = 0.0;

                            for (int i = 0; i < list.size(); i++) {
                                sum += list.get(i).getDishPrice() * list.get(i).getDishQuantity();
                            }

                            String priceLabel = "Pret: " + sum + " lei";
                            orderPriceTextView.setText(priceLabel);

                            final double finalSum = sum;
                            saveButton.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (list.size() == 0) {
                                                Toast.makeText(getActivity(), "Nu au fost adaugate produse la comanda", Toast.LENGTH_SHORT).show();
                                            } else {
                                                android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                                alertDialog.setTitle("Confirmare finalizare comandă");
                                                alertDialog.setMessage("Doriți să finalizați comanda?");

                                                alertDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String hallNameAux = hallName.replaceAll("\\s+", "");
                                                        final Map<String, Object> orderMap = new HashMap<>();

                                                        String orderId = hallNameAux + "Masa" + tableNumber;
                                                        orderMap.put("orderId", orderId);

                                                        orderMap.put("orderedDishes", list);

                                                        Calendar calendar = Calendar.getInstance();
                                                        @SuppressLint("SimpleDateFormat")
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                        String formattedDate = simpleDateFormat.format(calendar.getTime());
                                                        orderMap.put("orderTime", formattedDate);

                                                        orderMap.put("orderName", "Comanda masa nr. " + tableNumber);

                                                        orderMap.put("hallName", hallName);

                                                        orderMap.put("tableNumber", tableNumber);

                                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                        orderMap.put("waiterName", user.getDisplayName());

                                                        orderMap.put("orderPrice", finalSum);

                                                        orderMap.put("orderStatus", "waiting");

                                                        firebaseDatabaseHelper.insertData("Orders", orderId, orderMap);
                                                        firebaseDatabaseHelper.simpleUpdateField("Tables", hallNameAux + tableNumber, "tableAvailability", false);

                                                        ((TakeOrdersActivity) getActivity()).saveOrder();
                                                        dishList = new ArrayList<>();
                                                        viewModel.setDishList(dishList);
                                                        setLayoutManager();
                                                        setAdapter(dishList);

                                                        Toast.makeText(getActivity(), "Comanda a fost finalizată", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                alertDialog.setNegativeButton("Anulează", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alertDialog.show();
                                            }
                                        }
                                    }
                            );
                        }
                    }
                });

    }

    public void reset() {
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        dishList = new ArrayList<>();
        viewModel.setDishList(dishList);
        setLayoutManager();
        setAdapter(dishList);
    }
}


