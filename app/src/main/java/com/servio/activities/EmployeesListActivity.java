package com.servio.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.servio.R;
import com.servio.models.Employee;
import com.servio.recyclerviews.employee.EmployeeAdapter;

import java.util.Arrays;

/**
 * Clasa corespunzatoare activitatii care afiseaza lista cu toti angajatii.
 */

public class EmployeesListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addEmployeeBtn;
    private ImageButton backImageButton;
    private ImageButton refreshImageButton;
    private EditText editTextSearchEmployee;
    private ImageButton imageButtonSearchEmployee;

    private Context context;
    private final FirebaseFirestore firestoreReference = FirebaseFirestore.getInstance();
    private EmployeeAdapter employeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        setLayoutManager();

        addEmployeeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(new Intent(EmployeesListActivity.this, NewEmployeeActivity.class));
                    }
                }
        );

        backImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(new Intent(EmployeesListActivity.this, AdminUIActivity.class));
                    }
                }
        );

        refreshImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTextSearchEmployee.setText("");

                        Query query = firestoreReference.collection("Employees").orderBy("firstName", Query.Direction.ASCENDING);
                        employeeAdapter.stopListening();
                        FirestoreRecyclerOptions<Employee> options = new FirestoreRecyclerOptions.Builder<Employee>()
                                .setQuery(query, Employee.class)
                                .build();

                        employeeAdapter = new EmployeeAdapter(options, context);
                        recyclerView.setAdapter(employeeAdapter);
                        employeeAdapter.startListening();
                    }
                }
        );

        imageButtonSearchEmployee.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] keyWords = editTextSearchEmployee.getText().toString().toLowerCase().split("[ -]+");

                        if (!editTextSearchEmployee.getText().toString().matches("")) {

                            final Query query = firestoreReference.collection("Employees").whereArrayContainsAny("keyWords", Arrays.asList(keyWords));

                            query.get().addOnSuccessListener(
                                    new OnSuccessListener<QuerySnapshot>() {
                                        int valid = 0;

                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                if (document.exists()) {
                                                    valid = 1;
                                                }
                                            }
                                            if (valid == 1) {
                                                employeeAdapter.stopListening();
                                                FirestoreRecyclerOptions<Employee> options = new FirestoreRecyclerOptions.Builder<Employee>()
                                                        .setQuery(query, Employee.class)
                                                        .build();

                                                employeeAdapter = new EmployeeAdapter(options, context);
                                                recyclerView.setAdapter(employeeAdapter);
                                                employeeAdapter.startListening();
                                            } else {
                                                Toast.makeText(EmployeesListActivity.this, "Nu s-a gasit angajatul", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                            );

                        } else if (editTextSearchEmployee.getText().toString().matches("")) {
                            Query mainQuery = firestoreReference.collection("Employees").orderBy("firstName", Query.Direction.ASCENDING);
                            employeeAdapter.stopListening();
                            FirestoreRecyclerOptions<Employee> options = new FirestoreRecyclerOptions.Builder<Employee>()
                                    .setQuery(mainQuery, Employee.class)
                                    .build();

                            employeeAdapter = new EmployeeAdapter(options, context);
                            recyclerView.setAdapter(employeeAdapter);
                            employeeAdapter.startListening();
                        }
                    }
                }
        );
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewEmpList);
        addEmployeeBtn = findViewById(R.id.addEmployeeBtnEmpList);
        backImageButton = findViewById(R.id.imageButtonBack);
        refreshImageButton = findViewById(R.id.imageButtonRefresh);
        editTextSearchEmployee = findViewById(R.id.searchEmployeeEditText);
        editTextSearchEmployee.setImeOptions(EditorInfo.IME_ACTION_DONE);
        imageButtonSearchEmployee = findViewById(R.id.imageButtonSearchEmployee);
    }

    private void setLayoutManager() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = firestoreReference.collection("Employees").orderBy("lastName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Employee> options = new FirestoreRecyclerOptions.Builder<Employee>()
                .setQuery(query, Employee.class)
                .build();

        employeeAdapter = new EmployeeAdapter(options, this);
        context = this;

        recyclerView.setAdapter(employeeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        employeeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        employeeAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EmployeesListActivity.this, AdminUIActivity.class));
    }
}
