package com.example.alexander.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsLoader extends AsyncTaskLoader <ArrayList<Item>> {

    private ArrayList<Item> mData;

    public NewsLoader(Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
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
    public ArrayList<Item> loadInBackground() {
        Log.d("LOG", "Load Start");
        ArrayList<Item> data = new ArrayList<Item>();
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
                data.add(new Item(card.getString("title"),
                        card.getString("description"),
                        card.getString("link"),
                        card.getString("pubDate"),
                        card.getString("img")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void deliverResult(ArrayList<Item> data) {
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
    public void onCanceled(@Nullable ArrayList<Item> data) {
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

    protected void onReleaseResources(ArrayList<Item> data) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, close it here.
    }
}
