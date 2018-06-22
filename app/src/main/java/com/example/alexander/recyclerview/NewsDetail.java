package com.example.alexander.recyclerview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class NewsDetail extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();

        title = (TextView)findViewById(R.id.titleDetail);
        description = (TextView)findViewById(R.id.descriptionDetail);
        img = (ImageView)findViewById(R.id.imageViewDetail);

        img.setTransitionName(intent.getStringExtra("Img"));

        title.setText(intent.getStringExtra("Title"));
        description.setText(intent.getStringExtra("Description"));
        Picasso.get().load(intent.getStringExtra("Img")).into(img);
        supportStartPostponedEnterTransition();





    }

}
