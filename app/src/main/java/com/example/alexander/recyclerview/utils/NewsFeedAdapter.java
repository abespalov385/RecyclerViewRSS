package com.example.alexander.recyclerview.utils;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.example.alexander.recyclerview.R;
import com.example.alexander.recyclerview.model.News;
import com.squareup.picasso.Picasso;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private List<News> mDataSet;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
         void onClick(View view, int position, ImageView image);
    }

    /**
     * Set item click listener.
     * @param itemClickListener on item click listener
     */
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    /**
     * Set adapter data set.
     * @param data ArrayList with news items
     */
    public void setItems(ArrayList<News> data) {
        this.mDataSet = data;
    }

    @NonNull
    @Override
    public NewsFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Clear ArrayList and insert new data to it.
     * @param data ArrayList with news items
     */
    public void setData(ArrayList<News> data) {
        this.mDataSet.clear();
        if (data != null) {
            this.mDataSet.addAll(data);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsFeedAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(mDataSet.get(position).getTitle());
        holder.mDescription.setText(mDataSet.get(position).getDescription());
        if (mDataSet.get(position).getImg() != null) {
            Picasso.get().load(mDataSet.get(position).getImg()).into(holder.mImage);
        } else {
            holder.mImage.setImageBitmap(null);
        }
        // Set transition name for ViewHolder image that will be used for transition animation
        ViewCompat.setTransitionName(holder.mImage, mDataSet.get(position).getImg());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v, holder.getAdapterPosition(), holder.mImage);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Provide a reference to the views for each data item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDescription;
        private ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView)itemView.findViewById(R.id.description);
            mTitle = (TextView)itemView.findViewById(R.id.title);
            mImage = (ImageView)itemView.findViewById(R.id.image);
        }
    }
}
