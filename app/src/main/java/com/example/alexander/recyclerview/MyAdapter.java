package com.example.alexander.recyclerview;


import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    List<Item> dataSet;

    public MyAdapter(List<Item> itemsList) {
        dataSet = itemsList;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(dataSet.get(position).getTitle());
        holder.mDescription.setText(dataSet.get(position).getDescription());
        if(dataSet.get(position).getImg() != null)
            Picasso.get().load(dataSet.get(position).getImg()).into(holder.mImage);
            //new DownloadImageTask(holder.mImage).execute(dataSet.get(position).getImg());
        else {
            holder.mImage.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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


        }
    }
   /* private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }*/
}
