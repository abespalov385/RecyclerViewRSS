package com.example.alexander.recyclerview;


import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Item> dataSet;
    private OnItemClickListener listener;

    public MyAdapter(List<Item> itemsList) {
        this.dataSet = itemsList;
    }

    public interface OnItemClickListener {
         void onClick(View view, int position, ImageView image);
    }
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void setData(ArrayList<Item> data) {
        this.dataSet.clear();
        if (data != null) {
            this.dataSet.addAll(data);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(dataSet.get(position).getTitle());
        holder.mDescription.setText(dataSet.get(position).getDescription());
        if(dataSet.get(position).getImg() != null)
            Picasso.get().load(dataSet.get(position).getImg()).into(holder.mImage);
        else {
            holder.mImage.setImageBitmap(null);
        }
        ViewCompat.setTransitionName(holder.mImage, dataSet.get(position).getImg());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitle;
        private TextView mDescription;
        private CardView mCv;
        private ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mCv = (CardView)itemView.findViewById(R.id.card_view);
            mDescription = (TextView)itemView.findViewById(R.id.description);
            mTitle = (TextView)itemView.findViewById(R.id.title);
            mImage = (ImageView)itemView.findViewById(R.id.image);
            mCv.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onClick(v, getAdapterPosition(), mImage);
        }
    }

}
