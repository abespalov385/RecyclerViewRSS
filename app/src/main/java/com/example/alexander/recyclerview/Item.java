package com.example.alexander.recyclerview;



import java.io.Serializable;


public class Item implements Serializable{
    private String mTitle;
    private String mDescription;
    private String mImg;
    private static final long serialVersionUID = 1L;

    public Item(String mTitle, String mDescription) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
    }
    public Item(String mTitle, String mDescription, String mImg) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mImg = mImg;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmImg() {
        return mImg;
    }

    public void setmImg(String mImg) {
        this.mImg = mImg;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;

    }


}
