package com.example.hamza.social_app;

import java.util.Date;

public class BlogPost extends BlogPostId{
    public String name,image,description,path,uid;
    public Date time;

    public BlogPost(){}

    public BlogPost(String name, String image, String description, String path, String uid, Date time) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.path = path;
        this.uid = uid;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
