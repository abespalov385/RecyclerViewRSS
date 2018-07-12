package com.example.alexander.recyclerview.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alexander.recyclerview.R;

import com.example.alexander.recyclerview.background.NewsLoader;
import com.example.alexander.recyclerview.background.SyncService;
import com.example.alexander.recyclerview.model.News;
import com.example.alexander.recyclerview.utils.NewsFeedAdapter;

import java.util.ArrayList;

/**
 * Main application activity.
 * Contains RecyclerView with loaded data from JSON File
 * and allow to open single news item in another activity.
 */
public class NewsFeedActivity extends AppCompatActivity {

    public static final String NEWS_TITLE = "Title";
    public static final String NEWS_DESCRIPTION = "Description";
    public static final String NEWS_IMG = "Img";

    private SwipeRefreshLayout mSwipeRefresh;
    private NewsFeedAdapter mAdapter;
    private ArrayList<News> mItemsList;
    private BroadcastReceiver mBr;
    private ProgressDialog mProgressDialogLoading;
    private SharedPreferences mSharedPrefs;
    private Loader mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoader = getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks);
        startService(new Intent(this, SyncService.class));
        // Get SharedPrefs to load current filter
        mSharedPrefs = getSharedPreferences("Filter", Context.MODE_PRIVATE);
        // Show current filter
        Toast.makeText(this, mSharedPrefs.getString("Filter", NewsLoader.LAST_3_HOURS),
                Toast.LENGTH_SHORT).show();
        mItemsList = new ArrayList<News>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Trigger service forceUpdate() method
                startService(new Intent(NewsFeedActivity.this, SyncService.class));
                mSwipeRefresh.setRefreshing(false);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new NewsFeedAdapter();
        mAdapter.setItems(mItemsList);
        mAdapter.setClickListener(new NewsFeedAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, ImageView img) {
                Intent intent = new Intent(NewsFeedActivity.this, NewsDetailsActivity.class);
                // Putting extras to intent to restore it in new activity
                intent.putExtra(NEWS_TITLE, mItemsList.get(position).getTitle());
                intent.putExtra(NEWS_DESCRIPTION, mItemsList.get(position).getDescription());
                intent.putExtra(NEWS_IMG, mItemsList.get(position).getImg());
                Log.d("LOG", img.getTransitionName());
                // Set image as shared element in transition animation
                Pair imgPair = Pair.create(img, ViewCompat.getTransitionName(img));
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(NewsFeedActivity.this, imgPair);
                startActivity(intent, options.toBundle());
            }
        });
        recyclerView.setAdapter(mAdapter);
        mBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("LOG", intent.getAction());
                // Trigger updating loader when receive broadcast from service
                mLoader.onContentChanged();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(SyncService.LIST_READY);
        // Don't use LocalBroadcastReciever because service working in other process
        registerReceiver(mBr, filter);
    }

    @Override
    protected void onDestroy() {
        // Unregister receiver to prevent leaking out of the activity context
        unregisterReceiver(mBr);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Handle configuration change to avoid destroying and recreating activity when screen rotate
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        switch (item.getItemId()) {
            case R.id.last_hour:
                Log.d("LOG", NewsLoader.LAST_HOUR);
                editor.putString("Filter", NewsLoader.LAST_HOUR);
                // Save filter to SharedPrefs to restore it when open app again
                editor.apply();
                // Reload data depends on selected filter
                mLoader.onContentChanged();
                Toast.makeText(this, NewsLoader.LAST_HOUR, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.last_3_hours:
                Log.d("LOG", NewsLoader.LAST_3_HOURS);
                editor.putString("Filter", NewsLoader.LAST_3_HOURS);
                editor.apply();
                mLoader.onContentChanged();
                Toast.makeText(this, NewsLoader.LAST_3_HOURS, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.this_day:
                Log.d("LOG", NewsLoader.THIS_DAY);
                editor.putString("Filter", NewsLoader.THIS_DAY);
                editor.apply();
                mLoader.onContentChanged();
                Toast.makeText(this, NewsLoader.THIS_DAY, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.all:
                Log.d("LOG", NewsLoader.ALL);
                editor.putString("Filter", NewsLoader.ALL);
                editor.apply();
                mLoader.onContentChanged();
                Toast.makeText(this, NewsLoader.ALL, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show progress dialog when list is loading.
     */
    private void showProgressDialog() {
        if (mProgressDialogLoading == null) {
            mProgressDialogLoading = new ProgressDialog(this);
            mProgressDialogLoading.setMessage("Loading");
            mProgressDialogLoading.setIndeterminate(true);
            mProgressDialogLoading.setCancelable(false);
        }
        mProgressDialogLoading.show();
    }

    /**
     * Hide progress dialog if it's showing.
     */
    private void hideProgressDialog() {
        if (mProgressDialogLoading != null && mProgressDialogLoading.isShowing()) {
            mProgressDialogLoading.dismiss();
        }
    }

    private LoaderManager.LoaderCallbacks<ArrayList<News>>
            mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<ArrayList<News>>() {
                @Override
                public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {
                    // Show progress dialog while data is loading
                    showProgressDialog();
                    return new NewsLoader(NewsFeedActivity.this);
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> data) {
                    // Update adapter data if new items loaded
                    if (mAdapter.getItemCount() != data.size()) {
                        hideProgressDialog();
                        mAdapter.setData(data);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<News>> loader) {
                    mAdapter.setData(null);
                }
            };
}
