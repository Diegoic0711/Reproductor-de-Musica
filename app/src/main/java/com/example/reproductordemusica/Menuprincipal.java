package com.example.reproductordemusica;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class Menuprincipal extends AppCompatActivity implements FirebaseAdapter.OnItemClickListener {

    private ArrayList<ModelFirebase> modelFirebaseArrayList;
    private ArrayList<ModelFirebase> favoriteSongsList;
    private FirebaseAdapter firebaseAdapter;
    private FavoriteSongsAdapter favoriteSongsAdapter;
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerViewMain;
    private RecyclerView recyclerViewFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuprincipal);

        EdgeToEdge.enable(this);

        // Configurar RecyclerViews
        recyclerViewMain = findViewById(R.id.Lista);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewFavorites = findViewById(R.id.Lista1);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar listas y adaptadores
        modelFirebaseArrayList = new ArrayList<>();
        favoriteSongsList = new ArrayList<>();

        firebaseAdapter = new FirebaseAdapter(modelFirebaseArrayList, this);
        firebaseAdapter.setOnItemClickListener(this);
        recyclerViewMain.setAdapter(firebaseAdapter);

        favoriteSongsAdapter = new FavoriteSongsAdapter(favoriteSongsList, this);
        recyclerViewFavorites.setAdapter(favoriteSongsAdapter);

        // Configurar Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Musica");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelFirebaseArrayList.clear(); // Limpiar lista antes de actualizar
                for (DataSnapshot readFirebase : snapshot.getChildren()) {
                    ModelFirebase itemsFirebase = readFirebase.getValue(ModelFirebase.class);
                    modelFirebaseArrayList.add(itemsFirebase);
                }
                firebaseAdapter.notifyDataSetChanged(); // Notificar cambios en el adaptador
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Menuprincipal.this, "Error al cargar datos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar MediaPlayer
        mediaPlayer = new MediaPlayer();

        // Botón de Favoritos
        Button favoritosButton = findViewById(R.id.favoritas);
        favoritosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFavorites(); // Método para mostrar la vista de favoritos
            }
        });
    }

    @Override
    public void onPlayButtonClick(String songUrl) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Reproduciendo música", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al reproducir música", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showFavorites() {
        // Inflar el layout de favoritos.xml dentro del ConstraintLayout principal
        LayoutInflater inflater = LayoutInflater.from(this);
        View favoritosLayout = inflater.inflate(R.layout.favoritos, findViewById(R.id.main), false);

        // Configurar RecyclerView de favoritos dentro del layout inflado
        RecyclerView recyclerViewFavorites = favoritosLayout.findViewById(R.id.Lista1);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Limpiar y llenar la lista de favoritos
        favoriteSongsList.clear();
        for (ModelFirebase modelFirebase : modelFirebaseArrayList) {
            if (modelFirebase.isFavorite()) {
                favoriteSongsList.add(modelFirebase);
            }
        }

        // Configurar y asignar el adapter de favoritos
        FavoriteSongsAdapter favoriteSongsAdapter = new FavoriteSongsAdapter(favoriteSongsList, this);
        recyclerViewFavorites.setAdapter(favoriteSongsAdapter);

        // Mostrar el layout de favoritos y ocultar el RecyclerView principal
        recyclerViewMain.setVisibility(View.GONE);
        ViewGroup mainLayout = findViewById(R.id.main);
        mainLayout.addView(favoritosLayout); // Agregar el layout inflado al ConstraintLayout principal
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
