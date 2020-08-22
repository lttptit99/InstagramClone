package com.example.instagram.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.adapter.PostAdapter;
import com.example.instagram.model.Post;
import com.example.instagram.model.StateViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomePageFragment extends Fragment {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Post> postList;
    View view;
    NavBackStackEntry backStackEntry ;
    private StateViewModel stateViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_page, container, false);
        NavController navController = NavHostFragment.findNavController(this);
        backStackEntry = navController.getBackStackEntry(R.id.nav_graph);
        stateViewModel = new ViewModelProvider(backStackEntry).get(StateViewModel.class);
        postRecyclerView = (RecyclerView) view.findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.getLayoutManager().onRestoreInstanceState(stateViewModel.parcelable);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Parcelable parcelable = postRecyclerView.getLayoutManager().onSaveInstanceState();
                stateViewModel.parcelable = parcelable;
                postList = new ArrayList<>();
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    Post post = i.getValue(Post.class);
                    postList.add(post);
                }

                Collections.reverse(postList);
                postAdapter = new PostAdapter(getActivity(), postList);
                postRecyclerView.setAdapter(postAdapter);
                postRecyclerView.getLayoutManager().onRestoreInstanceState(stateViewModel.parcelable);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Parcelable parcelable = postRecyclerView.getLayoutManager().onSaveInstanceState();
        stateViewModel.parcelable = parcelable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
