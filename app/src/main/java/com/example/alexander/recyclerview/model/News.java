package com.example.alexander.recyclerview.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class represent single news item.
 */
public class News implements Serializable{

    private String mTitle;
    private String mDescription;
    private String mImg;
    private String mLink;
    private Date mPubDate;

    /**
     * Constructor.
     * @param mTitle news title
     * @param mDescription news description
     * @param mLink link to the full article
     * @param mDate news publication date and time
     * @param mImg link to the news image
     */
    public News(String mTitle, String mDescription, String mLink, String mDate , String mImg) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mLink = mLink;
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        try {
            this.mPubDate = formatter.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mImg = mImg;
    }

    /**
     * Get news publication date.
     * @return news publication date and time
     */
    public Date getPubDate() {
        return mPubDate;
    }

    /**
     * Set news publication date.
     * @param pubDate news publication date
     */
    public void setPubDate(Date pubDate) {
        this.mPubDate = pubDate;
    }

    /**
     * Get news title.
     * @return news title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set news title.
     * @param mTitle news title
     */
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * Get news description.
     * @return news description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set news description.
     * @param mDescription news description
     */
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    /**
     * Get news image.
     * @return news image
     */
    public String getImg() {
        return mImg;
    }

    /**
     * Set news image.
     * @param mImg news image
     */
    public void setImg(String mImg) {
        this.mImg = mImg;
    }

    /**
     * Get link to the full article.
     * @return link to the full article
     */
    public String getLink() {
        return mLink;
    }

    /**
     * Set link to the full article
     * @param mURL link to the full article
     */
    public void setLink(String mURL) {
        this.mLink = mURL;
    }
}
