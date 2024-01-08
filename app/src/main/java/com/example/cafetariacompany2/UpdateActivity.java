package com.example.cafetariacompany2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {
    String[] items = {"Male", "Female"};

    private EditText edtName, edtBirthdate, edtPhone, edtAddress;
    private ImageView edtAvatar;
    private Button btnUpdate;
    public static final String EXTRA_EMPLOYEE = "extra_employee";
    public final int ALERT_DIALOG_CLOSE = 10;
    public final int ALERT_DIALOG_DELETE = 20;
    private Employee employee;
    private String employeeId;
    DatabaseReference mDatabase;

    AutoCompleteTextView edtSex;
    ArrayAdapter<String> adapterItems;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtSex = findViewById(R.id.edt_edit_sex);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        edtSex.setAdapter(adapterItems);

        edtName = findViewById(R.id.edt_edit_name);
        edtBirthdate = findViewById(R.id.edt_edit_birthdate);
        edtPhone = findViewById(R.id.edt_edit_phone);
        edtAddress = findViewById(R.id.edt_edit_address);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);

        employee = getIntent().getParcelableExtra(EXTRA_EMPLOYEE);
        if (employee != null) {
            employeeId = employee.getId();
        } else {
            employee = new Employee();
        }
        if (employee != null) {
            edtName.setText(employee.getName());
            edtBirthdate.setText(employee.getBirthdate());
            edtPhone.setText(employee.getPhone());
            edtAddress.setText(employee.getAddress());
            edtSex.setText(employee.getSex());
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_update) {
            updateEmployee();
        }
    }
    private void updateEmployee() {
        String name = edtName.getText().toString().trim();
        String birthdate = edtBirthdate.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String sex = edtSex.getText().toString().trim();


        boolean isEmptyFields = false;
        if (TextUtils.isEmpty(name)) {
            isEmptyFields = true;
            edtName.setError("Field ini tidak boleh kosong");
        }
        if (TextUtils.isEmpty(birthdate)) {
            isEmptyFields = true;
            edtBirthdate.setError("Field ini tidak boleh kosong");
        }
        if (TextUtils.isEmpty(phone)) {
            isEmptyFields = true;
            edtPhone.setError("Field ini tidak boleh kosong");
        }
        if (TextUtils.isEmpty(address)) {
            isEmptyFields = true;
            edtAddress.setError("Field ini tidak boleh kosong");
        }
        if (TextUtils.isEmpty(sex)) {
            isEmptyFields = true;
            edtSex.setError("Field ini tidak boleh kosong");
        }
        if (! isEmptyFields) {
            Toast.makeText(UpdateActivity.this, "Updating Data...", Toast.LENGTH_SHORT).show();
            employee.setName(name);
            employee.setBirthdate(birthdate);
            employee.setPhone(phone);
            employee.setAddress(address);
            employee.setSex(sex);
            DatabaseReference dbEmployee = mDatabase.child("employee");

            //update data
            dbEmployee.child(employeeId).setValue(employee);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //pilih menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showAlertDialog(ALERT_DIALOG_DELETE);
        } else if (item.getItemId() == android.R.id.home) {
            showAlertDialog(ALERT_DIALOG_CLOSE);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;
        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        } else {
            dialogTitle = "Hapus Data";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isDialogClose) {
                    finish();
                } else {
                    //hapus data
                    DatabaseReference dbEmployee = mDatabase.child("employee").child(employeeId);
                    dbEmployee.removeValue();
                    Toast.makeText(UpdateActivity.this,"Deleting data...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}