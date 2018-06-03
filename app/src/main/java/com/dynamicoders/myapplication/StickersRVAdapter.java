package com.dynamicoders.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daasuu.cat.CountAnimationTextView;

import java.util.Random;

public class StickersRVAdapter extends RecyclerView.Adapter<StickersRVAdapter.ViewHolder> {
    private int[] mDataset;
    private LayoutInflater inflater;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView stickerName;
        public CountAnimationTextView stickerStats;
        public ImageView sticker;

        public ViewHolder(View v) {
            super(v);
            stickerName = v.findViewById(R.id.stickerName);
            stickerStats= v.findViewById(R.id.stickerStats);
            sticker = v.findViewById(R.id.sticker_image_stats);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StickersRVAdapter(Context context, int[] myDataset) {
        mDataset = myDataset;
        inflater = LayoutInflater.from(context);
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public StickersRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        StickersRVAdapter.ViewHolder holder;
        View v = inflater.inflate(R.layout.sticker_stats, parent, false);
        holder = new ViewHolder(v);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = "No. of people who have pledged to \n" + StickerSelectActivity.hm.get(mDataset[position]);
        holder.stickerName.setText(name);
        Drawable d = context.getDrawable(mDataset[position]);
        holder.sticker.setImageDrawable(d);

        Random rand = new Random();

        int  n = rand.nextInt(9999) + 1;

        holder.stickerStats.setAnimationDuration(2000)
                .countAnimation(0, n);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
