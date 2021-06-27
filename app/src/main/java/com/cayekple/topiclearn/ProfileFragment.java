package com.cayekple.topiclearn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView mProfileTopicView;
    private List<Topic> mProfileTopicList;

    private CircleImageView userProfileImage;
    private TextView txtFullname, txtEducationalLevel;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;

    private String userId;

    private ProfileTopicRecyclerAdapter mProfileTopicRecyclerAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mProfileTopicView = view.findViewById(R.id.profile_topic_list_view);
        mProfileTopicList = new ArrayList<>();

        txtFullname = view.findViewById(R.id.tvFullName);
        txtEducationalLevel = view.findViewById(R.id.tvEducationalLevel);
        userProfileImage = view.findViewById(R.id.imgProfile);

        mProfileTopicRecyclerAdapter = new ProfileTopicRecyclerAdapter(mProfileTopicList);

        mProfileTopicView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mProfileTopicView.setAdapter(mProfileTopicRecyclerAdapter);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            userId = mAuth.getCurrentUser().getUid();
            mFirebaseFirestore.collection("Users")
                    .document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        String name = task.getResult().getString("name");
                        String eduLevel = task.getResult().getString("educational_level");
                        String image = task.getResult().getString("image");

                        txtFullname.setText(name);
                        txtEducationalLevel.setText(eduLevel);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.ic_profile);
                        Glide.with(getContext()).setDefaultRequestOptions(placeholderRequest).load(image).into(userProfileImage);
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), "Error: "+error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        mFirebaseFirestore.collection("Topics")
                .whereEqualTo("user_id", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        Topic topic = documentChange.getDocument().toObject(Topic.class);
                        Log.d("TOPICLESSON", "Data: "+topic);
                        mProfileTopicList.add(topic);

                        mProfileTopicRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        Log.d("TOPICLESSON", "Data: "+mProfileTopicList);

        return view;
    }
}