package com.servio.recyclerviews.employee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.servio.R;
import com.servio.activities.EditEmployeeActivity;
import com.servio.activities.EmployeeInfoActivity;
import com.servio.models.Employee;

import org.jetbrains.annotations.NotNull;

public class EmployeeAdapter extends FirestoreRecyclerAdapter<Employee, EmployeeViewHolder> {
    private Context context;

    public EmployeeAdapter(@NonNull FirestoreRecyclerOptions<Employee> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Se creeaza cardul corespunzator obiectului de tip Employee de pe pozitia i
     *
     * @param employeeViewHolder obiect de tip EmployeeViewHolder = sablonul unui card
     * @param i                  indexul obiectului de tip Employee care trebuie afisat
     */
    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder employeeViewHolder, int i, @NotNull Employee employee) {
        if (employee.getFirstName() != null && employee.getLastName() != null) {
            String name = employee.getFirstName() + " " + employee.getLastName();
            employeeViewHolder.setEmployeeName(name);
        }

        if (employee.getType() != null) {
            employeeViewHolder.setEmployeeType(employee.getType());
        }

        setOnClickListeners(employeeViewHolder, employee);
    }

    private void setOnClickListeners(@NonNull final EmployeeViewHolder employeeViewHolder, final Employee employee) {

        employeeViewHolder.getButtonRemove().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Confirmare stergere angajat");
                alertDialog.setMessage("Doriti sa stergeti angajatul " + employee.getLastName() + " " + employee.getFirstName() + "?");

                alertDialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSnapshots().getSnapshot(employeeViewHolder.getAdapterPosition()).getReference().delete();
                        Toast.makeText(context, "Angajatul a fost sters cu succes", Toast.LENGTH_SHORT).show();
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
        });

        employeeViewHolder.getButtonEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(),
                        EditEmployeeActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("employee", employee);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });

        employeeViewHolder.getButtonInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(),
                        EmployeeInfoActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("employee", employee);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_view, parent, false);
        return new EmployeeViewHolder(view);
    }
}
