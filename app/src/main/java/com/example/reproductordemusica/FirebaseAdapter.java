package com.example.reproductordemusica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class FirebaseAdapter extends RecyclerView.Adapter<FirebaseAdapter.ViewHolder> {
    private final ArrayList<ModelFirebase> modelFirebaseArrayList;
    private final ArrayList<ModelFirebase> favoriteSongsList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onPlayButtonClick(String songUrl);
    }

    public ArrayList<ModelFirebase> getFavoriteSongsList() {
        return favoriteSongsList;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public FirebaseAdapter(ArrayList<ModelFirebase> modelFirebaseArrayList, Context context) {
        this.modelFirebaseArrayList = modelFirebaseArrayList;
        this.favoriteSongsList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelFirebase modelFirebase = modelFirebaseArrayList.get(position);

        holder.song.setText(modelFirebase.getSong());
        holder.artist.setText(modelFirebase.getArtist());
        Glide.with(context)
                .load(modelFirebase.getCover_image())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(17)))
                .into(holder.imageView);

        // Verificar si la canción está marcada como favorita y actualizar el icono correspondientemente
        if (modelFirebase.isFavorite()) {
            holder.favoriteButton.setImageResource(R.drawable.favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.none_favorite);
        }

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invertir el estado de favorito y actualizar la interfaz
                modelFirebase.setFavorite(!modelFirebase.isFavorite());
                notifyItemChanged(position);

                // Mostrar mensaje de Toast según el estado actual
                if (modelFirebase.isFavorite()) {
                    Toast.makeText(context, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        private final ImageView favoriteButton, playbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.Image);
            song = itemView.findViewById(R.id.song);
            artist = itemView.findViewById(R.id.artist);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            playbtn = itemView.findViewById(R.id.playbtn);
        }
    }
}
