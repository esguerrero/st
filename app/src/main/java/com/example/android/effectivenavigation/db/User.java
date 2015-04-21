package com.example.android.effectivenavigation.db;

import java.util.List;

/**
 * Created by Esteban on 5/22/14.
 */
public class User {

    private int iduser;
    private  String username;
    private  String comment;

    private List<String> activities;





    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public User(int iduser) {
        this.iduser = iduser;
    }

    public User(int iduser, String username) {
        this.iduser = iduser;
        this.username = username;
    }





}
