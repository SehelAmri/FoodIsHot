package com.example.foodishot.Model;

import androidx.annotation.NonNull;

public class AddOnsSpecs {
 private String Obj;
 private String Cash;
 private String Required;
private int NoRequired;
private int Position;
private int Up_to;

    public AddOnsSpecs() {

    }
    public String getObj() {
        return Obj;
    }

    public void setObj(String obj) {
        Obj = obj;

    }

    public String getCash() {
        return Cash;
    }

    public void setCash(String cash) {
        Cash = cash;
    }

    public String getRequired() {
        return Required;
    }

    public void setRequired(String required) {
        Required = required;
    }

    public int getNoRequired() {
        return NoRequired;
    }

    public void setNoRequired(int noRequired) {
        NoRequired = noRequired;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public int getUp_to() {
        return Up_to;
    }

    public void setUp_to(int up_to) {
        Up_to = up_to;
    }

    public AddOnsSpecs(String obj, String cash, String required, int noRequired, int position, int up_to) {
        Obj = obj;
        Cash = cash;
        Required = required;
        NoRequired = noRequired;
        Position = position;
        Up_to = up_to;
    }
}
