package com.example.alexander.recyclerview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.recyclerview.R;
import com.squareup.picasso.Picasso;

/**
 * Activity that represent single news item.
 */
public class NewsDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        TextView title = (TextView) findViewById(R.id.titleDetail);
        TextView description = (TextView) findViewById(R.id.descriptionDetail);
        ImageView img = (ImageView) findViewById(R.id.imageViewDetail);
        // Getting transition name from intent and set it to imageView
        img.setTransitionName(intent.getStringExtra(NewsFeedActivity.NEWS_IMG));
        title.setText(intent.getStringExtra(NewsFeedActivity.NEWS_TITLE));
        description.setText(intent.getStringExtra(NewsFeedActivity.NEWS_DESCRIPTION));
        Picasso.get().load(intent.getStringExtra(NewsFeedActivity.NEWS_IMG)).into(img);
        supportStartPostponedEnterTransition();
    }
}
