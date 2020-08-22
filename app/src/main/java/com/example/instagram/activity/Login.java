package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {
    TextView register;
    Button login;
    EditText email, password;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    CheckBox saveInfor;
    SharedPreferences sharedPreferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        String data_email = sharedPreferences.getString("email", "");
        String data_password = sharedPreferences.getString("password", "");
        if (sharedPreferences.getBoolean("checked",false)){
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mAuth.signInWithEmailAndPassword(data_email, data_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Đăng nhập không thàng công", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

        }
    }

    private void initView() {
        register = (TextView) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        saveInfor = (CheckBox) findViewById(R.id.saveInfor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                break;
            case R.id.login:
                dangNhap();
                break;
        }
    }

    private void dangNhap() {
        String email_user = email.getText().toString().trim();
        String password_user = password.getText().toString().trim();
        if (email_user.isEmpty() || password_user.isEmpty()) {
            Toast.makeText(this, "Email hoặc mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mAuth.signInWithEmailAndPassword(email_user, password_user).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Đăng nhập không thàng công", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        if (saveInfor.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email_user);
            editor.putString("password", password_user);
            editor.putBoolean("checked",true);
            editor.apply();
        }
    }
}