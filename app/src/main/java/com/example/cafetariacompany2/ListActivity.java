package com.example.cafetariacompany2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity{
    private ListView listView;
    private ArrayList<Employee> employeeList;
    DatabaseReference dbEmployee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        listView = findViewById(R.id.list);
        dbEmployee = FirebaseDatabase.getInstance().getReference("employee");
        employeeList = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListActivity.this, UpdateActivity.class);
                intent.putExtra(UpdateActivity.EXTRA_EMPLOYEE, employeeList.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbEmployee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employeeList.clear();
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    Employee employee = employeeSnapshot.getValue(Employee.class);
                    employeeList.add(employee);
                }
                EmployeeAdapter adapter = new EmployeeAdapter(ListActivity.this);
                adapter.setEmployeeList(employeeList);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });
        
        //p user veronica
    }
}