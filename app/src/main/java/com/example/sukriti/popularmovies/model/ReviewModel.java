package com.example.sukriti.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewModel {

    private String id;
    private String author;
    private String content;

    public ReviewModel() {

    }

    public ReviewModel(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.author = trailer.getString("author");
        this.content = trailer.getString("content");
    }

    public String getId() { return id; }

    public String getAuthor() { return author; }

    public String getContent() { return content; }
}
