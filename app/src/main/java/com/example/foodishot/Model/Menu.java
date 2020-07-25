package com.example.foodishot.Model;

import java.util.ArrayList;

public class Menu {
   private String Name;
   private String Type;
   private String Contains;
   private String Price;
   private  String Key;
   private String Tab;
    private  String MenuKey;
    public Menu() {
    }


    public String getTab() {
        return Tab;
    }

    public void setTab(String tab) {
        Tab = tab;
    }

    public String getMenuKey() {
        return MenuKey;
    }

    public void setMenuKey(String menuKey) {
        MenuKey = menuKey;
    }

    public Menu(String name, String type, String contains, String price, String key, String tab, String menuKey) {
     Name =name;
     Contains = contains;
     Price = price;
     Type = type;
     Key = key;
     Tab =tab;
     MenuKey = menuKey;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
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

    public String getContains() {
        return Contains;
    }

    public void setContains(String contains) {
        Contains = contains;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

}
