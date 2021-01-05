package com.servio.recyclerviews.employee;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.servio.R;

/**
 * Clasa de tip ViewHolder care primeste elementele vizuale corespunzatoare fisierului XML:
 * "employee_view.xml"
 */
class EmployeeViewHolder extends RecyclerView.ViewHolder {

    private TextView employeeName;
    private TextView employeeType;

    private ImageButton buttonEdit;
    private ImageButton buttonRemove;
    private ImageButton buttonInfo;

    EmployeeViewHolder(@NonNull View itemView) {
        super(itemView);
        employeeName = itemView.findViewById(R.id.nameTxtViewRecyclerView);
        employeeType = itemView.findViewById(R.id.empTypeTxtViewRecyclerView);

        buttonEdit = itemView.findViewById(R.id.editBtnRecyclerView);
        buttonRemove = itemView.findViewById(R.id.removeBtnRecyclerView);
        buttonInfo = itemView.findViewById(R.id.infoBtnRecyclerView);
    }

    void setEmployeeName(String name) {
        employeeName.setText(name);
    }

    void setEmployeeType(String type) {
        employeeType.setText(type);
    }

    ImageButton getButtonEdit() {
        return buttonEdit;
    }

    ImageButton getButtonRemove() {
        return buttonRemove;
    }

    ImageButton getButtonInfo() {
        return buttonInfo;
    }
}
