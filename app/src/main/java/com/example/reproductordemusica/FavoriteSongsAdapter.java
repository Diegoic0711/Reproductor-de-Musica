package com.example.reproductordemusica;

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

import java.util.List;

public class FavoriteSongsAdapter extends RecyclerView.Adapter<FavoriteSongsAdapter.FavoriteSongViewHolder> {

    private List<ModelFirebase> favoriteSongsList;
    private Context context;

    public FavoriteSongsAdapter(List<ModelFirebase> favoriteSongsList, Context context) {
        this.favoriteSongsList = favoriteSongsList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist, parent, false);
        return new FavoriteSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteSongViewHolder holder, int position) {
        ModelFirebase song = favoriteSongsList.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return favoriteSongsList.size();
    }

    public class FavoriteSongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView song, artist;

        public FavoriteSongViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.Image);
            song = itemView.findViewById(R.id.song);
            artist = itemView.findViewById(R.id.artist);
        }

        public void bind(ModelFirebase song) {
            this.song.setText(song.getSong());
            this.artist.setText(song.getArtist());
            Glide.with(context).load(song.getCover_image())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(17)))
                    .into(imageView);
        }
    }

    public void updateFavoriteSongs(List<ModelFirebase> newFavoriteSongsList) {
        this.favoriteSongsList = newFavoriteSongsList;
        notifyDataSetChanged();
    }
}
