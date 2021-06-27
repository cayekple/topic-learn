package com.cayekple.topiclearn;


import java.util.Date;

public class Topic {
    public String user_id, image, topic, topicId;

    public Date timestamp;

    public Topic() {}

    public Topic(String user_id, String image, String topic, Date timestamp, String topicId) {
        this.user_id = user_id;
        this.image = image;
        this.topic = topic;
        this.timestamp = timestamp;
        this.topicId = topicId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTopic() {
        return topic;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTopicIdp(String topicId) {
        this.topicId = topicId;
    }
}
