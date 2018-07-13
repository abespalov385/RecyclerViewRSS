package com.example.alexander.recyclerview.model;

import com.example.alexander.recyclerview.utils.Parser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Class represent single news item.
 */
public class News implements Serializable {

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
    public News(String mTitle, String mDescription, String mLink, String mDate, String mImg) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mLink = mLink;
        DateFormat formatter = new SimpleDateFormat(Parser.DATE_PATTERN, Locale.US);
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
     * Set link to the full article.
     * @param mURL link to the full article
     */
    public void setLink(String mURL) {
        this.mLink = mURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(mTitle, news.mTitle) &&
                Objects.equals(mDescription, news.mDescription) &&
                Objects.equals(mImg, news.mImg) &&
                Objects.equals(mLink, news.mLink) &&
                Objects.equals(mPubDate, news.mPubDate);
    }

    /**
     * Compare fields of two news items.
     * @param news news item for compare
     * @return true if there are similar fields, false if all fields unique
     */
    public boolean checkDuplicates (News news) {
        if (this.getTitle().equals(news.getTitle()) ||
                this.getDescription().equals(news.getDescription()) ||
                this.getLink().equals(news.getLink()) ||
                this.getPubDate().equals(news.getPubDate()) ||
                this.getImg().equals(news.getImg())) {
            return true;
        } else {
            return false;
        }
    }
}
