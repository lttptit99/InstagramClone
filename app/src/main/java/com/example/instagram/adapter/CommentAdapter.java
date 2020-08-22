package com.example.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.activity.Comment;
import com.example.instagram.model.CommentModel;
import com.example.instagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context context;
    private List<CommentModel> mData;
    String txt;
    String comment;


    public CommentAdapter(Comment context, ArrayList<CommentModel> commentsList) {
        this.context = context;
        this.mData = commentsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.row_comment_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.MyViewHolder holder, final int position) {
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("users");
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (i.child("user_id").getValue().toString().equals(mData.get(position).getUser_comment())) {
                        Picasso.get().load(i.child("profile_image").getValue().toString()).into(holder.profile_image);
                        txt = i.child("user_name").getValue().toString();
                    }
                }
                comment = txt +" "+mData.get(position).getComment();
                CharSequence source = comment;
                SpannableString spannableString = new SpannableString(source);
                spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, txt.length(), 0);
                holder.user_name_cmt.setText(spannableString);


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
        TextView user_name_cmt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            user_name_cmt = itemView.findViewById(R.id.user_name_cmt);
        }
    }
}
