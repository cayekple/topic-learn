package com.cayekple.topiclearn;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView mTopicListView;
    private List<Topic> mTopicList;

    private FirebaseFirestore mFirebaseFirestore;

    private TopicRecyclerAdapter mTopicRecyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTopicListView = view.findViewById(R.id.topic_list_view);
        mTopicList = new ArrayList<>();

        mTopicRecyclerAdapter = new TopicRecyclerAdapter(mTopicList);

        mTopicListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mTopicListView.setAdapter(mTopicRecyclerAdapter);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseFirestore.collection("Topics").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange: value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        Topic topic = documentChange.getDocument().toObject(Topic.class);
                        mTopicList.add(topic);

                        mTopicRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}