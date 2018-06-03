package com.dynamicoders.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.cat.CountAnimationTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mzelzoghbi.zgallery.ZGrid;
import com.mzelzoghbi.zgallery.entities.ZColor;

import java.util.ArrayList;
import java.util.Random;

public class StickersRVAdapter extends RecyclerView.Adapter<StickersRVAdapter.ViewHolder> {
    private int[] mDataset;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> urls = new ArrayList<>();


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
    public void onBindViewHolder(ViewHolder holder, final int position) {
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

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Recycle Click" + position, Toast.LENGTH_SHORT).show();
//                //Start Activity here
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//
//                storageRef.child("test1.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        // Got the download URL for 'users/me/profile.png'
//                        urls.add("https://firebasestorage.googleapis.com/"+uri.getPath()+"?alt=media");
//                        Log.d("url",uri.getPath());
//                        urls.add("http://static0.passel.co/wp-content/uploads/2016/08/05110349/20160731-igor-trepeshchenok-barnimages-08-768x509.jpg");
//                        ZGrid.with(((MainActivity)context), urls)
//                                .setToolbarColorResId(R.color.colorPrimary) // toolbar color
//                                .setTitle("Zak Gallery") // toolbar title
//                                .setToolbarTitleColor(ZColor.WHITE) // toolbar title color
//                                .setSpanCount(3) // colums count
//                                .setGridImgPlaceHolder(R.color.colorPrimary) // color placeholder for the grid image until it loads
//                                .show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                    }
//                });
//
//
//            }
//        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
