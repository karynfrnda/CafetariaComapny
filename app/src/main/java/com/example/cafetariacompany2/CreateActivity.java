package com.example.cafetariacompany2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {
    String[] items = {"Male", "Female"};

    private EditText edtName, edtBirthdate, edtPhone, edtAddress;
    private ImageView edtAvatar;
    private Button btnSubmit;
    private Employee employee;
    DatePickerDialog.OnDateSetListener setListener;
    DatabaseReference mDatabase;

    AutoCompleteTextView edtSex;
    ArrayAdapter<String> adapterItems;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtSex = findViewById(R.id.edt_sex);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        edtSex.setAdapter(adapterItems);

        edtName = findViewById(R.id.edt_name);
        edtBirthdate = findViewById(R.id.edt_birthdate);
        edtPhone = findViewById(R.id.edt_phone);
        edtAddress = findViewById(R.id.edt_address);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v->{
            if(edtName.getText().length()>0 && edtBirthdate.getText().length()>0 && edtPhone.getText().length()>0 && edtAddress.getText().length()>0 && edtSex.getText().length()>0){
                upload(edtName.getText().toString(), edtBirthdate.getText().toString(), edtPhone.getText().toString(), edtAddress.getText().toString(), edtSex.getText().toString());
            }else{
                Toast.makeText(getApplicationContext(), "Please fill all the data!", Toast.LENGTH_SHORT).show();
            }
        });

        employee = new Employee();
        edtAvatar = findViewById(R.id.edt_avatar);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        edtAvatar.setOnClickListener(v->{
            selectImage();
        });

        edtSex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        if (intent != null){
            edtName.setText(intent.getStringExtra("name"));
            edtBirthdate.setText(intent.getStringExtra("birthdate"));
            edtPhone.setText(intent.getStringExtra("phone"));
            edtAddress.setText(intent.getStringExtra("address"));
            edtSex.setText(intent.getStringExtra("sex"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("avatar")).into(edtAvatar);
        }

        edtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
//                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        edtBirthdate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit) {
            saveEmployee();
        }
    }

    private void selectImage(){
        final CharSequence[] items = {"Choose from library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item)-> {
            if (items[item].equals("Choose from library")){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 20);
            } else if (items[item].equals("Cancel")){
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null){
            final Uri path = data.getData();
            Thread thread = new Thread(()->{
                try{
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    edtAvatar.post(()->{
                        edtAvatar.setImageBitmap(bitmap);
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

    private void upload(String name, String birthdate, String phone, String sex, String address){
        edtAvatar.setDrawingCacheEnabled(true);
        edtAvatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) edtAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //Upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images").child("IMG" + new Date().getTime() + ".jpeg");
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata()!=null){
                    if(taskSnapshot.getMetadata().getReference()!=null){
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.getResult()!=null){
                                    saveEmployee();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveEmployee()
    {
        String name = edtName.getText().toString().trim();
        String birthdate = edtBirthdate.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String sex = edtSex.getText().toString().trim();
        String avatar = edtAvatar.getDisplay().toString();
        boolean isEmptyFields = false;
        if (TextUtils.isEmpty(name)) {
            isEmptyFields = true;
            edtName.setError("This field cannot be empty");
        }
        if (TextUtils.isEmpty(birthdate)) {
            isEmptyFields = true;
            edtBirthdate.setError("This field cannot be empty");
        }
        if (TextUtils.isEmpty(phone)) {
            isEmptyFields = true;
            edtPhone.setError("This field cannot be empty");
        }
        if (TextUtils.isEmpty(address)) {
            isEmptyFields = true;
            edtAddress.setError("This field cannot be empty");
        }
        if (TextUtils.isEmpty(sex)) {
            isEmptyFields = true;
            edtSex.setError("This field cannot be empty");
        }
        if (! isEmptyFields) {
            try {
                Toast.makeText(CreateActivity.this, "Saving Data...",
                        Toast.LENGTH_SHORT).show();
                DatabaseReference dbEmployee = mDatabase.child("employee");
                String id = dbEmployee.push().getKey();
                employee.setId(id);
                employee.setName(name);
                employee.setBirthdate(birthdate);
                employee.setPhone(phone);
                employee.setAddress(address);
                employee.setSex(sex);
                employee.setAvatar(avatar);

                //insert data
                dbEmployee.child(id).setValue(employee);
                finish();
                System.out.println("OKE");
            }
            catch (Exception ex)
            {
                System.out.println("Error : " + ex);
            }
        }
    }
}
