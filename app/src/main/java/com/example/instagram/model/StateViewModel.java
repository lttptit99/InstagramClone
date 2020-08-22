package com.example.instagram.model;

import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

public class StateViewModel extends ViewModel {

    public Parcelable parcelable = null;
//    public String test = "";
    public int check = 0;

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
