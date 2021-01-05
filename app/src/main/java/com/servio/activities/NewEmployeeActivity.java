package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.servio.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Clasa activitatii care permite inserarea unui nou angajat in baza de date.
 * Primeste elementele vizuale drept campuri si o referinta catre firebase.
 */
public class NewEmployeeActivity extends AppCompatActivity {

    private EditText fullNameEditTxt;
    private EditText emailEditTxt;
    private EditText passwordEditTxt;
    private Spinner jobSpinner;

    private String firstName;
    private String lastName;

    private Button addEmployeeButton;
    private Button backButton;
    private ProgressBar progressBar;

    private FirebaseFirestore firebaseReference;
    private FirebaseAuth firebaseAuthSecondary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_employee);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        initSpinner();
        initClickListeners();
    }

    /**
     * Initializam elementele vizuale cu cele corespunzatoare din fisierul XML.
     */
    private void initViews() {
        firebaseReference = FirebaseFirestore.getInstance();

        fullNameEditTxt = findViewById(R.id.fullNameEditTxtAddEmp);
        emailEditTxt = findViewById(R.id.mailEditTxtAddEmp);
        passwordEditTxt = findViewById(R.id.passwordEditTextAddEmp);
        jobSpinner = findViewById(R.id.jobSpinnerAddEmp);

        addEmployeeButton = findViewById(R.id.addEmployeeBtnAddEmp);
        backButton = findViewById(R.id.backBtnAddEmp);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://console.firebase.google.com/project/restaurant-fabulos/authentication/users")
                .setApiKey("AIzaSyCkBTqZyN5ujLvX73MTPYK8QkknQa2vMZs")
                .setApplicationId("restaurant-fabulos").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "RestaurantFabulos");
            firebaseAuthSecondary = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            firebaseAuthSecondary = FirebaseAuth.getInstance(FirebaseApp.getInstance("RestaurantFabulos"));
        }
    }

    /**
     * - Adaugam in elementul de tip ”Spinner” (ComboBox) sirurile de caractere care vor fi optiunile
     * de selectat: ”Bucatar”, ”Ospatar”.
     * - Folosim un ArrayAdapter<String> care primeste un array de string-uri.
     * - La final apelam "spinner.setAdapter".
     */
    private void initSpinner() {
        String[] jobs = {"Ospatar", "Bucatar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                jobs);

        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        jobSpinner.setAdapter(adapter);
    }

    /**
     * Se trateaza evenimentele de onclick ale celor doua butoane.
     * - In cazul selectarii butonului "Adauga angajat", se insereaza in baza de date noul angajat.
     * Intai se creeaza contul angajatului cu o instanta a clasei FirebaseAuth.
     * Daca acest cont s-a creat cu succes, se obtine id-ul si se adauga si in baza de date angajatul
     * folosind FirebaseFirestore;
     * <p>
     * - In cazul selectarii butonului "Inapoi", se inchide activitatea curenta
     */
    private void initClickListeners() {
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullNameEditTxt.getText().toString().trim().isEmpty() || emailEditTxt.getText().toString().trim().isEmpty() || passwordEditTxt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewEmployeeActivity.this, "Completati toate campurile", Toast.LENGTH_SHORT).show();
                } else {
                    String email = emailEditTxt.getText().toString().trim();
                    final String job = jobSpinner.getSelectedItem().toString().trim();
                    String password = passwordEditTxt.getText().toString().trim();
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    List<String> keyWords = new ArrayList<>();

                    String[] names = String.valueOf(fullNameEditTxt.getText()).split("[\\s-]+");

                    if (names.length < 2) {
                        Toast.makeText(NewEmployeeActivity.this, "Numele si prenumele trebuie separate prin spatiu", Toast.LENGTH_SHORT).show();
                    } else {
                        lastName = names[0];
                        firstName = fullNameEditTxt.getText().toString().replace(lastName + " ", "");

                        for (int i = 0; i < names.length; i++)
                            names[i] = names[i].toLowerCase();

                        keyWords.add(job.toLowerCase());
                        Collections.addAll(keyWords, names);

                        String[] keyWordsArray = new String[keyWords.size()];
                        keyWordsArray = keyWords.toArray(keyWordsArray);

                        final Map<String, Object> employee = new HashMap<>();

                        employee.put("keyWords", Arrays.asList(keyWordsArray));
                        employee.put("firstName", firstName);
                        employee.put("hireDate", date);
                        employee.put("lastName", lastName);
                        employee.put("photo", "");
                        employee.put("salary", 0);
                        employee.put("type", job);
                        employee.put("email", email);

                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuthSecondary.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser currentFirebaseUser = firebaseAuthSecondary.getCurrentUser();
                                    employee.put("userID", currentFirebaseUser.getUid());

                                    UserProfileChangeRequest profileRequest =new UserProfileChangeRequest.Builder().setDisplayName(lastName+" "+firstName).build();

                                    currentFirebaseUser.updateProfile(profileRequest).addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(NewEmployeeActivity.this, "Eroare la actualizarea profilului utilizatorului.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );

                                    firebaseReference.collection("Employees").document(currentFirebaseUser.getUid()).set(employee).addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(NewEmployeeActivity.this, "Angajatul a fost adaugat cu succes.", Toast.LENGTH_SHORT).show();
                                                    firebaseAuthSecondary.signOut();

                                                    finish();
                                                    startActivity(new Intent(NewEmployeeActivity.this, EmployeesListActivity.class));
                                                }
                                            })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(NewEmployeeActivity.this, "Eroare la adaugarea angajatului.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                            );
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(NewEmployeeActivity.this, "Eroare la adaugarea angajatului.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(NewEmployeeActivity.this,
                        EmployeesListActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(NewEmployeeActivity.this, EmployeesListActivity.class));
    }
}
