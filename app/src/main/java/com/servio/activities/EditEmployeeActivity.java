package com.servio.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;
import com.servio.helpers.FirebaseDatabaseHelper;
import com.servio.models.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EditEmployeeActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private TextView hireDateTextView;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private EditText salaryEditText;
    private Spinner mJobSpinner;
    private Button saveInfoButton;
    private ProgressBar progressBar;

    private String firstName;
    private String lastName;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private Employee emp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        emp = (Employee) bundle.getSerializable("employee");

        initViews();
        saveData();
    }

    private void initViews() {
        FirebaseFirestore firebaseReference = FirebaseFirestore.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(firebaseReference, EditEmployeeActivity.this);

        fullNameEditText = findViewById(R.id.fullNameEditTxtEditEmp);
        String fullName = emp.getLastName() + " " + emp.getFirstName();
        fullNameEditText.setText(fullName);

        TextView emailEditText = findViewById(R.id.emailEditTxtEditEmp);
        emailEditText.setText(emp.getEmail());
        emailEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditEmployeeActivity.this, "Adresa de email nu se poate modifica", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        hireDateTextView = findViewById(R.id.hireDateEditTextEditEmp);
        hireDateTextView.setText(emp.getHireDate());
        pickDate();

        salaryEditText = findViewById(R.id.salaryEditTextEditEmp);
        salaryEditText.setText(String.valueOf(emp.getSalary()));

        mJobSpinner = findViewById(R.id.jobSpinnerEditEmp);
        initSpinner();

        if (emp.getType().toLowerCase().equals("ospatar")) {
            mJobSpinner.setSelection(0);
        } else {
            mJobSpinner.setSelection(1);
        }

        Button backButton = findViewById(R.id.backBtnEditEmp);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(EditEmployeeActivity.this,
                        EmployeesListActivity.class));
            }
        });

        saveInfoButton = findViewById(R.id.saveEmployeeBtnEditEmp);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void pickDate() {
        hireDateTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                EditEmployeeActivity.this,
                                R.style.Theme_AppCompat_Light_Dialog_MinWidth, onDateSetListener, year, month, day);

                        datePickerDialog.show();
                    }
                }
        );

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String hireDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                hireDateTextView.setText(hireDate);
            }
        };
    }

    private void initSpinner() {
        String[] jobs = {"Ospatar", "Bucatar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                jobs);

        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        mJobSpinner.setAdapter(adapter);
    }

    private void saveData() {
        saveInfoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (fullNameEditText.getText().toString().trim().isEmpty() || salaryEditText.getText().toString().trim().isEmpty()) {
                            Toast.makeText(EditEmployeeActivity.this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.VISIBLE);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    List<String> keyWords = new ArrayList<>();

                                    String[] names = String.valueOf(fullNameEditText.getText()).split("[\\s-]+");

                                    if (names.length < 2) {
                                        Toast.makeText(EditEmployeeActivity.this, "Numele si prenumele trebuie separate prin spatiu", Toast.LENGTH_SHORT).show();
                                    } else {
                                        lastName = names[0];
                                        firstName = fullNameEditText.getText().toString().replace(lastName + " ", "");

                                        for (int i = 0; i < names.length; i++)
                                            names[i] = names[i].toLowerCase();

                                        firebaseDatabaseHelper.simpleUpdateField("Employees", emp.getUserID(), "firstName", firstName);

                                        firebaseDatabaseHelper.simpleUpdateField("Employees", emp.getUserID(), "lastName", lastName);

                                        firebaseDatabaseHelper.simpleUpdateField("Employees", emp.getUserID(), "hireDate", hireDateTextView.getText().toString());

                                        firebaseDatabaseHelper.simpleUpdateField("Employees", emp.getUserID(), "salary", Double.valueOf(salaryEditText.getText().toString()));

                                        firebaseDatabaseHelper.simpleUpdateField("Employees", emp.getUserID(), "type", mJobSpinner.getSelectedItem().toString());

                                        keyWords.add(mJobSpinner.getSelectedItem().toString().toLowerCase());
                                        Collections.addAll(keyWords, names);

                                        String[] keyWordsArray = new String[keyWords.size()];
                                        keyWordsArray = keyWords.toArray(keyWordsArray);

                                        firebaseDatabaseHelper.simpleUpdateField("Employees", emp.getUserID(), "keyWords", Arrays.asList(keyWordsArray));

                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(EditEmployeeActivity.this, "Datele au fost actualizate cu succes", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(EditEmployeeActivity.this, EmployeesListActivity.class));
                                    }
                                }
                            }, 500);
                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EditEmployeeActivity.this, EmployeesListActivity.class));
    }
}
