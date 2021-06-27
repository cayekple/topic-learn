package com.cayekple.topiclearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ProfileTopicRecyclerAdapter extends RecyclerView.Adapter<ProfileTopicRecyclerAdapter.ViewHolder> {

    public List<Topic> mProfileTopicList;
    public Context mContext;

    private FirebaseFirestore mFirebaseFirestore;

    public ProfileTopicRecyclerAdapter(List<Topic> mProfileTopicList) {
        this.mProfileTopicList = mProfileTopicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_topic_list_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String topicData = mProfileTopicList.get(position).getTopic();
        holder.setTopicText(topicData);

        String userId = mProfileTopicList.get(position).getUser_id();

        String image = mProfileTopicList.get(position).getImage();
        holder.setTopicImage(image);

//        mFirebaseFirestore.collection("Topics").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//
//                }
//            }
//        });

        long millisecond = mProfileTopicList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.getDateInstance().format(new Date(millisecond));
    }

    @Override
    public int getItemCount() {
        return mProfileTopicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View mView;

        private TextView topicView;
        private ImageView topicImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTopicText(String topicText){
            topicView = mView.findViewById(R.id.tvTopic);
            topicView.setText(topicText);
        }

        public void setTopicImage(String downloadUrl){
            topicImageView = mView.findViewById(R.id.imgTopic);
            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.ic_profile);
            Glide.with(mContext).setDefaultRequestOptions(placeholderRequest).load(downloadUrl).into(topicImageView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
