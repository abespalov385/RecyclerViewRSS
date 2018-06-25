package com.example.alexander.recyclerview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NewsDetail extends AppCompatActivity {

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
