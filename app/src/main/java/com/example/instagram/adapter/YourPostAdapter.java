package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class YourPostAdapter extends RecyclerView.Adapter<YourPostAdapter.MyViewHolder> {
    Context context;
    ArrayList<Post> mData;

    public YourPostAdapter(Context context, ArrayList<Post> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_your_post_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(mData.get(position).getImage()).into(holder.image_your_post);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image_your_post;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image_your_post = itemView.findViewById(R.id.image_your_post);
        }
    }
}
