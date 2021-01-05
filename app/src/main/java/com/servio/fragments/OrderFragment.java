package com.servio.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Dish;
import com.servio.recyclerviews.dishOrder.DishOrderAdapter;

import org.jetbrains.annotations.NotNull;

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
    private List<Dish> dishList = new ArrayList<>();

    //private Order order=new Order();
    private TextView orderPriceTextView;
    private Button saveButton;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private void setLayoutManager() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setAdapter(List<Dish> dishList1) {
        DishOrderAdapter dishOrderAdapter = new DishOrderAdapter(getContext(), dishList1);
        recyclerView.setAdapter(dishOrderAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        emptyOrder = view.findViewById(R.id.emptyOrder);


       /* button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.setText(editText.getText());
                    }
                }
        );*/


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

        if (bundle != null) {
            tableNumber = bundle.getInt("tableNumber");
            hallName = bundle.getString("hallName");
            titleTextView.setText("Comanda masa nr. " + tableNumber);
        }

        recyclerView = view.findViewById(R.id.recyclerViewMenu);
        orderPriceTextView = view.findViewById(R.id.orderPrice);
        /*backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final LinearLayout linearLayout = (LinearLayout) getActivity().getWindow().findViewById(R.id.fragmentsLinearLayout);
                        final ConstraintLayout optionsLayout = (ConstraintLayout) getActivity().getWindow().findViewById(R.id.optionsLayout);
                        final RelativeLayout hallRelativeLayout = (RelativeLayout) getActivity().getWindow().findViewById(R.id.hallRelativeLayout);
                        linearLayout.setVisibility(View.GONE);
                        optionsLayout.setVisibility(View.VISIBLE);
                        hallRelativeLayout.setVisibility(View.VISIBLE);
                    }
                }
        );*/

        saveButton = view.findViewById(R.id.saveData);
       /* saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       *//* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Toast.makeText(getActivity(), user.getUid(), Toast.LENGTH_SHORT).show();

                        Calendar calendar=Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String formattedDate=simpleDateFormat.format(calendar.getTime());
                        Toast.makeText(getActivity(), formattedDate, Toast.LENGTH_SHORT).show();*//*

                        Toast.makeText(getActivity(), String.valueOf(order.getOrderPrice()), Toast.LENGTH_SHORT).show();
                    }
                }
        );*/

        setLayoutManager();
        setAdapter(dishList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedViewModel viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        viewModel.dishList.observe(getViewLifecycleOwner(),
                new Observer<List<Dish>>() {
                    @Override
                    public void onChanged(final List<Dish> list) {

                        if (list.size() > 0) {
                            emptyOrder.setVisibility(View.GONE);
                        } else {
                            emptyOrder.setVisibility(View.VISIBLE);
                        }

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
                                            Toast.makeText(getActivity(), "Comanda a fost finalizata cu succes.", Toast.LENGTH_SHORT).show();

                                            String s= "salut";
                                            simpleCallback.callback(s);
                                        }
                                    }
                                }
                        );
                    }
                });

    }

    private SimpleCallback simpleCallback;
    @Override
    public void onAttach(@NotNull Activity activity) {
        super.onAttach(activity);
        try {
            simpleCallback = (SimpleCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
}
