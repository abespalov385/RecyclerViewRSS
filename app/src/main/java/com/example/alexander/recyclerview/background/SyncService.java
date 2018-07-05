package com.example.alexander.recyclerview.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.alexander.recyclerview.R;
import com.example.alexander.recyclerview.activities.NewsFeedActivity;
import com.example.alexander.recyclerview.model.News;
import com.example.alexander.recyclerview.utils.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncService extends JobService {

    private static final String CHANNEL_ID = "News channel";
    private ArrayList<News> mItemsList;

    public SyncService() {
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
        createNotificationChannel();
        mItemsList = new ArrayList<News>();
        Log.d("LOG", "OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LOG", "OnStartCommand");
        if (!getFileStreamPath("news.json").exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Parser.parseRssToList(mItemsList);
                    writeToFile(mItemsList);
                }
            }).start();
        } else {
            forceUpdate();
        }
        scheduleJob();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void writeToFile(ArrayList<News> data) {
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
                if (!getFileStreamPath("news.json").exists()) {
                    Parser.parseRssToList(mItemsList);
                    writeToFile(mItemsList);
                }
                if (mItemsList.isEmpty()) {
                    readFromFile();
                }
                News lastItem = mItemsList.get(0);
                Integer size = mItemsList.size();
                // Log.d("LOG", lastItem.getTitle());
                ArrayList<News> tempList = new ArrayList<News>();
                Parser.parseRssToList(tempList);
                // Log.d("LOG", tempList.get(0).getTitle());
                for (int i = 0; i < tempList.size(); i++) {
                    if (!tempList.get(i).getTitle().equals(lastItem.getTitle())) {
                        mItemsList.add(0, tempList.get(i));
                        // Log.d("LOG", Integer.toString(itemsList.size()));
                    } else break;
                }
                if (size != mItemsList.size()) {
                    Collections.sort(mItemsList, new Comparator<News>() {
                        @Override
                        public int compare(News lhs, News rhs) {
                            return rhs.getPubDate().compareTo(lhs.getPubDate());
                        }
                    });
                    writeToFile(mItemsList);
                    if (!NewsFeedActivity.sIsActive) {
                        sendNotif();
                    }
                    Log.d("LOG", "UPDATED");
                }
                Log.d("LOG", "CHECKED");
            }
        }).start();
    }

    public void forceUpdate() {
        if (checkConnection()) {
            checkUpdates();
        }
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
                mItemsList.add(new News(card.getString("title"),
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
                new ComponentName(this, SyncService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(180 * 1000)
                .setOverrideDeadline(300 * 1000)
                .build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotif() {
        Intent resultIntent = new Intent(this, NewsFeedActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("News updated")
                .setContentText("Your news list has been updated")
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
    }
}
