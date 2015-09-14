package com.example.sukriti.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

    public class TrailerModel {

        private String id;
        private String key;
        private String name;

        public TrailerModel() {

        }

        public TrailerModel(JSONObject trailer) throws JSONException {
            this.id = trailer.getString("id");
            this.key = trailer.getString("key");
            this.name = trailer.getString("name");

        }

        public String getId() {
            return id;
        }

        public String getKey() { return key; }

        public String getName() { return name; }

    }

