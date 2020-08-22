package com.example.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.activity.Comment;
import com.example.instagram.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.UserWriteRecord;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<Post> mData;
    String id, name, name_desc;
    FirebaseUser mAuth;
    int temp = 0;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public PostAdapter(Context context, List<Post> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        id = mData.get(position).getUserId();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(mData.get(position).getProfile_image()).into(holder.profile_image);
        Picasso.get().load(mData.get(position).getImage()).into(holder.image_post);
        isLike(mData.get(position).getPostKey(), holder.like, mAuth.getUid());
        countLike(mData.get(position).getPostKey(), holder.numLike);
        countComment(mData.get(position).getPostKey(), holder.count_comment);
        holder.count_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("postId", mData.get(position).getPostKey());
                context.startActivity(intent);
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp == 1) {
                    FirebaseDatabase.getInstance().getReference().child("like").child(mData.get(position).getPostKey()).child(mAuth.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("like").child(mData.get(position).getPostKey()).child(mAuth.getUid()).removeValue();

                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("postId", mData.get(position).getPostKey());
                context.startActivity(intent);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (i.child("user_id").getValue().toString().equals(mData.get(position).getUserId())) {
                        name = i.child("user_name").getValue().toString();
                        holder.user_name.setText(name);
                        name_desc = name + " " + mData.get(position).getDescription();
                    }
                }
                CharSequence source = name_desc;
                SpannableString spannableString = new SpannableString(source);
                spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), 0);
                holder.user_name2.setText(spannableString);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void countComment(String postKey, final TextView count_comment) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postKey);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    count_comment.setText("Xem tất cả " + dataSnapshot.getChildrenCount() + " bình luận");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countLike(String postKey, final TextView numLike) {
        DatabaseReference LikeRef = FirebaseDatabase.getInstance().getReference().child("like").child(postKey);
        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    numLike.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    numLike.setText("0");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLike(String postKey, final Button like, final String uid) {
        DatabaseReference LikeRef = FirebaseDatabase.getInstance().getReference().child("like").child(postKey);
        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).exists()) {
                    temp = 0;
                    like.setBackgroundResource(R.drawable.ic_liked);
                } else {
                    temp = 1;
                    like.setBackgroundResource(R.drawable.ic_like);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView user_name;
        ImageView image_post;
        TextView user_name2;
        Button like, comment;
        TextView numLike, count_comment;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            user_name = itemView.findViewById(R.id.user_name);
            image_post = itemView.findViewById(R.id.image_post);
            user_name2 = itemView.findViewById(R.id.user_name2);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            numLike = itemView.findViewById(R.id.numLike);
            count_comment = itemView.findViewById(R.id.count_comment);

        }
    }
}
