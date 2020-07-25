package com.example.foodishot.Model;

public class AddOns {
    String Key;
    String Required;
    int Up_to;
    public AddOns() {
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }


    public String getRequired() {
        return Required;
    }

    public void setRequired(String required) {
        Required = required;
    }

    public int getUp_to() {
        return Up_to;
    }

    public void setUp_to(int up_to) {
        Up_to = up_to;
    }

    public AddOns(String key, String required, int up_to) {
        Key = key;
        Required = required;
        Up_to = up_to;
    }
}
