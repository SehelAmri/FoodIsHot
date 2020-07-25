package com.example.foodishot.Model;

import android.media.Image;

public class Categories {
    public String Name;
    public int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Categories(String name, int image) {
       this.image = image;
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
