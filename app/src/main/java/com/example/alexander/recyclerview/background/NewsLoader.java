package com.example.alexander.recyclerview.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.alexander.recyclerview.R;
import com.example.alexander.recyclerview.model.News;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsLoader extends AsyncTaskLoader <ArrayList<News>> {

    private ArrayList<News> mData;
    private SharedPreferences mSharedPrefs;
    private Resources mRes;
    private static final long DELTA_ONE_HOUR = 1000 * 60 * 60;
    private static final long DELTA_THREE_HOURS = 1000 * 60 * 60 * 3;
    private static final long DELTA_ONE_DAY = 1000 * 60 * 60 * 24;

    public NewsLoader(Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
        mSharedPrefs = getContext().getSharedPreferences("Filter", Context.MODE_PRIVATE);
        mRes = getContext().getResources();
        if(mData != null) {
            deliverResult(mData);
        } else {
            if (getContext().getFileStreamPath("news.json").exists()) {
                forceLoad();
            }
        }
        super.onStartLoading();
    }

    @Override
    public ArrayList<News> loadInBackground() {
        long delta = 0;
        switch (mSharedPrefs.getString("Filter", mRes.getString(R.string.last_3_hours))) {
            case "Last hour":
                delta = DELTA_ONE_HOUR;
                break;
            case "Last 3 hours":
                delta = DELTA_THREE_HOURS;
                break;
            case "This day":
                delta = DELTA_ONE_DAY;
                break;
        }
        long time = Calendar.getInstance().getTimeInMillis();
        ArrayList<News> data = new ArrayList<News>();
        getContext().getFileStreamPath("news.json");
        String json = null;
        try {
            InputStream inputStream = getContext().openFileInput("news.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.d("LOG", "Not found");
            e.printStackTrace();
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray news = obj.getJSONArray("news");
            for(int i = 0; i < news.length(); i++) {
                JSONObject card = news.getJSONObject(i);
                data.add(new News(card.getString("title"),
                        card.getString("description"),
                        card.getString("link"),
                        card.getString("pubDate"),
                        card.getString("img")));
                if (delta != 0 && data.get(i).getPubDate().getTime() < (time - delta)) break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void deliverResult(ArrayList<News> data) {
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(@Nullable ArrayList<News> data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    protected void onReleaseResources(ArrayList<News> data) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, close it here.
    }
}
