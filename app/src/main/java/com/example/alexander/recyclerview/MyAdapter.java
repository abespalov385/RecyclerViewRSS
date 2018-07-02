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
import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Item> mDataSet;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
         void onClick(View view, int position, ImageView image);
    }
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void setData(ArrayList<Item> data) {
        this.mDataSet.clear();
        if (data != null) {
            this.mDataSet.addAll(data);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(mDataSet.get(position).getTitle());
        holder.mDescription.setText(mDataSet.get(position).getDescription());
        if(mDataSet.get(position).getImg() != null) {
            Picasso.get().load(mDataSet.get(position).getImg()).into(holder.mImage);
        } else {
            holder.mImage.setImageBitmap(null);
        }
        ViewCompat.setTransitionName(holder.mImage, mDataSet.get(position).getImg());
    }



    @Override
    public int getItemCount() {
        return mDataSet.size();
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
            if (mListener != null)
                mListener.onClick(v, getAdapterPosition(), mImage);
        }
    }
}
