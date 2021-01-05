package com.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.servio.R;
import com.servio.models.Employee;

public class EmployeeInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_info);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Employee emp = (Employee) bundle.getSerializable("employee");

        TextView fullNameTextView = findViewById(R.id.textViewNameText);
        String fullName = emp.getLastName() + " " + emp.getFirstName();
        fullNameTextView.setText(fullName);

        TextView emailTextView = findViewById(R.id.textViewEmailText);
        emailTextView.setText(emp.getEmail());

        TextView hireDateTextView = findViewById(R.id.textViewHireDateText);
        hireDateTextView.setText(emp.getHireDate());

        TextView salaryTextView = findViewById(R.id.textViewSalaryValue);
        salaryTextView.setText(String.valueOf(emp.getSalary()));

        TextView jobTextView = findViewById(R.id.textViewTypeText);
        jobTextView.setText(emp.getType());

        Button backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(EmployeeInfoActivity.this,
                        EmployeesListActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EmployeeInfoActivity.this, EmployeesListActivity.class));
    }
}
