package com.example.alexander.recyclerview.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.recyclerview.R;
import com.squareup.picasso.Picasso;

public class NewsDetailsActivity extends AppCompatActivity {

    private TextView mTitle;
    private TextView mDescription;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        mTitle = (TextView) findViewById(R.id.titleDetail);
        mDescription = (TextView) findViewById(R.id.descriptionDetail);
        mImg = (ImageView) findViewById(R.id.imageViewDetail);
        mImg.setTransitionName(intent.getStringExtra("Img"));
        mTitle.setText(intent.getStringExtra("Title"));
        mDescription.setText(intent.getStringExtra("Description"));
        Picasso.get().load(intent.getStringExtra("Img")).into(mImg);
        supportStartPostponedEnterTransition();
    }
}
