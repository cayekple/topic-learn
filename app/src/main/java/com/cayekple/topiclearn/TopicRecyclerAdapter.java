package com.cayekple.topiclearn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class TopicRecyclerAdapter extends RecyclerView.Adapter<TopicRecyclerAdapter.ViewHolder> {

    public List<Topic> mTopicList;

    public TopicRecyclerAdapter(List<Topic> mTopicList) {
        this.mTopicList = mTopicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String topicData = mTopicList.get(position).getTopic();
        holder.setTopicText(topicData);
    }

    @Override
    public int getItemCount() {
        return mTopicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView topicView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTopicText(String topicText){
            topicView = mView.findViewById(R.id.txtTopicTitle);
            topicView.setText(topicText);
        }
    }
}
