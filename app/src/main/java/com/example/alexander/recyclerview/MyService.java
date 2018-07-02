package com.example.alexander.recyclerview;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import java.util.ArrayList;

public class MyService extends Service {

    private int mStartMode;
    private IBinder mBinder;
    private boolean mAllowRebind;

    static final int MSG_CONNECTED = 1;
    static final int MSG_UPDATE_LIST = 2;
    static final int MSG_SEND_LIST = 3;
    static final int MSG_ADD_NEWS = 4;
    static final int START_NEWS = 20;
    static Messenger sClient;
    static ArrayList<Item> sItemsList;

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECTED:
                    sClient = msg.replyTo;
                    break;
                case MSG_UPDATE_LIST:
                    sItemsList.clear();
                    parseRss();
                    break;
                case MSG_ADD_NEWS:
                    addNews(msg.arg1);
                    break;
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
        sItemsList = new ArrayList<Item>();
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

    public static void parseRss(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Parser.parseRssToList(sItemsList);
                try {
                    Message newMsg = Message.obtain(null,
                            MyService.MSG_SEND_LIST, START_NEWS, sItemsList.size());
                    Bundle b = new Bundle();
                    for (int i = 0; i < START_NEWS; i++){
                        b.putSerializable("item" + i, sItemsList.get(i));
                    }
                    newMsg.setData(b);
                    if(sClient != null){
                        sClient.send(newMsg);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void addNews(int newsCount){
        try {
            if(newsCount != 0){
                Message newMsg = Message.obtain(null,
                        MyService.MSG_ADD_NEWS);
                Bundle b = new Bundle();
                for (int i = newsCount; i < newsCount+10; i++){
                    b.putSerializable("item" + i, sItemsList.get(i));
                }
                newMsg.setData(b);
                sClient.send(newMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

