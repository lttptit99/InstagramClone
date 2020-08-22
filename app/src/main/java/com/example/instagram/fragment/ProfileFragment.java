package com.example.instagram.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.activity.Login;
import com.example.instagram.adapter.YourPostAdapter;
import com.example.instagram.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    CircleImageView profile_image;
    TextView user_name,email;
    RecyclerView your_post;
    FirebaseUser mAuth;
    ArrayList<Post> arrPost;
    YourPostAdapter yourPostAdapter;
    ImageView logOut;
    SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,container,false);
        initView(view);
        logOut.setOnClickListener(this);
        return view;
    }

    private void initView(View view) {
        profile_image = (CircleImageView)view.findViewById(R.id.profile_image);
        user_name = (TextView)view.findViewById(R.id.user_name);
        email = (TextView)view.findViewById(R.id.email);
        logOut = (ImageView)view.findViewById(R.id.logOut);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(mAuth.getPhotoUrl()).into(profile_image);
        user_name.setText(mAuth.getDisplayName());
        email.setText(mAuth.getEmail());
        your_post = (RecyclerView)view.findViewById(R.id.your_post);
        your_post.setLayoutManager(new GridLayoutManager(getActivity(),3));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrPost = new ArrayList<>();
                for(DataSnapshot i : dataSnapshot.getChildren()){
                    if(i.child("userId").getValue().toString().equals(mAuth.getUid())){
                        Post post = i.getValue(Post.class);
                        arrPost.add(post);
                    }
                }
                yourPostAdapter = new YourPostAdapter(getContext(),arrPost);
                your_post.setAdapter(yourPostAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logOut:
                sharedPreferences = this.getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("checked",false);
                editor.commit();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
        }
    }
}
