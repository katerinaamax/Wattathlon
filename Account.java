package com.wattathlon.wattathlon2;

import android.app.Application;
import android.content.Context;

//Class that saves user's info from database (used as global variable).
public class Account extends Application {

    private String email;
    private String password;
    private String name;
    private int height;
    private int weight;
    private int rowFtp;
    private int bikeFtp;
    private int skiFtp;
    private DataBase base;
    private String ergType;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAll(String email, String password, Context context) {
        base = new DataBase(context);

        name = base.getName(email, password);
        height = base.getHeight(email, password);
        weight = base.getWeight(email, password);
        rowFtp = base.getRowFtp(email, password);
        bikeFtp = base.getBikeFtp(email, password);
        skiFtp = base.getSkiFtp(email, password);
        ergType = "";
    }

    public void setErgType(String type) {
        ergType = type;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getRowFtp() {
        return rowFtp;
    }

    public int getBikeFtp() {
        return bikeFtp;
    }

    public int getSkiFtp() {
        return skiFtp;
    }

    public String getErgType() {
        return ergType;
    }
}
