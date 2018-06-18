package com.example.alexander.recyclerview;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Parser {

    public static ArrayList<Item> parseRss(ArrayList<Item> itemsList){

        try{

            URL url = new URL("https://lenta.ru/rss");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(url.openConnection().getInputStream(), "UTF-8");


            boolean insideItem = false;

            String title = null;
            String description = null;
            String img = null;

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            title = xpp.nextText();
                        //Log.i("Title: ",xpp.nextText());
                    }
                    else if (xpp.getName().equalsIgnoreCase("description")) {
                        if (insideItem)
                            description = xpp.nextText();
                        //Log.i("Description: ",xpp.nextText());
                    }
                    else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                        if (insideItem) {
                            img = xpp.getAttributeValue(null, "url");
                            //Log.i("ImgUrl ", xpp.getAttributeValue(null, "url"));
                        }
                        }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                    if (img!=null){
                        itemsList.add(new Item(title,description,img));
                        img = null;
                        }
                        else{
                        itemsList.add(new Item(title,description));
                        }
                        title = null;
                    description = null;
                        }eventType = xpp.next(); /// move to next element
            }

            }
            catch (IOException e) {
            Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
            Log.e(TAG, "Error", e);
            }
    return itemsList;
            }
}

