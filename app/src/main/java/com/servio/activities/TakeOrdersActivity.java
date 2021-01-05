package com.servio.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.servio.R;
import com.servio.fragments.MenuFragment;
import com.servio.fragments.OrderFragment;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Order;
import com.servio.models.Table;
import com.servio.recyclerviews.activeOrders.ActiveOrdersAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class TakeOrdersActivity extends AppCompatActivity implements SimpleCallback {
    private ImageButton helpImageButton;
    private TextView hallNameTextView;
    private TextView noHallsTextView;

    private ConstraintLayout activeOrdersConstraintLayout;
    private RecyclerView recyclerViewActiveOrders;
    private Button swapHallButton;

    private LinearLayout selectHallLinearLayout;
    private RadioGroup selectHallRadioGroup;
    private Button confirmSelectedHallButton;

    private String hallName;

    private ConstraintLayout optionsLayout;
    private RelativeLayout hallRelativeLayout;
    private LinearLayout phoneDisplay;
    private LinearLayout fragmentsLayout;
    private ProgressBar progressBar;

    private int numberOfHeightCells;
    private int numberOfWidthCells;
    private int cellWidth;

    private int tableNumberTemp;

    private FirebaseFirestore firebaseReference;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_orders);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        checkExistingHalls();
        initClickListeners();

        getSupportFragmentManager().beginTransaction().add(R.id.menuFragment, new MenuFragment()).add(R.id.orderFragment
                , new OrderFragment()).commit();
    }

    private void initViews() {
        firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, TakeOrdersActivity.this);

        helpImageButton = findViewById(R.id.helpImageButton);
        hallNameTextView = findViewById(R.id.hallNameTextView);
        noHallsTextView = findViewById(R.id.noHalltextView);

        activeOrdersConstraintLayout = findViewById(R.id.activeOrdersConstraintLayout);
        recyclerViewActiveOrders = findViewById(R.id.recyclerViewActiveOrders);
        swapHallButton = findViewById(R.id.swapHallButton);

        selectHallLinearLayout = findViewById(R.id.selectHallLinearLayout);
        selectHallRadioGroup = findViewById(R.id.selectHallRadioGroup);
        confirmSelectedHallButton = findViewById(R.id.confirmSelectedHallButton);

        optionsLayout = findViewById(R.id.optionsLayout);
        hallRelativeLayout = findViewById(R.id.hallRelativeLayout);
        phoneDisplay = findViewById(R.id.display);
        fragmentsLayout = findViewById(R.id.fragmentsLinearLayout);
        progressBar = findViewById(R.id.progressBar);
    }

    private void checkExistingHalls() {
        firebaseDatabaseHelper.checkEmptyCollection("Halls", new SimpleCallback<Boolean>() {
            @Override
            public void callback(Boolean check) {
                if (check) {
                    selectHallLinearLayout.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.GONE);
                    noHallsTextView.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    noHallsTextView.setVisibility(View.INVISIBLE);
                    addHallOptions();
                    selectHallLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initClickListeners() {
        confirmSelectedHallButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectHallRadioGroup.getCheckedRadioButtonId() != -1) {
                            progressBar.setVisibility(View.VISIBLE);
                            int radioId = selectHallRadioGroup.getCheckedRadioButtonId();
                            RadioButton radioButton = findViewById(radioId);
                            hallName = (String) radioButton.getText();
                            helpImageButton.setVisibility(View.INVISIBLE);
                            initRecyclerView(hallName);
                            setRecyclerViewListener(hallName);
                            activeOrdersConstraintLayout.setVisibility(View.VISIBLE);
                            hallNameTextView.setText(hallName);
                            selectHallLinearLayout.setVisibility(View.INVISIBLE);
                            hallRelativeLayout.setVisibility(View.VISIBLE);
                            String documentPath = "hall" + radioButton.getText();
                            documentPath = documentPath.replaceAll("\\s+", "");

                            firebaseDatabaseHelper.getSingleDataFieldValue("Halls", documentPath, "hallSize", new SimpleCallback<String>() {
                                @Override
                                public void callback(String fieldValue) {
                                    String[] fieldValueArray = fieldValue.split(" ");
                                    String hallSize = fieldValueArray[1].substring(0, fieldValueArray[1].length() - 1);

                                    setHallSize(hallSize);
                                    reloadHall();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(TakeOrdersActivity.this, "Selectati sala de afisat din lista de mai sus", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        swapHallButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseDatabaseHelper.checkEmptyCollection("Halls", new SimpleCallback<Boolean>() {
                            @Override
                            public void callback(Boolean check) {
                                if (check) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(TakeOrdersActivity.this, "Nu exista sali de afisat", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (selectHallLinearLayout.getVisibility() == View.INVISIBLE) {
                                        hallNameTextView.setText("");
                                        helpImageButton.setVisibility(View.VISIBLE);
                                        deleteConfiguration();
                                        activeOrdersConstraintLayout.setVisibility(View.GONE);
                                        selectHallRadioGroup.removeAllViews();
                                        addHallOptions();
                                        selectHallLinearLayout.setVisibility(View.VISIBLE);
                                        selectHallRadioGroup.clearCheck();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(TakeOrdersActivity.this, "Selectati sala de afisat din lista de mai sus", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
        );
    }

    private void initRecyclerView(String hallName) {
        final Context context = this;
        firebaseDatabaseHelper.getOrdersData("hallName",hallName,
                new SimpleCallback<List<Order>>() {
                    @Override
                    public void callback(List<Order> activeOrders) {
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerViewActiveOrders.setLayoutManager(layoutManager);

                        ActiveOrdersAdapter activeOrdersAdapter = new ActiveOrdersAdapter(activeOrders);
                        recyclerViewActiveOrders.setAdapter(activeOrdersAdapter);
                        //activeOrdersAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    private void setRecyclerViewListener(final String hallName) {
        firebaseReference.collection("Orders").whereEqualTo("hallName", hallName).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(TakeOrdersActivity.this, "sdfsdfsdf", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (documentChange.getType()) {
                                case ADDED:
                                    Toast.makeText(TakeOrdersActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                    initRecyclerView(hallName);
                                    //Toast.makeText(TakeOrdersActivity.this, documentChange.getDocument().getString("dada"), Toast.LENGTH_SHORT).show();
                                    break;
                                case MODIFIED:
                                    //Toast.makeText(TakeOrdersActivity.this, "Modofied", Toast.LENGTH_SHORT).show();
                                    initRecyclerView(hallName);
                                    break;
                                case REMOVED:
                                    //Toast.makeText(TakeOrdersActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                    initRecyclerView(hallName);
                                    break;
                            }
                        }
                    }
                }
        );
    }

    private void deleteConfiguration() {
        int i = 0, j = hallRelativeLayout.getChildCount();
        while (i < j) {
            String tag = (String) hallRelativeLayout.getChildAt(i).getTag();
            if (tag.contains("cell")) {
                hallRelativeLayout.removeView(hallRelativeLayout.getChildAt(i));
                j--;
            } else {
                i++;
            }
        }
    }

    private void addHallOptions() {
        firebaseDatabaseHelper.getDataFieldsValues("Halls", "hallName", new SimpleCallback<List<String>>() {
            @Override
            public void callback(List<String> fieldsValues) {
                for (int i = 0; i < fieldsValues.size(); i++) {
                    RadioButton radioButton = new RadioButton(getApplicationContext());
                    radioButton.setText(fieldsValues.get(i));
                    radioButton.setTextColor(Color.WHITE);
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 8, 0, 0);
                    radioButton.setLayoutParams(params);
                    selectHallRadioGroup.addView(radioButton);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setHallSize(String hallSize) {
        if (hallSize.equals("mica")) {
            numberOfHeightCells = 6;
            numberOfWidthCells = 8;
            cellWidth = phoneDisplay.getHeight() / numberOfHeightCells;
        } else if (hallSize.equals("medie")) {
            numberOfHeightCells = 9;
            numberOfWidthCells = 12;
            cellWidth = phoneDisplay.getHeight() / numberOfHeightCells;
        } else {
            numberOfHeightCells = 12;
            numberOfWidthCells = 16;
            cellWidth = phoneDisplay.getHeight() / numberOfHeightCells;
        }
    }

    private void showPopup(View v) {
        final Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, v);

        setForceShowIcon(popupMenu);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_table_options_waiter, popupMenu.getMenu());
        //popupMenu.show();

        MenuItem item = popupMenu.getMenu().findItem(R.id.tableNumber);
        setDialogTitle(item, v, popupMenu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addOrder:
                        optionsLayout.setVisibility(View.GONE);
                        hallRelativeLayout.setVisibility(View.GONE);
                        fragmentsLayout.setVisibility(View.VISIBLE);

                        OrderFragment orderFragment = new OrderFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("tableNumber", tableNumberTemp);
                        bundle.putString("hallName", hallName);
                        orderFragment.setArguments(bundle);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.orderFragment, orderFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    case R.id.cancelOrder:
                        Toast.makeText(wrapper, "cancel", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.editOrder:
                        Toast.makeText(wrapper, "edit", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setDialogTitle(final MenuItem item, View view, final PopupMenu popupMenu) {
        final String hallNameAux = hallName.replaceAll("\\s+", "");

        LinearLayout parent = (LinearLayout) view.getParent();

        firebaseDatabaseHelper.getSingleLongFieldValueWithCondition("Cells" + hallNameAux, "tableNumber", "cellId", String.valueOf(parent.getTag()),
                new SimpleCallback<Long>() {
                    @Override
                    public void callback(final Long tableNumber) {
                        String documentPath = hallNameAux + tableNumber;
                        firebaseDatabaseHelper.getDocument("Tables", documentPath, Table.class, new SimpleCallback<Table>() {
                            @Override
                            public void callback(Table table) {
                                tableNumberTemp = table.getTableNumber();

                                item.setTitle("Masa nr. " + tableNumberTemp);

                                popupMenu.show();
                            }
                        });
                    }
                });
    }

    private void setTableAvailability(long tableNumber, final LinearLayout cell, String hallName) {
        firebaseDatabaseHelper.getSingleDataFieldValue("Tables", hallName + tableNumber, "tableAvailability",
                new SimpleCallback<Boolean>() {
                    @Override
                    public void callback(Boolean data) {
                        if (data) {
                            cell.setBackground(ContextCompat.getDrawable(TakeOrdersActivity.this, R.drawable.green_border));
                        } else {
                            cell.setBackground(ContextCompat.getDrawable(TakeOrdersActivity.this, R.drawable.red_border));
                        }

                    }
                });
    }

    private void reloadHall() {
        final String hallNameAux = hallName.replaceAll("\\s+", "");
        String documentPath = "hall" + hallName;
        documentPath = documentPath.replaceAll("\\s+", "");
        firebaseDatabaseHelper.getSingleDataFieldValue("Halls", documentPath, "cellCollectionName", new SimpleCallback<String>() {
                    @Override
                    public void callback(String cellCollectionName) {
                        for (int i = 0; i < numberOfHeightCells; i++) {
                            for (int j = 0; j < numberOfWidthCells; j++) {
                                final LinearLayout cell = new LinearLayout(TakeOrdersActivity.this);
                                cell.setOrientation(LinearLayout.VERTICAL);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellWidth, cellWidth);
                                params.setMargins(j * cellWidth, i * cellWidth, 0, 0);
                                cell.setLayoutParams(params);
                                cell.setBackground(ContextCompat.getDrawable(TakeOrdersActivity.this, R.drawable.simple_border));
                                cell.setTag("cell_" + i + "_" + j);

                                DocumentReference dr = firebaseReference.collection(cellCollectionName)
                                        .document("cell_" + i + "_" + j);

                                dr.get().addOnCompleteListener(
                                        new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                String imageName;

                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                    if (Objects.requireNonNull(documentSnapshot).exists()) {
                                                        imageName = String.valueOf(documentSnapshot.get("content"));

                                                        ImageView imageView = new ImageView(TakeOrdersActivity.this);
                                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cellWidth, cellWidth);
                                                        imageView.setLayoutParams(layoutParams);

                                                        switch (imageName) {
                                                            case "tableImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_masa);
                                                                imageView.setScaleX((float) 0.75);
                                                                imageView.setScaleY((float) 0.75);
                                                                //imageView.setTag("tableImageView");

                                                                Long tableNumber = (Long) documentSnapshot.get("tableNumber");
                                                                setTableAvailability(tableNumber, cell, hallNameAux);
                                                                /*if((Boolean) documentSnapshot.get("tableAvailability")){
                                                                    cell.setBackground(ContextCompat.getDrawable(TakeOrdersActivity.this, R.drawable.simple_border));
                                                                }else{
                                                                    cell.setBackground(ContextCompat.getDrawable(TakeOrdersActivity.this, R.drawable.red_border));
                                                                }*/

                                                                imageView.setOnClickListener(
                                                                        new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                showPopup(v);
                                                                            }
                                                                        }
                                                                );

                                                                cell.addView(imageView);
                                                                break;

                                                            case "barImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_bar);
                                                                //imageView.setTag("barImageView");
                                                                cell.addView(imageView);
                                                                break;

                                                            case "kitchenImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_bucatarie);
                                                                //imageView.setTag("kitchenImageView");
                                                                imageView.setScaleX((float) 1.3);
                                                                imageView.setScaleY((float) 1.3);
                                                                cell.addView(imageView);
                                                                break;

                                                            case "perete":
                                                            case "perete_2":
                                                            case "perete_3":
                                                            case "perete_4":
                                                            case "perete_5":
                                                            case "perete_6":
                                                            case "perete_7":
                                                            case "perete_8":
                                                            case "perete_9":
                                                            case "perete_10":
                                                            case "perete_11":
                                                                Resources res = getResources();
                                                                int resId = res.getIdentifier("ic_launcher_foreground_" + imageName, "drawable", getApplicationContext().getPackageName());
                                                                imageView.setImageResource(resId);
                                                                //imageView.setTag(imageName);
                                                                cell.addView(imageView);
                                                                break;

                                                            case "toiletImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_toaleta);
                                                                //imageView.setTag("toiletImageView");
                                                                cell.addView(imageView);
                                                                break;

                                                            default:
                                                                break;
                                                        }
                                                    } else {
                                                        Toast.makeText(TakeOrdersActivity.this, "Eroare", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(TakeOrdersActivity.this, "Eroare", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                );
                                hallRelativeLayout.addView(cell);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (fragmentsLayout.getVisibility() == View.VISIBLE) {
            android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(TakeOrdersActivity.this);
            alertDialog.setTitle("Confirmare anulare comanda");
            alertDialog.setMessage("Doriti sa anulati comanda curenta?");

            alertDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fragmentsLayout.setVisibility(View.GONE);
                    optionsLayout.setVisibility(View.VISIBLE);
                    hallRelativeLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(TakeOrdersActivity.this, "Comanda a fost anulata", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog.show();

        } else {
            finish();
            startActivity(new Intent(TakeOrdersActivity.this, WaiterUiActivity.class));
        }
    }

    @Override
    public void callback(Object data) {
        Toast.makeText(this, String.valueOf(data), Toast.LENGTH_SHORT).show();
    }
}
