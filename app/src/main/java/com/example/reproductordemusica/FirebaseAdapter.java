package com.example.reproductordemusica;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class FirebaseAdapter extends RecyclerView.Adapter<FirebaseAdapter.ViewHolder> {
    private final ArrayList<ModelFirebase> modelFirebaseArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onPlayButtonClick(String songUrl);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public FirebaseAdapter(ArrayList<ModelFirebase> modelFirebaseArrayList, Context context) {
        this.modelFirebaseArrayList = modelFirebaseArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView")
    int position) {
        ModelFirebase modelFirebase = modelFirebaseArrayList.get(position);

        holder.song.setText(modelFirebase.getSong());
        holder.artist.setText(modelFirebase.getArtist());
        Glide.with(context)
                .load(modelFirebase.getCover_image())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(17)))
                .into(holder.imageView);


        holder.playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onPlayButtonClick(modelFirebase.getUrl());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelFirebaseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView song, artist;
        private final ImageView playbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.Image);
            song = itemView.findViewById(R.id.song);
            artist = itemView.findViewById(R.id.artist);
            playbtn = itemView.findViewById(R.id.playbtn);
        }
    }
}
