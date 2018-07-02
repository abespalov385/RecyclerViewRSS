package com.example.alexander.recyclerview;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Item> mItemsList;
    public static Boolean sIsActive = false;
    private BroadcastReceiver mBr;
    private ProgressDialog mProgressDialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks);

        startService(new Intent(this, NewsService.class));
        mItemsList = new ArrayList<Item>();
        mLayoutManager = new LinearLayoutManager(this);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().getLoader(0).forceLoad();
                mSwipeRefresh.setRefreshing(false);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mAdapter.setClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, ImageView img) {
                // Log.d("LOG", "Title " + mItemsList.get(position).getTitle() +
                // ", Description " + mItemsList.get(position).getDescription());
                Intent intent = new Intent(MainActivity.this, NewsDetail.class);
                intent.putExtra("Title", mItemsList.get(position).getTitle());
                intent.putExtra("Description", mItemsList.get(position).getDescription());
                intent.putExtra("Img", mItemsList.get(position).getImg());
                Log.d("LOG", img.getTransitionName());
                Pair imgPair = Pair.create(img, ViewCompat.getTransitionName(img));
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imgPair);
                startActivity(intent, options.toBundle());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getSupportLoaderManager().getLoader(0).forceLoad();
            }
        };
        registerReceiver(mBr, new IntentFilter("com.example.alexander.recyclerview.NOTIFICATION"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBr);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sIsActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        sIsActive = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    public void showProgressDialog(String dialog) {
        if (mProgressDialogLoading == null) {
            mProgressDialogLoading = new ProgressDialog(this);
            mProgressDialogLoading.setMessage(dialog);
            mProgressDialogLoading.setIndeterminate(true);
            mProgressDialogLoading.setCancelable(false);
        }
        mProgressDialogLoading.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialogLoading != null && mProgressDialogLoading.isShowing()) {
            mProgressDialogLoading.dismiss();
        }
    }

    private LoaderManager.LoaderCallbacks<ArrayList<Item>>
            mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<ArrayList<Item>>() {
                @Override
                public Loader<ArrayList<Item>> onCreateLoader(int id, Bundle args) {
                    showProgressDialog("Loading");
                    return new NewsLoader(MainActivity.this);
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<Item>> loader, ArrayList<Item> data) {
                    if (mAdapter.getItemCount() != data.size()) {
                        hideProgressDialog();
                        mAdapter.setData(data);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Item>> loader) {
                }
            };

    public Boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
