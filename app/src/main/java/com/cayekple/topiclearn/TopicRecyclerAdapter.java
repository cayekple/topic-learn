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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class TopicRecyclerAdapter extends RecyclerView.Adapter<TopicRecyclerAdapter.ViewHolder> {

    public List<Topic> mTopicList;
    public Context mContext;

    public TopicRecyclerAdapter(List<Topic> mTopicList) {
        this.mTopicList = mTopicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_list_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String topicData = mTopicList.get(position).getTopic();
        holder.setTopicText(topicData);

        String image = mTopicList.get(position).getImage();
        holder.setTopicImage(image);

        long millisecond = mTopicList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.getDateInstance().format(new Date(millisecond));
    }

    @Override
    public int getItemCount() {
        return mTopicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView topicView;
        private ImageView topicImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTopicText(String topicText){
            topicView = mView.findViewById(R.id.txtTopicTitle);
            topicView.setText(topicText);
        }

        public void setTopicImage(String downloadUrl){
            topicImageView = mView.findViewById(R.id.circleImageView);
            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.ic_profile);
            Glide.with(mContext).setDefaultRequestOptions(placeholderRequest).load(downloadUrl).into(topicImageView);
        }
    }
}
