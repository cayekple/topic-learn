package com.cayekple.topiclearn;


public class Topic {
    public String user_id, image, topic;

    public Topic() {}

    public Topic(String user_id, String image, String topic) {
        this.user_id = user_id;
        this.image = image;
        this.topic = topic;
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

    public void setTopic(String topic) {
        this.topic = topic;
    }


}
