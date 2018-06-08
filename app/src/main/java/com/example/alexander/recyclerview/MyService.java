package com.example.alexander.recyclerview;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.renderscript.ScriptGroup;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyService extends Service {

    int mStartMode;
    IBinder mBinder;
    boolean mAllowRebind;

    static final int MSG_CONNECTED = 1;
    static final int MSG_UPDATE_LIST = 2;
    static final int MSG_SEND_LIST = 3;
    static final int MSG_ADD_NEWS = 4;

    static final int START_NEWS = 20;
    private Messenger mClient;
    ArrayList<Item> itemsList;



    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECTED:
                    mClient = msg.replyTo;
                    break;
                case MSG_UPDATE_LIST:
                    itemsList.clear();
                    ParseRss task = new ParseRss();
                    task.execute();
                case MSG_ADD_NEWS:
                    addNews(msg.arg1);

                    default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {


        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        itemsList = new ArrayList<Item>();
        //ParseFile task = new ParseFile();
        //task.execute();
        ParseRss task = new ParseRss();
        task.execute();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private class ParseRss extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
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

                    }

                    eventType = xpp.next(); /// move to next element
                }

            }
                catch (IOException e) {
                    Log.e(TAG, "Error", e);
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "Error", e);
                }


            try {
                Message newMsg = Message.obtain(null,
                        MyService.MSG_SEND_LIST, START_NEWS, itemsList.size());
                Bundle b = new Bundle();
                for (int i=0; i<START_NEWS; i++){
                    b.putSerializable("item"+i, itemsList.get(i));
                }
                newMsg.setData(b);
               if(mClient!=null)
                mClient.send(newMsg);

            } catch (RemoteException e) {
                e.printStackTrace();
            }


                return null;
            }
    }

    public void addNews(int newsCount){
        try {
            if(newsCount!=0){
                Message newMsg = Message.obtain(null,
                        MyService.MSG_ADD_NEWS);
                Bundle b = new Bundle();
                for (int i=newsCount; i<newsCount+10; i++){
                    b.putSerializable("item"+i, itemsList.get(i));
                }
                newMsg.setData(b);
                mClient.send(newMsg);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
