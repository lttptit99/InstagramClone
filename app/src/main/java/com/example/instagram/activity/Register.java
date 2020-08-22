package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity implements View.OnClickListener {
    CircleImageView profile_image;
    static int REQUEST_CODE = 1;
    Uri pickedImage;
    EditText email,user_name,password,confirm_password;
    Button register;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        profile_image.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    private void initView() {
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        email = (EditText) findViewById(R.id.email);
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        register = (Button) findViewById(R.id.register);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                openGallery();
                break;
            case R.id.register:
                dangKy();
        }
    }

    private void dangKy() {
        if(email.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Chưa nhập Email ! Vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        if(user_name.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Chưa nhập User name ! Vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Chưa nhập Password ! Vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!confirm_password.getText().toString().trim().equals(password.getText().toString().trim())){
            Toast.makeText(this, "Xác nhận lại mật khẩu !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pickedImage==null){
            Toast.makeText(this, "Chưa chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().length()<6){
            Toast.makeText(this, "Độ dài mật khẩu phải lớn hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        final String email_user = email.getText().toString().trim();
        final String password_user = password.getText().toString().trim();
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email_user,password_user).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog = new ProgressDialog(Register.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                if(task.isSuccessful()){
                    saveInforUser(email_user,password_user,user_name.getText().toString().trim(),pickedImage,mAuth.getCurrentUser());
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void saveInforUser(final String email_user, final String password_user, final String user_name, final Uri pickedImage, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("profile_image");
        final StorageReference imageFilePath = mStorage.child(pickedImage.getLastPathSegment());
        imageFilePath.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User user = new User(mAuth.getUid(),user_name,email_user,password_user,uri.toString());
                        mDatabase.child("users").push().setValue(user);
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user_name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this,Login.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImage = data.getData();
            profile_image.setImageURI(pickedImage);
        }
    }
}