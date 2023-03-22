package com.servio.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.activities.PrepareOrdersActivity;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Dish;
import com.servio.models.Order;
import com.servio.recyclerviews.dishWaiter.DishWaiterAdapter;
import com.servio.recyclerviews.waitingOrders.WaitingOrdersAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class MenuFragment extends Fragment {
    private SharedViewModel viewModel;
    private DishWaiterAdapter dishWaiterAdapter;
    private FirebaseFirestore firebaseReference;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private EditText searchView;
    private RecyclerView recyclerView;
    private List<Dish> dishList = new ArrayList<>();
    final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private void setLayoutManager() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setAdapter() {
        dishWaiterAdapter = new DishWaiterAdapter(getContext(), dishList);
        recyclerView.setAdapter(dishWaiterAdapter);
    }

    private void getDishes() {
        firebaseDatabaseHelper.getSingleDataFieldValue("Employees", currentUserId, "restaurant", new SimpleCallback<String>() {
            @Override
            public void callback(String restaurant) {
                firebaseDatabaseHelper.getDishesData(new SimpleCallback<List<Dish>>() {
                    @Override
                    public void callback(List<Dish> list) {
                        dishList = new ArrayList<>(list);
                        setAdapter();
                    }
                }, restaurant);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchDishEditText);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        recyclerView = view.findViewById(R.id.recyclerViewMenu);

        firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, getContext());

        setLayoutManager();
        getDishes();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<Dish> filteredList = new ArrayList<>();

        for (Dish dish : dishList) {
            if (dish.getDishName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(dish);
            }
        }

        dishWaiterAdapter.filterList(filteredList);
    }
}
