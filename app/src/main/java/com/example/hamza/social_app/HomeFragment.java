package com.example.hamza.social_app;


import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamza.social_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private List<BlogPost> list;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Query query;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(container.getContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        firestore.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    assert queryDocumentSnapshots != null;
                                    if (e!=null){
                                        Toast.makeText(getActivity(),"empty",Toast.LENGTH_LONG).show();
                                    }
                                    else{


                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                        if (doc.getType() == DocumentChange.Type.ADDED){
                                            String blogPostId = doc.getDocument().getId();
                                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                            list.add(blogPost);
                                            recyclerViewAdapter.notifyDataSetChanged();
                                        }
                        }
                    }
                }
        });
        return view;
    }
}
