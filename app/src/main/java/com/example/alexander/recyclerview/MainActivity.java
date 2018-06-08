package com.example.alexander.recyclerview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Item> itemsList;

    private int newsCount;

    Messenger mService = null;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    boolean mBound;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyService.MSG_SEND_LIST:
                    loadList(msg);
                    Log.d("LOG", Integer.toString(msg.arg2));
                    break;
                case MyService.MSG_ADD_NEWS:
                    updateList(msg);
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mService = new Messenger(service);
            mBound = true;
            Message msg = Message.obtain(null,
                    MyService.MSG_CONNECTED);
            msg.replyTo = mMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsList = new ArrayList<Item>();

        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemsList.clear();
                Message msg = Message.obtain(null, MyService.MSG_UPDATE_LIST);
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
                if (!recyclerView.canScrollVertically(1) && newsCount<200) {
                    Message msg = Message.obtain(null, MyService.MSG_ADD_NEWS, newsCount, 0);
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mAdapter = new MyAdapter(itemsList);
        mRecyclerView.setAdapter(mAdapter);

        // Bind to the service
        bindService(new Intent(this, MyService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mBound = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
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

    public void loadList(Message msg){

        for (int i=0; i < msg.arg1; i++){
            itemsList.add((Item) msg.getData().getSerializable("item"+i));
        }
        newsCount = itemsList.size();
        mAdapter.notifyDataSetChanged();
    }

    public void updateList(Message msg){
        for (int i=newsCount; i<newsCount+10; i++){
            itemsList.add((Item) msg.getData().getSerializable("item"+i));

        }
        newsCount = itemsList.size();
        mAdapter.notifyDataSetChanged();

    }


}
