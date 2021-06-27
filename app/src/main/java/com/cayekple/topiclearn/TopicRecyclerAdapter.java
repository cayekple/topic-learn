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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class TopicRecyclerAdapter extends FirestoreRecyclerAdapter<Topic, TopicRecyclerAdapter.ViewHolder> {

    public List<Topic> mTopicList;
    public Context mContext;

    private OnCardClickListener mOnCardClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TopicRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Topic> options) {
        super(options);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_list_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Topic topic) {

        holder.setTopicText(topic.getTopic());
        holder.setTopicImage(topic.getImage());

        long millisecond = topic.getTimestamp().getTime();
        String dateString = DateFormat.getDateInstance().format(new Date(millisecond));

//        final String topicId = mTopicList.get(position).getTopicId();
//
//        holder.topicImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnCardClickListener.onCardClick(topicId);
//            }
//        });
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
