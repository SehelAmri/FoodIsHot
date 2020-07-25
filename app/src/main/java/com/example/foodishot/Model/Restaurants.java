package com.example.foodishot.Model;

import android.media.Image;

import java.util.ArrayList;

public class Restaurants {
    private float Distance;
    private String Price;
    private String Type;
    private String SubType;
    private String Rating;
    private String NoRating;
    private String Name;
    private String Image;
    private String Longitude;
    private String Latitude;
    private String Open;
    private String Close;
    private String Delivery_time;
    private String Key;
    private String Unavailable;

public  Restaurants(){

}

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getOpen() {
        return Open;
    }

    public void setOpen(String open) {
        Open = open;
    }

    public String getClose() {
        return Close;
    }

    public void setClose(String close) {
        Close = close;
    }

    public String getUnavailable() {
        return Unavailable;
    }

    public void setUnavailable(String unavailable) {
        Unavailable = unavailable;
    }

    public String getSubType() {
        return SubType;
    }

    public void setSubType(String subType) {
        SubType = subType;
    }

    public Restaurants(String unavailable, String open, String close, String price, String type, String subType, String rating, String no_rating, String name, String image, String longitude, String latitude, float distance, String delivery_time, String key){
        Image = image;
        Name = name;
        Price = price;
        Type = type;
        Rating = rating;
        NoRating = no_rating;
        Longitude = longitude;
        Latitude = latitude;
        Distance = distance;
        Delivery_time = delivery_time;
        Key = key;
        Open = open;
        Close = close;
        Unavailable = unavailable;
        SubType = subType;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getNoRating() {
        return NoRating;
    }

    public void setNoRating(String noOfRating) {
        NoRating = noOfRating;
    }

    public String getPrice() {
        return Price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public float getDistance() {
        return Distance;
    }

    public void setDistance(float distance) {
        Distance = distance;
    }

    public String getDelivery_time() {
        return Delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        Delivery_time = delivery_time;
    }

}
