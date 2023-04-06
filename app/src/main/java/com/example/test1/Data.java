package com.example.test1;

public class Data {
    private String id;
    private String activity;

    public Data(String id, String activity) {
        this.id = id;
        this.activity = activity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}