package com.servio.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Dish;
import com.servio.models.Order;
import com.servio.recyclerviews.preparingOrders.PreparingOrdersAdapter;
import com.servio.recyclerviews.waitingOrders.WaitingOrdersAdapter;

import java.util.ArrayList;
import java.util.List;

public class PrepareOrdersActivity extends AppCompatActivity implements View.OnTouchListener {
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private RecyclerView waitingOrdersRecyclerView;
    private RecyclerView preparingOrdersRecyclerView;

    private List<String> openedOrders=new ArrayList<>();
    private int xDelta;
    private int yDelta;
    private int xTemp;

    private FirebaseFirestore firebaseFirestoreReference;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_orders);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver1,
                new IntentFilter("waitingOrder-object"));

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver2,
                new IntentFilter("preparingOrder-object"));

        initViews();
        startAnimation();

        firebaseFirestoreReference.collection("Orders").addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(PrepareOrdersActivity.this, "sdfsdfsdf", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (documentChange.getType()) {
                                case ADDED:
                                    initWaitingOrdersRecyclerView();
                                    initPreparingOrdersRecyclerView();
                                    //Toast.makeText(PrepareOrdersActivity.this, "ADDED", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(PrepareOrdersActivity.this, documentChange.getDocument().getString("dada"), Toast.LENGTH_SHORT).show();
                                    break;
                                case MODIFIED:
                                    initWaitingOrdersRecyclerView();
                                    initPreparingOrdersRecyclerView();
                                    //Toast.makeText(PrepareOrdersActivity.this, "Modofied", Toast.LENGTH_SHORT).show();
                                    break;
                                case REMOVED:
                                    initWaitingOrdersRecyclerView();
                                    initPreparingOrdersRecyclerView();
                                    //Toast.makeText(PrepareOrdersActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                }
        );
    }

    private void initViews() {
        firebaseFirestoreReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseFirestoreReference, PrepareOrdersActivity.this);

        relativeLayout = findViewById(R.id.ordersInPreparationRelativeLayout);
        imageView = findViewById(R.id.clockAnimation);
        waitingOrdersRecyclerView = findViewById(R.id.recyclerViewWaitingOrders);
        preparingOrdersRecyclerView = findViewById(R.id.recyclerViewOrdersInPreparation);
    }

    private void initWaitingOrdersRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PrepareOrdersActivity.this);
        waitingOrdersRecyclerView.setLayoutManager(layoutManager);

        firebaseDatabaseHelper.getOrdersData("orderStatus", "waiting",
                new SimpleCallback<List<Order>>() {
                    @Override
                    public void callback(List<Order> waitingOrders) {
                        WaitingOrdersAdapter waitingOrdersAdapter = new WaitingOrdersAdapter(waitingOrders, PrepareOrdersActivity.this);
                        waitingOrdersRecyclerView.setAdapter(waitingOrdersAdapter);
                    }
                });
    }

    private void initPreparingOrdersRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PrepareOrdersActivity.this);
        preparingOrdersRecyclerView.setLayoutManager(layoutManager);

        firebaseDatabaseHelper.getOrdersData("orderStatus", "preparing",
                new SimpleCallback<List<Order>>() {
                    @Override
                    public void callback(List<Order> preparingOrders) {
                        PreparingOrdersAdapter preparingOrdersAdapter = new PreparingOrdersAdapter(preparingOrders, PrepareOrdersActivity.this);
                        preparingOrdersRecyclerView.setAdapter(preparingOrdersAdapter);
                    }
                });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        Drawable d = imageView.getDrawable();

        if (hasFocus) {
            ((Animatable) d).start();
        } else {
            ((Animatable) d).stop();
        }
    }

    public BroadcastReceiver messageReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = (Order) intent.getSerializableExtra("waitingOrder");

            if(!openedOrders.contains(order.getOrderId())){
                createOrderLayout(order);
            }else{
                Toast.makeText(context, "Comanda este deja deschisa", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public BroadcastReceiver messageReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Order order = (Order) intent.getSerializableExtra("preparingOrder");

            if(!openedOrders.contains(order.getOrderId())){
                createOrderLayout(order);
            }else{
                Toast.makeText(context, "Comanda este deja deschisa", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private void createOrderLayout(Order order) {
        openedOrders.add(order.getOrderId());

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        constraintLayout.setId(0);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(400, 525);
        if (xTemp < 450) {
            params2.setMargins(450, 0, 0, 0);
        }
        constraintLayout.setLayoutParams(params2);
        constraintLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.thick_border));

        this.relativeLayout.addView(constraintLayout);

        List<TextView> textViewList = new ArrayList<>();
        List<TextView> textViewOrderList = new ArrayList<>();

        TextView orderName = new TextView(this);
        orderName.setText(order.getOrderName());
        textViewList.add(orderName);

        TextView hallName = new TextView(this);
        hallName.setText(getResources().getString(R.string.hall_name_label));
        textViewList.add(hallName);

        TextView waiterName = new TextView(this);
        waiterName.setText(getResources().getString(R.string.waiter_name_label));
        textViewList.add(waiterName);

        TextView orderTime = new TextView(this);
        orderTime.setText(getResources().getString(R.string.order_time_label));
        textViewList.add(orderTime);

        TextView orderedDishes = new TextView(this);
        orderedDishes.setText(getResources().getString(R.string.ordered_dishes_label));
        textViewList.add(orderedDishes);

        TextView hallNameText = new TextView(this);
        hallNameText.setText(order.getHallName());
        textViewOrderList.add(hallNameText);

        TextView waiterNameText = new TextView(this);
        String[] name = order.getWaiterName().split("[\\s-]+");
        String shortWaiterName = name[0] + " " + name[1].substring(0, 1) + ".";
        waiterNameText.setText(shortWaiterName);
        textViewOrderList.add(waiterNameText);

        TextView orderTimeText = new TextView(this);
        String time = order.getOrderTime().substring(order.getOrderTime().length() - 8);
        orderTimeText.setText(time);
        textViewOrderList.add(orderTimeText);

        TextView orderedDishesText = new TextView(this);
        orderedDishesText.setMovementMethod(ScrollingMovementMethod.getInstance());
        orderedDishesText.setScrollBarStyle(0x03000000);
        orderedDishesText.setVerticalScrollBarEnabled(true);
        List<Dish> dishes = new ArrayList<>(order.getOrderedDishes());
        StringBuilder dishesString = new StringBuilder();

        for (int i = 0; i < dishes.size(); i++) {
            if (i == dishes.size() - 1) {
                dishesString.append("- ").append(dishes.get(i).getDishName()).append(" X ").append(dishes.get(i).getDishQuantity());
            } else {
                dishesString.append("- ").append(dishes.get(i).getDishName()).append(" X ").append(dishes.get(i).getDishQuantity()).append("\n");
            }
        }
        orderedDishesText.setText(dishesString.toString());
        textViewOrderList.add(orderedDishesText);

        textViewList.get(0).setId(1);
        textViewList.get(0).setLayoutParams(params);
        textViewList.get(0).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewList.get(0).setTextColor(Color.WHITE);
        textViewList.get(0).setTypeface(Typeface.create(String.valueOf(R.string.font_family_1), Typeface.NORMAL));
        textViewList.get(0).setMaxLines(1);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(textViewList.get(0), TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

        constraintLayout.addView(textViewList.get(0));

        View view = new View(this);
        view.setId(7);
        ViewGroup.LayoutParams params3 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
        view.setLayoutParams(params3);
        view.setBackgroundColor(Color.WHITE);
        constraintLayout.addView(view);

        for (int i = 1; i < textViewList.size(); i++) {
            //int[] list={16};
            //TextViewCompat.setAutoSizeTextTypeUniformWithPresetSizes(textViewList.get(i), list, TypedValue.COMPLEX_UNIT_SP);
            textViewList.get(i).setId(i + 1);
            textViewList.get(i).setLayoutParams(params);
            textViewList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textViewList.get(i).setTextColor(Color.WHITE);
            textViewList.get(i).setTypeface(Typeface.create(String.valueOf(R.string.font_family_1), Typeface.NORMAL));
            //textViewList.get(i).setMaxLines(1);
            //TextViewCompat.setAutoSizeTextTypeWithDefaults(textViewList.get(i), TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

            constraintLayout.addView(textViewList.get(i));
        }

        for (int i = 0; i < textViewOrderList.size(); i++) {
            //textViewOrderList.get(i).setMaxLines(1);
            //int[] list={16};
            //TextViewCompat.setAutoSizeTextTypeUniformWithPresetSizes(textViewOrderList.get(i), list, TypedValue.COMPLEX_UNIT_SP);
            //textViewOrderList.get(i).setMaxWidth(100);
            if (i == textViewOrderList.size() - 1) {
                ViewGroup.LayoutParams params4 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 190);
                textViewOrderList.get(i).setLayoutParams(params4);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(textViewOrderList.get(i), TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            } else {
                textViewOrderList.get(i).setLayoutParams(params);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(textViewOrderList.get(i), TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            textViewOrderList.get(i).setId(i + 8);
            textViewOrderList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textViewOrderList.get(i).setTextColor(Color.WHITE);
            textViewOrderList.get(i).setTypeface(Typeface.create(String.valueOf(R.string.font_family_1), Typeface.NORMAL));

            constraintLayout.addView(textViewOrderList.get(i));
        }

        Button postponeOrderButton = new Button(this);
        postponeOrderButton.setId(12);
        ViewGroup.LayoutParams params4 = new ViewGroup.LayoutParams(150, 60);
        postponeOrderButton.setLayoutParams(params4);
        postponeOrderButton.setText(R.string.postpone_button_text);
        postponeOrderButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        postponeOrderButton.setPadding(4, 2, 4, 2);
        postponeOrderButton.setTextColor(Color.WHITE);
        postponeOrderButton.setTypeface(Typeface.create(String.valueOf(R.string.font_family_1), Typeface.BOLD));
        postponeOrderButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_hall));

        constraintLayout.addView(postponeOrderButton);

        Button prepareOrderButton = new Button(this);
        prepareOrderButton.setId(13);
        prepareOrderButton.setLayoutParams(params4);
        prepareOrderButton.setText(R.string.prepare_button_text);
        prepareOrderButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        prepareOrderButton.setPadding(4, 2, 4, 2);
        prepareOrderButton.setTextColor(Color.WHITE);
        prepareOrderButton.setTypeface(Typeface.create(String.valueOf(R.string.font_family_1), Typeface.BOLD));
        prepareOrderButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_hall));
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(prepareOrderButton, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

        constraintLayout.addView(prepareOrderButton);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        for (int i = 0; i < textViewList.size(); i++) {
            if (i == 0) {
                constraintSet.connect(1, ConstraintSet.TOP, 0, ConstraintSet.TOP, 0);
                constraintSet.connect(1, ConstraintSet.LEFT, 0, ConstraintSet.LEFT, 4);
                //constraintSet.connect(1, ConstraintSet.RIGHT, 0, ConstraintSet.RIGHT, 0);

                constraintSet.connect(7, ConstraintSet.TOP, 1, ConstraintSet.BOTTOM, 0);
                //constraintSet.connect(1, ConstraintSet.RIGHT,0, ConstraintSet.RIGHT, 0);
            } else if (i == 1) {
                constraintSet.connect(textViewList.get(i).getId(), ConstraintSet.TOP, 7, ConstraintSet.BOTTOM, 4);
                constraintSet.connect(textViewList.get(i).getId(), ConstraintSet.LEFT, 0, ConstraintSet.LEFT, 4);
            } else {
                constraintSet.connect(textViewList.get(i).getId(), ConstraintSet.TOP, textViewList.get(i - 1).getId(), ConstraintSet.BOTTOM, 2);
                constraintSet.connect(textViewList.get(i).getId(), ConstraintSet.LEFT, 0, ConstraintSet.LEFT, 4);
                //constraintSet.connect(textViewList.get(i).getId(), ConstraintSet.RIGHT,0, ConstraintSet.RIGHT, 0);
            }
        }

        for (int i = 0; i < textViewOrderList.size(); i++) {
            //constraintSet.constrainDefaultWidth(textViewOrderList.get(i).getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);
          /*  ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textViewOrderList.get(i).getLayoutParams();
            layoutParams.constrainedWidth = true;

            textViewOrderList.get(i).setLayoutParams(layoutParams);*/
            if (i == 0) {
                constraintSet.connect(8, ConstraintSet.TOP, 7, ConstraintSet.BOTTOM, 4);
                constraintSet.connect(8, ConstraintSet.RIGHT, 0, ConstraintSet.RIGHT, 4);
                //constraintSet.connect(8, ConstraintSet.LEFT, 1, ConstraintSet.RIGHT, 4);
            } else if (i == textViewOrderList.size() - 1) {
                constraintSet.connect(textViewOrderList.get(i).getId(), ConstraintSet.TOP, 5, ConstraintSet.BOTTOM, 2);
                constraintSet.connect(textViewOrderList.get(i).getId(), ConstraintSet.LEFT, 0, ConstraintSet.LEFT, 8);
            } else {
                constraintSet.connect(textViewOrderList.get(i).getId(), ConstraintSet.TOP, textViewOrderList.get(i - 1).getId(), ConstraintSet.BOTTOM, 2);
                constraintSet.connect(textViewOrderList.get(i).getId(), ConstraintSet.RIGHT, 0, ConstraintSet.RIGHT, 4);
                //constraintSet.connect(textViewOrderList.get(i).getId(), ConstraintSet.LEFT, i + 7, ConstraintSet.RIGHT, 4);
            }
        }

        constraintSet.connect(12, ConstraintSet.BOTTOM, 0, ConstraintSet.BOTTOM, 6);
        constraintSet.connect(12, ConstraintSet.LEFT, 0, ConstraintSet.LEFT, 8);

        constraintSet.connect(13, ConstraintSet.BOTTOM, 0, ConstraintSet.BOTTOM, 6);
        constraintSet.connect(13, ConstraintSet.RIGHT, 0, ConstraintSet.RIGHT, 8);

        constraintSet.applyTo(constraintLayout);

        setPostponeButtonClickListener(postponeOrderButton,order);
        setPrepareButtonClickListener(prepareOrderButton, order);

        constraintLayout.setOnTouchListener(this);
        xTemp = 450;

    }

    private void setPostponeButtonClickListener(final Button button,final Order order) {
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConstraintLayout constraintLayout = (ConstraintLayout) button.getParent();
                        constraintLayout.setVisibility(View.GONE);
                        openedOrders.remove(order.getOrderId());
                    }
                }
        );
    }

    private void setPrepareButtonClickListener(final Button button, final Order order) {
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConstraintLayout constraintLayout = (ConstraintLayout) button.getParent();
                        constraintLayout.setVisibility(View.GONE);
                        firebaseDatabaseHelper.simpleUpdateField("Orders", order.getOrderId(), "orderStatus", "preparing");
                        openedOrders.remove(order.getOrderId());
                        Toast.makeText(PrepareOrdersActivity.this, "Comanda este in preparare", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                xDelta = X - lParams.leftMargin;
                yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X - xDelta;
                layoutParams.topMargin = Y - yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                xTemp = X;
                break;
        }
        relativeLayout.invalidate();
        return true;
    }

    private void startAnimation() {
        ConstraintLayout constraintLayout = findViewById(R.id.preparingOrdersLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
}
