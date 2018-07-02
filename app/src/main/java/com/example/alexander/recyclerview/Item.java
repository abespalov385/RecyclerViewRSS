package com.example.alexander.recyclerview;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item implements Serializable{

    private String mTitle;
    private String mDescription;
    private String mImg;
    private String mLink;
    private Date mPubDate;

    public Item(String mTitle, String mDescription, String mLink, String date) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mLink = mLink;
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        try {
            this.mPubDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Item(String mTitle, String mDescription, String mLink, String date , String mImg) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mLink = mLink;
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        try {
            this.mPubDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mImg = mImg;
    }

    public Date getPubDate() {
        return mPubDate;
    }

    public void setPubDate(Date pubDate) {
        this.mPubDate = pubDate;
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

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getImg() {
        return mImg;
    }

    public void setImg(String mImg) {
        this.mImg = mImg;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String mURL) {
        this.mLink = mURL;
    }
}
