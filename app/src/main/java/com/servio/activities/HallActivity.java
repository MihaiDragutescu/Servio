package com.servio.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Clasa corespunzatoare activitatii care va permite modificarea planificarii restaurantului.
 */
public class HallActivity extends AppCompatActivity {
    private ImageView tableImageView;
    private ImageView kitchenImageView;
    private ImageView toiletImageView;
    private ImageView barImageView;
    private ImageView wallImageView;
    private ImageView wallImageView2;

    private ImageButton helpImageButton;
    private TextView hallNameTextView;
    private TextView noHallsTextView;

    private LinearLayout selectHallLinearLayout;
    private RadioGroup selectHallRadioGroup;
    private Button confirmSelectedHallButton;

    private String hallName;
    private String hallSizeSelection;

    private RelativeLayout hallRelativeLayout;
    private LinearLayout phoneDisplay;

    private Button addHallButton;
    private Button deleteHallButton;
    private Button swapHallButton;
    private ProgressBar progressBar;

    private String imageId;
    public int selectedIndex;

    private int numberOfHeightCells;
    private int numberOfWidthCells;
    private LinearLayout cell;
    private int cellWidth;
    private String cellId;
    private View tempView;

    private int tableNumberTemp;
    private int tableNumberOfSeatsTemp;

    private FirebaseFirestore firebaseFirestoreReference;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        checkExistingHalls();
        initClickListeners();
    }

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private void initViews() {
        FirebaseApp.initializeApp(this); //in order to have Firebase initialize into entire application, not just one Activity.
        firebaseFirestoreReference = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestoreReference.setFirestoreSettings(settings);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseFirestoreReference, HallActivity.this);

        helpImageButton = findViewById(R.id.helpImageButton);
        hallNameTextView = findViewById(R.id.hallNameTextView);
        noHallsTextView = findViewById(R.id.noHalltextView);

        selectHallLinearLayout = findViewById(R.id.selectHallLinearLayout);
        selectHallRadioGroup = findViewById(R.id.selectHallRadioGroup);
        confirmSelectedHallButton = findViewById(R.id.confirmSelectedHallButton);
        progressBar = findViewById(R.id.progressBar);

        phoneDisplay = findViewById(R.id.display);
        hallRelativeLayout = findViewById(R.id.hallRelativeLayout);

        tableImageView = findViewById(R.id.tableImageView);
        kitchenImageView = findViewById(R.id.kitchenImageView);
        toiletImageView = findViewById(R.id.toiletImageView);
        barImageView = findViewById(R.id.barImageView);
        wallImageView = findViewById(R.id.wallImageView);
        wallImageView2 = findViewById(R.id.wallImageView2);

        addHallButton = findViewById(R.id.addHallButton);
        deleteHallButton = findViewById(R.id.deleteHallButton);
        swapHallButton = findViewById(R.id.swapHallButton);
    }

    public void imageClicked(View view) {
        Toast.makeText(this, "Deschideti sau creati o sala pentru a putea adauga elementele din meniu", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void enableImagesOnTouchListener() {
        tableImageView.setOnTouchListener(new MyTouchListener());
        kitchenImageView.setOnTouchListener(new MyTouchListener());
        toiletImageView.setOnTouchListener(new MyTouchListener());
        barImageView.setOnTouchListener(new MyTouchListener());
        wallImageView.setOnTouchListener(new MyTouchListener());
        wallImageView2.setOnTouchListener(new MyTouchListener());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void disableImagesOnTouchListener() {
        tableImageView.setOnTouchListener(dummyOnTouchListener);
        kitchenImageView.setOnTouchListener(dummyOnTouchListener);
        toiletImageView.setOnTouchListener(dummyOnTouchListener);
        barImageView.setOnTouchListener(dummyOnTouchListener);
        wallImageView.setOnTouchListener(dummyOnTouchListener);
        wallImageView2.setOnTouchListener(dummyOnTouchListener);
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
        helpImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(HallActivity.this).create();
                        alertDialog.setTitle("Ajutor");
                        alertDialog.setMessage("În fereastra 'Administrează Sălile' puteți efectua acțiuni precum:" + "\n\n" +
                                " - 'Adaugă sală': creează o sală nouă cu dimensiunea specificată în care veți putea adăuga diverse elemente (masă, bucătarie, baie, bar, perete orizontal sau vertical) prin tragerea din meniul din partea stangă;" + "\n\n" +
                                " - 'Șterge sala': elimină sala curentă din listă;" + "\n\n" +
                                " - 'Schimbă sala': permite redeschiderea unei săli pentru a se face modificări;"
                        );
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
        );

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
                            hallNameTextView.setText(hallName);
                            selectHallLinearLayout.setVisibility(View.INVISIBLE);
                            hallRelativeLayout.setVisibility(View.VISIBLE);
                            enableImagesOnTouchListener();
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
                            Toast.makeText(HallActivity.this, "Selectati sala de afisat din lista de mai sus", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        addHallButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getHallSize();
                    }
                }
        );

        deleteHallButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (noHallsTextView.getVisibility() == View.VISIBLE) {
                            Toast.makeText(HallActivity.this, "Nu exista sali de sters", Toast.LENGTH_SHORT).show();
                        } else if (selectHallLinearLayout.getVisibility() == View.VISIBLE) {
                            Toast.makeText(HallActivity.this, "Accesati sala pe care doriti sa o stergeti", Toast.LENGTH_SHORT).show();
                        } else {
                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(HallActivity.this);
                            alertDialog.setTitle("Confirmare stergere sala");
                            alertDialog.setMessage("Doriti sa stergeti sala " + hallName + " ?");

                            alertDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    disableImagesOnTouchListener();
                                    progressBar.setVisibility(View.VISIBLE);
                                    deleteData();
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
                                    Toast.makeText(HallActivity.this, "Nu exista sali de afisat", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (selectHallLinearLayout.getVisibility() == View.INVISIBLE) {
                                        hallNameTextView.setText("");
                                        helpImageButton.setVisibility(View.VISIBLE);
                                        deleteConfiguration();
                                        selectHallRadioGroup.removeAllViews();
                                        addHallOptions();
                                        selectHallLinearLayout.setVisibility(View.VISIBLE);
                                        disableImagesOnTouchListener();
                                        selectHallRadioGroup.clearCheck();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(HallActivity.this, "Selectati sala de afisat din lista de mai sus", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
        );
    }

    private void getHallSize() {
        selectedIndex = -1;
        final String[] hallSizes = getApplicationContext().getResources().getStringArray(R.array.hall_sizes);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selectati tipul salii");
        builder.setSingleChoiceItems(R.array.hall_sizes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hallSizeSelection = hallSizes[which];
                selectedIndex = which;
            }
        });

        builder.setPositiveButton("Confirma", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedIndex != -1) {
                    getHallName();
                } else {
                    Toast.makeText(HallActivity.this, "Nu ati selectat dimensiunea salii", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void getHallName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduceti numele salii");
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Confirma", new DialogInterface.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(HallActivity.this, "Campul cu numele salii trebuie completat", Toast.LENGTH_SHORT).show();
                } else if (input.getText().toString().length() < 5) {
                    Toast.makeText(HallActivity.this, "Numele salii trebuie sa fie format din minim 6 caractere", Toast.LENGTH_SHORT).show();
                } else {
                    String hallNameAux = input.getText().toString().replaceAll("\\s+", "").toLowerCase();

                    firebaseDatabaseHelper.checkIfDocumentExists("Halls", "hall" + hallNameAux,
                            new SimpleCallback<Boolean>() {
                                @Override
                                public void callback(Boolean check) {
                                    if (!check) {
                                        enableImagesOnTouchListener();
                                        progressBar.setVisibility(View.VISIBLE);
                                        String hallSize;
                                        if (noHallsTextView.getVisibility() == View.VISIBLE) {
                                            noHallsTextView.setVisibility(View.INVISIBLE);
                                        } else {
                                            selectHallLinearLayout.setVisibility(View.INVISIBLE);
                                        }

                                        if (hallRelativeLayout.getChildCount() > 2) {
                                            deleteConfiguration();
                                        }

                                        String[] hallSizeSelectionArray = hallSizeSelection.split("[\\s:]+");
                                        hallSize = hallSizeSelectionArray[1];
                                        hallName = input.getText().toString();

                                        setHallSize(hallSize);
                                        buildConfiguration();
                                        addHall();
                                        helpImageButton.setVisibility(View.INVISIBLE);
                                        hallNameTextView.setText(hallName);
                                    } else {
                                        Toast.makeText(HallActivity.this, "Exista deja o sala cu acest nume", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        builder.setCancelable(false);

        builder.show();
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

    /**
     * Se creeaza celulele care sunt adaugate la RelativeLayout-ul mare, pentru a putea fi modificat
     * ulterior continutul lor.
     */
    public void buildConfiguration() {
        for (int i = 0; i < numberOfHeightCells; i++) {
            for (int j = 0; j < numberOfWidthCells; j++) {
                final LinearLayout cellLayout = new LinearLayout(HallActivity.this);
                cellLayout.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellWidth, cellWidth);
                params.setMargins(j * cellWidth, i * cellWidth, 0, 0);
                cellLayout.setLayoutParams(params);
                cellLayout.setBackground(ContextCompat.getDrawable(HallActivity.this, R.drawable.simple_border));
                cellLayout.setOnDragListener(new MyDragListener());
                cellLayout.setTag("cell_" + i + "_" + j);

                hallRelativeLayout.addView(cellLayout);
            }
        }
    }

    /**
     * Se insereaza celulele in baza de date externa (Firebase Firestore).
     * Se trateaza cazurile speciale (prima coloana, prima linie, ultima coloana, ultima linie).
     */
    public void addHall() {
        final String hallNameAux = hallName.replaceAll("\\s+", "");
        Map<String, Object> cell = new HashMap<>();

        for (int i = 0; i < numberOfHeightCells; i++) {
            for (int j = 0; j < numberOfWidthCells; j++) {
                if (i == 0 && j == 0) {
                    cell.put("cellId", "cell_" + i + "_" + j);
                    cell.put("leftNeighbour", null);
                    cell.put("upNeighbour", null);
                    cell.put("content", null);
                    cell.put("tableNumber", 0);

                    firebaseDatabaseHelper.insertData("Cells" + hallNameAux, "cell_" + i + "_" + j, cell);

                } else if (i == 0) {
                    cell.put("cellId", "cell_" + i + "_" + j);
                    cell.put("leftNeighbour", "cell_" + i + "_" + (j - 1));
                    cell.put("upNeighbour", null);
                    cell.put("content", null);
                    cell.put("tableNumber", 0);

                    firebaseDatabaseHelper.insertData("Cells" + hallNameAux, "cell_" + i + "_" + j, cell);

                } else if (j == 0) {
                    cell.put("cellId", "cell_" + i + "_" + j);
                    cell.put("leftNeighbour", null);
                    cell.put("upNeighbour", "cell_" + (i - 1) + "_" + j);
                    cell.put("content", null);
                    cell.put("tableNumber", 0);

                    firebaseDatabaseHelper.insertData("Cells" + hallNameAux, "cell_" + i + "_" + j, cell);

                } else {
                    cell.put("cellId", "cell_" + i + "_" + j);
                    cell.put("leftNeighbour", "cell_" + i + "_" + (j - 1));
                    cell.put("upNeighbour", "cell_" + (i - 1) + "_" + j);
                    cell.put("content", null);
                    cell.put("tableNumber", 0);

                    firebaseDatabaseHelper.insertData("Cells" + hallNameAux, "cell_" + i + "_" + j, cell);
                }
            }
        }

        final Map<String, Object> hall = new HashMap<>();
        hall.put("hallId", "hall" + hallNameAux);
        hall.put("hallName", hallName);
        hall.put("hallSize", hallSizeSelection);
        hall.put("cellCollectionName", "Cells" + hallNameAux);

        firebaseDatabaseHelper.getCollectionDocumentsCount("Halls",
                new SimpleCallback<Integer>() {
                    @Override
                    public void callback(Integer count) {
                        hall.put("hallNumber", count + 1);
                        firebaseDatabaseHelper.insertData("Halls", "hall" + hallNameAux, hall);
                        progressBar.setVisibility(View.GONE);
                    }
                });
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

    public void deleteData() {
        String hallNameAux = hallName.replaceAll("\\s+", "");
        hallNameTextView.setText("");
        helpImageButton.setVisibility(View.VISIBLE);
        firebaseDatabaseHelper.deleteDocumentsWithGreaterThanCondition("Tables", "tableId", hallNameAux);
        firebaseDatabaseHelper.deleteDocument("Halls", "hall" + hallNameAux);
        firebaseDatabaseHelper.deleteCollection("Cells" + hallNameAux);
        firebaseDatabaseHelper.deleteDocumentsWithGreaterThanCondition("Orders", "orderId", hallNameAux);

        deleteConfiguration();
        firebaseDatabaseHelper.checkEmptyCollection("Halls", new SimpleCallback<Boolean>() {
            @Override
            public void callback(Boolean check) {
                if (check) {
                    progressBar.setVisibility(View.GONE);
                    noHallsTextView.setVisibility(View.VISIBLE);
                } else {
                    hallNameTextView.setText("");
                    helpImageButton.setVisibility(View.VISIBLE);
                    selectHallRadioGroup.removeAllViews();
                    addHallOptions();
                    selectHallLinearLayout.setVisibility(View.VISIBLE);
                    selectHallRadioGroup.clearCheck();
                }
            }
        });

        Toast.makeText(this, "Sala a fost stearsa cu succes", Toast.LENGTH_SHORT).show();
    }

    public void deleteElement(final View view) {
        final String hallNameAux = hallName.replaceAll("\\s+", "");

        final AlertDialog.Builder builder = new AlertDialog.Builder(HallActivity.this, R.style.AlertDialog);
        builder.setMessage("Doriti sa stergeti elementul selectat?");
        builder.setCancelable(false);

        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LinearLayout cell = (LinearLayout) view.getParent();
                cell.removeAllViews();

                if (view.getTag().equals("tableImageView")) {
                    firebaseDatabaseHelper.getSingleLongFieldValueWithCondition("Cells" + hallNameAux, "tableNumber", "cellId", (String) cell.getTag(), new SimpleCallback<Long>() {
                        @Override
                        public void callback(Long tableNumber) {
                            firebaseDatabaseHelper.deleteDocument("Tables", hallNameAux + tableNumber);
                            firebaseDatabaseHelper.deleteDocument("Orders", hallNameAux + "Masa" + tableNumber);
                        }
                    });

                    firebaseDatabaseHelper.updateFields("Cells" + hallNameAux,
                            "cellId", (String) cell.getTag(), "tableNumber", 0);
                }

                firebaseDatabaseHelper.updateFields("Cells" + hallNameAux,
                        "cellId", (String) cell.getTag(), "content", null);

            }
        });
        builder.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String getElementId(View v) {
        String path = v.getResources().getResourceName(v.getId());
        String[] split = path.split("/");

        return split[1];
    }

    public final View.OnTouchListener dummyOnTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent rawEvent) {
            return false;
        }
    };

    /**
     * Aceasta clasa detecteaza ”ridicarea” unei imagini (evenimentul de drag).
     * In momentul in care acest eveniment are loc, este salvat id-ul imaginii selectate.
     */
    private final class MyTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);

                case MotionEvent.ACTION_UP:
                    imageId = getElementId(view);
                    break;
            }
            return true;
        }
    }

    /**
     * Prin aceasta clasa este implementat evenimentul de drag and drop pentru imagini.
     * Este implementata interfata OnDragListener, iar in cazul evenimentului onDrop, este
     * updatat continutul celulei unde a fost lasata imaginea.
     */
    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            String hallNameAux = hallName.replaceAll("\\s+", "");

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    break;

                case DragEvent.ACTION_DROP:
                    switch (imageId) {
                        case "tableImageView": {
                            int xLocation = (int) v.getX();
                            int yLocation = (int) v.getY();

                            Intent intent = new Intent(HallActivity.this,
                                    NewTableActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("xLocation", xLocation);
                            bundle.putSerializable("yLocation", yLocation);
                            intent.putExtras(bundle);

                            View view = (View) event.getLocalState();
                            LinearLayout container = (LinearLayout) v;

                            if (container.getChildCount() == 0) {
                                ImageView oldView = (ImageView) view;
                                ImageView newView = new ImageView(getApplicationContext());

                                newView.setImageBitmap(((BitmapDrawable) oldView.getDrawable()).getBitmap());
                                newView.setScaleX((float) 0.75);
                                newView.setScaleY((float) 0.75);

                                cellId = (String) v.getTag();
                                newView.setTag(imageId);
                                tempView = newView;
                                showPopupTableOnClick(newView);
                                container.addView(newView);
                                view.setVisibility(View.VISIBLE);

                                startActivityForResult(intent, 1);
                            } else {
                                Toast.makeText(HallActivity.this, "Nu puteti suprapune doua elemente", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case "wallImageView":
                            cell = (LinearLayout) v;

                            if (cell.getChildCount() == 0) {
                                cellId = (String) v.getTag();

                                startActivityForResult(new Intent(HallActivity.this, NewWallActivity.class), 3);
                            } else {
                                Toast.makeText(HallActivity.this, "Nu puteti suprapune doua elemente", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "wallImageView2":
                            cell = (LinearLayout) v;

                            if (cell.getChildCount() == 0) {
                                cellId = (String) v.getTag();

                                startActivityForResult(new Intent(HallActivity.this, NewWallActivity2.class), 3);
                            } else {
                                Toast.makeText(HallActivity.this, "Nu puteti suprapune doua elemente", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default: {
                            View view = (View) event.getLocalState();
                            LinearLayout container = (LinearLayout) v;
                            ImageView oldView = (ImageView) view;
                            ImageView newView = new ImageView(getApplicationContext());

                            newView.setImageBitmap(((BitmapDrawable) oldView.getDrawable()).getBitmap());
                            cellId = (String) v.getTag();

                            if (imageId.equals("kitchenImageView")) {
                                newView.setScaleX((float) 1.3);
                                newView.setScaleY((float) 1.3);
                            }

                            deleteElementOnClick(newView);

                            newView.setTag(imageId);
                            container.addView(newView);
                            view.setVisibility(View.VISIBLE);

                            final CollectionReference ref = firebaseFirestoreReference.collection("Cells" + hallNameAux);
                            ref.whereEqualTo("cellId", cellId).whereEqualTo("content", null).get().addOnCompleteListener(
                                    new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                                    Map<String, Object> map = new HashMap<>();

                                                    map.put("content", imageId);
                                                    ref.document(documentSnapshot.getId()).set(map, SetOptions.merge());
                                                }
                                            }
                                        }
                                    }
                            ).addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(HallActivity.this, "Eroare", Toast.LENGTH_LONG).show();
                                        }
                                    }
                            );
                            break;
                        }
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void addTable(final int tableNumber, final int tableNumberOfSeats) {
        final String hallNameAux = hallName.replaceAll("\\s+", "");
        String value = hallNameAux + tableNumber;

        firebaseDatabaseHelper.checkIfDocumentExists("Tables", value, new SimpleCallback<Boolean>() {
            @Override
            public void callback(Boolean check) {
                if (!check) {
                    final CollectionReference ref = firebaseFirestoreReference.collection("Cells" + hallNameAux);
                    ref.whereEqualTo("cellId", cellId).whereEqualTo("content", null).get().addOnCompleteListener(
                            new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                            Map<String, Object> map = new HashMap<>();

                                            map.put("tableNumber", tableNumber);
                                            map.put("content", imageId);
                                            ref.document(documentSnapshot.getId()).set(map, SetOptions.merge());
                                        }
                                    }
                                }
                            }
                    ).addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HallActivity.this, "Eroare", Toast.LENGTH_LONG).show();
                                }
                            }
                    );

                    final Map<String, Object> table = new HashMap<>();
                    String tableId = hallNameAux + tableNumber;
                    table.put("tableId", tableId);
                    table.put("tableNumber", tableNumber);
                    table.put("tableNumberOfSeats", tableNumberOfSeats);
                    table.put("tableAvailability", true);

                    firebaseDatabaseHelper.insertData("Tables", tableId, table);

                } else {
                    LinearLayout cell = (LinearLayout) tempView.getParent();
                    cell.removeAllViews();
                    Toast.makeText(HallActivity.this, "Exista deja o masa cu acest numar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTableInfo(int tableNumber, int tableNumberOfSeats) {
        final String hallNameAux = hallName.replaceAll("\\s+", "");
        String collectionPath = "Cells" + hallNameAux;

        firebaseDatabaseHelper.simpleUpdateField(collectionPath, cellId, "tableNumber", tableNumber);
        firebaseDatabaseHelper.deleteDocument("Tables", hallNameAux + tableNumberTemp);

        Map<String, Object> table = new HashMap<>();
        table.put("tableAvailability", true);
        table.put("tableId", hallNameAux + tableNumber);
        table.put("tableNumber", tableNumber);
        table.put("tableNumberOfSeats", tableNumberOfSeats);

        firebaseDatabaseHelper.insertData("Tables", hallNameAux + tableNumber, table);

        Toast.makeText(this, "Informatiile mesei au fost actualizate", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int tableNumber;
        int tableNumberOfSeats;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                tableNumber = data.getIntExtra("tableNumber", 0);
                tableNumberOfSeats = data.getIntExtra("tableNumberOfSeats", 0);
                addTable(tableNumber, tableNumberOfSeats);
            } else {
                LinearLayout cell = (LinearLayout) tempView.getParent();
                cell.removeAllViews();
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                tableNumber = data.getIntExtra("tableNumber", 0);
                tableNumberOfSeats = data.getIntExtra("tableNumberOfSeats", 0);
                updateTableInfo(tableNumber, tableNumberOfSeats);
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String wallType = data.getStringExtra("wallType");
                addWall(wallType);
            }
        }
    }

    private void showPopup(final View v) {
        final Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, v);

        setForceShowIcon(popupMenu);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_table_options_admin, popupMenu.getMenu());

        MenuItem item = popupMenu.getMenu().findItem(R.id.tableNumber);
        setDialogTitle(item, v, popupMenu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tableInfo:
                        showTableInfoDialog(v);
                        break;
                    case R.id.tableEditInfo:
                        editTableInfoDialog(v);
                        break;
                    case R.id.deleteTable:
                        deleteElement(v);
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

    private void showPopupTableOnClick(View view) {
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(v);
                    }
                }
        );
    }

    private void deleteElementOnClick(View view) {
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteElement(v);
                    }
                }
        );
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
                                tableNumberOfSeatsTemp = table.getTableNumberOfSeats();

                                item.setTitle("Masa nr. " + tableNumberTemp);

                                popupMenu.show();
                            }
                        });
                    }
                });
    }

    private void showTableInfoDialog(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();

        int xLocation = (int) parent.getX();
        int yLocation = (int) parent.getY();

        final Intent intent = new Intent(HallActivity.this,
                TableInfoActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putSerializable("xLocation", xLocation);
        bundle.putSerializable("yLocation", yLocation);

        bundle.putSerializable("tableNumber", tableNumberTemp);
        bundle.putSerializable("tableNumberOfSeats", tableNumberOfSeatsTemp);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void editTableInfoDialog(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        cellId = String.valueOf(parent.getTag());

        int xLocation = (int) parent.getX();
        int yLocation = (int) parent.getY();

        final Intent intent = new Intent(HallActivity.this,
                EditTableInfoActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putSerializable("xLocation", xLocation);
        bundle.putSerializable("yLocation", yLocation);

        bundle.putSerializable("tableNumber", tableNumberTemp);
        bundle.putSerializable("tableNumberOfSeats", tableNumberOfSeatsTemp);

        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }

    private void addWall(final String wallType) {
        final String hallNameAux = hallName.replaceAll("\\s+", "");

        ImageView newView = new ImageView(getApplicationContext());
        Resources res = getResources();
        int resId = res.getIdentifier("ic_launcher_foreground_" + wallType, "drawable", getApplicationContext().getPackageName());

        newView.setImageResource(resId);

        newView.setTag(imageId);
        tempView = newView;

        deleteElementOnClick(newView);
        cell.addView(newView);

        final CollectionReference ref = firebaseFirestoreReference.collection("Cells" + hallNameAux);
        ref.whereEqualTo("cellId", cellId).whereEqualTo("content", null).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                Map<String, Object> map = new HashMap<>();

                                map.put("content", wallType);
                                ref.document(documentSnapshot.getId()).set(map, SetOptions.merge());
                            }
                        }
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HallActivity.this, "Eroare", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void reloadHall() {
        String documentPath = "hall" + hallName;
        documentPath = documentPath.replaceAll("\\s+", "");
        firebaseDatabaseHelper.getSingleDataFieldValue("Halls", documentPath, "cellCollectionName", new SimpleCallback<String>() {
                    @Override
                    public void callback(String cellCollectionName) {
                        for (int i = 0; i < numberOfHeightCells; i++) {
                            for (int j = 0; j < numberOfWidthCells; j++) {
                                final LinearLayout cell = new LinearLayout(HallActivity.this);
                                cell.setOrientation(LinearLayout.VERTICAL);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellWidth, cellWidth);
                                params.setMargins(j * cellWidth, i * cellWidth, 0, 0);
                                cell.setLayoutParams(params);
                                cell.setBackground(ContextCompat.getDrawable(HallActivity.this, R.drawable.simple_border));
                                cell.setTag("cell_" + i + "_" + j);

                                DocumentReference dr = firebaseFirestoreReference.collection(cellCollectionName)
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

                                                        ImageView imageView = new ImageView(HallActivity.this);
                                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cellWidth, cellWidth);
                                                        imageView.setLayoutParams(layoutParams);

                                                        switch (imageName) {
                                                            case "tableImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_masa);
                                                                imageView.setScaleX((float) 0.75);
                                                                imageView.setScaleY((float) 0.75);
                                                                imageView.setTag("tableImageView");

                                                                showPopupTableOnClick(imageView);
                                                                cell.setOnDragListener(new MyDragListener());
                                                                cell.addView(imageView);
                                                                break;

                                                            case "barImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_bar);
                                                                imageView.setTag("barImageView");
                                                                deleteElementOnClick(imageView);
                                                                cell.setOnDragListener(new MyDragListener());
                                                                cell.addView(imageView);
                                                                break;

                                                            case "kitchenImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_bucatarie);
                                                                imageView.setTag("kitchenImageView");
                                                                imageView.setScaleX((float) 1.3);
                                                                imageView.setScaleY((float) 1.3);
                                                                deleteElementOnClick(imageView);
                                                                cell.setOnDragListener(new MyDragListener());
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
                                                                imageView.setTag(imageName);
                                                                deleteElementOnClick(imageView);
                                                                cell.setOnDragListener(new MyDragListener());
                                                                cell.addView(imageView);
                                                                break;

                                                            case "toiletImageView":
                                                                imageView.setBackgroundResource(R.drawable.ic_launcher_foreground_toaleta);
                                                                imageView.setTag("toiletImageView");
                                                                deleteElementOnClick(imageView);
                                                                cell.setOnDragListener(new MyDragListener());
                                                                cell.addView(imageView);
                                                                break;

                                                            default:
                                                                cell.setOnDragListener(new MyDragListener());
                                                                break;
                                                        }
                                                    } else {
                                                        Toast.makeText(HallActivity.this, "Eroare", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(HallActivity.this, "Eroare", Toast.LENGTH_SHORT).show();
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
        finish();
        startActivity(new Intent(HallActivity.this, AdminUIActivity.class));
    }
}
