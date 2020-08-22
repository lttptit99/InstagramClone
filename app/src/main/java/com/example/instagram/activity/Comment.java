package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.adapter.CommentAdapter;
import com.example.instagram.model.CommentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment extends AppCompatActivity implements View.OnClickListener {
    Intent intent;
    String postId;
    FirebaseUser mAuth;
    CircleImageView image_user;
    ImageView image_post;
    ImageView post_comment;
    EditText comment;
    RecyclerView recyclerView;
    ArrayList<CommentModel> commentsList = new ArrayList<>();
    CommentAdapter commentAdapter ;
    TextView name_desc;
    String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        post_comment.setOnClickListener(this);
    }

    private void initView() {
        intent = getIntent();
        postId = intent.getStringExtra("postId");
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.reclye_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(Comment.this,commentsList);
        recyclerView.setAdapter(commentAdapter);
        image_user = (CircleImageView) findViewById(R.id.image_user);
        Picasso.get().load(mAuth.getPhotoUrl()).into(image_user);
        image_post = (ImageView) findViewById(R.id.image_post);
        readComment();
        String txt;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(image_post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        comment = (EditText) findViewById(R.id.comment);
        post_comment = (ImageView) findViewById(R.id.post_comment);



    }

    private void readComment() {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    commentsList.clear();
                    for(DataSnapshot i : dataSnapshot.getChildren()){
                        CommentModel cmt = i.getValue(CommentModel.class);
                        commentsList.add(cmt);
                    }
                    commentAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(commentsList.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_comment:
                postComment();
                break;
        }
    }

    private void postComment() {
        String txt_comment = comment.getText().toString().trim();
        if (txt_comment.isEmpty()) {
            Toast.makeText(this, "Please input your comment", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        HashMap hashMap = new HashMap<String, Object>();
        hashMap.put("comment", txt_comment);
        hashMap.put("user_comment", mAuth.getUid());
        commentRef.push().setValue(hashMap);
        comment.getText().clear();
    }
}