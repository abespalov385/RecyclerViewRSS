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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImg() {
        return mImg;
    }

    public void setImg(String mImg) {
        this.mImg = mImg;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;

    }


}
