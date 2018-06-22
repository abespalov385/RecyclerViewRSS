package com.example.alexander.recyclerview;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Item> itemsList;
    public static Boolean isActive = false;
    private BroadcastReceiver br;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks);
        showProgressDialog();

        itemsList = new ArrayList<Item>();
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().getLoader(0).forceLoad();
                mSwipeRefresh.setRefreshing(false);

            }
        });
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {

                }
            }
        });
        mAdapter = new MyAdapter(itemsList);
        mAdapter.setClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, ImageView img) {
                //Log.d("LOG", "Title " + itemsList.get(position).getTitle() + ", Description " +itemsList.get(position).getDescription());
                Intent intent = new Intent(MainActivity.this, NewsDetail.class);
                intent.putExtra("Title", itemsList.get(position).getTitle());
                intent.putExtra("Description", itemsList.get(position).getDescription());
                intent.putExtra("Img", itemsList.get(position).getImg());
                Log.d("LOG", img.getTransitionName());
                Pair imgPair = Pair.create(img, ViewCompat.getTransitionName(img));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imgPair);
                startActivity(intent, options.toBundle());

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        startService(new Intent(this, NewsService.class));
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getSupportLoaderManager().getLoader(0).forceLoad();
            }
        };
        registerReceiver(br, new IntentFilter("com.example.alexander.recyclerview.NOTIFICATION"));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();

    }

    @Override
    protected void onStart(){
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        isActive = false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }

    }


    private LoaderManager.LoaderCallbacks<ArrayList<Item>>
            mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<ArrayList<Item>>() {

                @Override
                public Loader<ArrayList<Item>> onCreateLoader(
                        int id, Bundle args) {
                    return new NewsLoader(MainActivity.this);
                }

                @Override
                public void onLoadFinished(
                        Loader<ArrayList<Item>> loader, ArrayList<Item> data) {
                        if (mAdapter.getItemCount() != data.size()){
                            hideProgressDialog();
                            mAdapter.setData(data);
                            mAdapter.notifyDataSetChanged();

                        }
                }


                @Override
                public void onLoaderReset(Loader<ArrayList<Item>> loader) {
                }
            };

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }




}
