package com.example.alexander.recyclerview.utils;

import android.util.JsonWriter;
import android.util.Log;

import com.example.alexander.recyclerview.model.News;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import static android.content.ContentValues.TAG;

/**
 * Class provides static methods that parse RSS feed to ArrayList
 * and write ArrayList to JSON File.
 */
public class Parser {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String PUB_DATE = "pubDate";
    public static final String IMG = "img";
    public static final String FILE = "news.json";
    public static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

    /**
     * Parse RSS feed to ArrayList.
     * @param itemsList result ArrayList
     */
    public static void parseRssToList(ArrayList<News> itemsList) {
        try {
            URL url = new URL("https://lenta.ru/rss");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(url.openConnection().getInputStream(), "UTF-8");
            boolean insideItem = false;
            String title = null;
            String description = null;
            String link = null;
            String pubDate = null;
            String img = null;
            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    // While insideItem = true, parse all needed tags
                    } else if (xpp.getName().equalsIgnoreCase(TITLE)) {
                        if (insideItem) {
                            title = xpp.nextText();
                            //  Log.i("Title: ",xpp.nextText());
                        }
                    } else if (xpp.getName().equalsIgnoreCase(DESCRIPTION)) {
                        if (insideItem) {
                            description = xpp.nextText();
                            //  Log.i("Description: ",xpp.nextText());
                        }
                    } else if (xpp.getName().equalsIgnoreCase(LINK)) {
                        if (insideItem) {
                            link = xpp.nextText();
                            //  Log.i("Description: ",xpp.nextText());
                        }
                    } else if (xpp.getName().equalsIgnoreCase(PUB_DATE)) {
                        if (insideItem) {
                            pubDate = xpp.nextText();
                            //  Log.i("Description: ",xpp.nextText());
                        }
                    } else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                        if (insideItem) {
                            img = xpp.getAttributeValue(null, "url");
                            //  Log.i("ImgUrl ", xpp.getAttributeValue(null, "url"));
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    // When insideItem = false, create new news item based on parsed information
                    insideItem = false;
                    if (img != null) {
                        itemsList.add(new News(title, description, link, pubDate, img));
                        img = null;
                        title = null;
                        description = null;
                        pubDate = null;
                    }
                }
                eventType = xpp.next(); // move to next item element
            }
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error", e);
        }
    }

    /**
     * Write ArrayList of news items to JSON Stream.
     * @param out file output stream
     * @param itemsList ArrayList with news items
     * @throws IOException if JSON file not found
     */
    public static void writeJsonStream(OutputStream out, ArrayList<News> itemsList) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writer.beginObject();
        writer.name("news");
        writeNewsArray(writer, itemsList);
        writer.endObject();
        writer.close();
    }

    /**
     * Write JSON Array.
     * @param writer JSON Writer
     * @param itemsList ArrayList with news items
     * @throws IOException if JSON file not found
     */
    public static void writeNewsArray(JsonWriter writer, ArrayList<News> itemsList) throws IOException {
        writer.beginArray();
        for (int i = 0; i < itemsList.size(); i++) {
            writeNews(writer, itemsList.get(i));
        }
        writer.endArray();
    }

    /**
     * Write single news item to JSON Array.
     * @param writer JSON Writer
     * @param item news item
     * @throws IOException if JSON file not found
     */
    public static void writeNews(JsonWriter writer, News item) throws IOException {
        DateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        writer.beginObject();
        writer.name(TITLE).value(item.getTitle());
        writer.name(DESCRIPTION).value(item.getDescription());
        writer.name(LINK).value(item.getLink());
        writer.name(PUB_DATE).value(formatter.format(item.getPubDate()));
        writer.name(IMG).value(item.getImg());
        writer.endObject();
    }
}
