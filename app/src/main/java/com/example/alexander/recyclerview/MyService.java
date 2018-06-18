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

import android.util.Log;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;


import java.net.URL;
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
                    parseRss();
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
        parseRss();

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

    public void parseRss(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Parser.parseRss(itemsList);
                try {
                    Message newMsg = Message.obtain(null,
                            MyService.MSG_SEND_LIST, START_NEWS, itemsList.size());
                    Bundle b = new Bundle();
                    for (int i = 0; i < START_NEWS; i++){
                        b.putSerializable("item" + i, itemsList.get(i));
                    }
                    newMsg.setData(b);
                    if(mClient != null)
                        mClient.send(newMsg);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void addNews(int newsCount){
        try {
            if(newsCount != 0){
                Message newMsg = Message.obtain(null,
                        MyService.MSG_ADD_NEWS);
                Bundle b = new Bundle();
                for (int i = newsCount; i < newsCount+10; i++){
                    b.putSerializable("item" + i, itemsList.get(i));
                }
                newMsg.setData(b);
                mClient.send(newMsg);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}

