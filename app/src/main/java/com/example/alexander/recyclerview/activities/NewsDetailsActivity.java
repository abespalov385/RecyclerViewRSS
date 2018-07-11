package com.example.alexander.recyclerview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        img.setTransitionName(intent.getStringExtra("Img"));
        title.setText(intent.getStringExtra("Title"));
        description.setText(intent.getStringExtra("Description"));
        Picasso.get().load(intent.getStringExtra("Img")).into(img);
        supportStartPostponedEnterTransition();
    }
}
