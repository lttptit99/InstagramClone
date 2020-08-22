package com.example.instagram.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE = 1;
    private static final int RESULT_OK = -1;
    View view;
    EditText title, desc;
    CircleImageView profile_image;
    ImageView image_post, add_image;
    Button add_post;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Uri pickedImage =null;
    ProgressDialog progressDialog ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_post, container, false);
        title = (EditText) view.findViewById(R.id.title);
        desc = (EditText) view.findViewById(R.id.description);
        image_post = (ImageView) view.findViewById(R.id.image_post);
        add_image = (ImageView) view.findViewById(R.id.add_image);
        add_post = (Button)view.findViewById(R.id.add_post);
        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Picasso.get().load(currentUser.getPhotoUrl()).into(profile_image);
        add_image.setOnClickListener(this);
        add_post.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image:
                openGallery();
                break;
            case R.id.add_post:
                savePost();
                break;
        }
    }

    private void savePost() {
        if (!title.getText().toString().isEmpty() && !desc.getText().toString().isEmpty() && pickedImage!=null){
            Context context;
            progressDialog = new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("post_images");
            final StorageReference imageFilePath = storageReference.child(pickedImage.getLastPathSegment());
            imageFilePath.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Post post = new Post(title.getText().toString(),desc.getText().toString(),uri.toString(),currentUser.getPhotoUrl().toString(),currentUser.getUid());
                            addPost(post);

                        }
                    });
                }
            });


        }
        else{
            Toast.makeText(getContext(), "Vui lòng điền đủ thông tin của Post", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("posts").push();
        String key = myRef.getKey();
        post.setPostKey(key);
        myRef.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Post bài thành công", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AddPostFragment.this).navigate(R.id.homePageFragment);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImage = data.getData();
            image_post.setImageURI(pickedImage);


        }
    }
}
