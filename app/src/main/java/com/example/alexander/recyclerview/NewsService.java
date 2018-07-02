package com.example.alexander.recyclerview;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsService extends JobService {

    private ArrayList<Item> mItemsList;

    public NewsService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (checkConnection()) {
            checkUpdates();
        }
        jobFinished(params, true);
        scheduleJob();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mItemsList = new ArrayList<Item>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!getFileStreamPath("news.json").exists()) {
                    Parser.parseRssToList(mItemsList);
                    writeToFile(mItemsList);
                }
            }
        }).start();
        Log.d("LOG", "OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LOG", "OnStartCommand");
        scheduleJob();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void writeToFile(ArrayList<Item> data) {
        try {
            if (checkConnection()) {
                OutputStream outputStream = openFileOutput("news.json", Context.MODE_PRIVATE);
                Parser.writeJsonStream(outputStream, data);
                outputStream.close();
                sendBroadcastReady();
            }
                // Log.d("LOG", this.getFilesDir().getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkUpdates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mItemsList.isEmpty()) {
                    readFromFile();
                }
                Item lastItem = mItemsList.get(0);
                Integer size = mItemsList.size();
                // Log.d("LOG", lastItem.getTitle());
                ArrayList<Item> tempList = new ArrayList<Item>();
                Parser.parseRssToList(tempList);
                // Log.d("LOG", tempList.get(0).getTitle());
                for (int i = 0; i < tempList.size(); i++) {
                    if (!tempList.get(i).getTitle().equals(lastItem.getTitle())) {
                        mItemsList.add(0, tempList.get(i));
                        // Log.d("LOG", Integer.toString(itemsList.size()));
                    } else break;
                }
                if (size != mItemsList.size()) {
                    Collections.sort(mItemsList, new Comparator<Item>() {
                        @Override
                        public int compare(Item lhs, Item rhs) {
                            return rhs.getPubDate().compareTo(lhs.getPubDate());
                        }
                    });
                    writeToFile(mItemsList);
                    sendBroadcastUpdate();
                    Log.d("LOG", "UPDATED");
                }
                Log.d("LOG", "CHECKED");
            }
        }).start();
    }

    public void readFromFile() {
        String json = null;
        try {
            InputStream inputStream = openFileInput("news.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray news = obj.getJSONArray("news");
            for (int i = 0; i < news.length(); i++) {
                JSONObject card = news.getJSONObject(i);
                mItemsList.add(new Item(card.getString("title"),
                        card.getString("description"),
                        card.getString("link"),
                        card.getString("pubDate"),
                        card.getString("img")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcastUpdate() {
        Intent intent = new Intent();
        intent.setAction("com.example.alexander.recyclerview.NOTIFICATION");
        sendBroadcast(intent);
    }

    public void sendBroadcastReady() {
        Intent intent = new Intent();
        intent.setAction("com.example.alexander.recyclerview.LISTREADY");
        sendBroadcast(intent);
        Log.d("LOG", intent.getAction());
    }

    public Boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void scheduleJob() {
        JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(1,
                new ComponentName(this, NewsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(30 * 1000)
                .setOverrideDeadline(60 * 1000)
                .build());
    }
}
