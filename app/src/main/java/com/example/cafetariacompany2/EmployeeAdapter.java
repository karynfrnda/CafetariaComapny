package com.example.cafetariacompany2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EmployeeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Employee> employeeList = new ArrayList<>();

    public void setEmployeeList(ArrayList<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public EmployeeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return employeeList.size();
    }

    @Override
    public Object getItem(int i) {
        return employeeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.data_employee, viewGroup, false);
        }
        ViewHolder viewHolder = new ViewHolder(itemView);
        Employee employee = (Employee) getItem(i);
        viewHolder.bind(employee);
        return itemView;
    }

    private class ViewHolder {
        private TextView txtName, txtBirthdate, txtPhone, txtAddress;
        private TextView txtSex;
        private ImageView txtAvatar;
        ViewHolder(View view) {
            txtName = view.findViewById(R.id.txt_name);
            txtBirthdate = view.findViewById(R.id.txt_birthdate);
            txtPhone = view.findViewById(R.id.txt_phone);
            txtAddress = view.findViewById(R.id.txt_address);
            txtSex = view.findViewById(R.id.txt_sex);
            txtAvatar = view.findViewById(R.id.txt_avatar);
        }
        void bind(Employee employee) {
            txtName.setText(employee.getName());
            txtBirthdate.setText(employee.getBirthdate());
            txtPhone.setText(employee.getPhone());
            txtAddress.setText(employee.getAddress());
            txtSex.setText(employee.getSex());
//            txtAvatar.setText(employee.getAvatar());
            Glide.with(txtAvatar).load(employee.getAvatar());
        }
    }


}