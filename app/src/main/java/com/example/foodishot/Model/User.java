package com.example.foodishot.Model;

public class User {

    private String Email,Password,Name;


    public User(String name,String email, String password) {
        Email = email;
        Password = password;
        Name = name;
    }
    public User(){

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
