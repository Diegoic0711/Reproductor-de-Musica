package com.example.reproductordemusica;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        FirebaseAdapter.OnItemClickListener {

    private ArrayList<ModelFirebase> modelFirebaseArrayList;
    private FirebaseAdapter firebaseAdapter;
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuprincipal);

        EdgeToEdge.enable(this);

        // Configurar RecyclerViews
        recyclerViewMain = findViewById(R.id.Lista);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));


        // Inicializar listas y adaptadores
        modelFirebaseArrayList = new ArrayList<>();

        firebaseAdapter = new FirebaseAdapter(modelFirebaseArrayList, this);
        firebaseAdapter.setOnItemClickListener(this);
        recyclerViewMain.setAdapter(firebaseAdapter);


        // Configurar Firebase
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Musica");
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
                Toast.makeText(MainActivity.this,
                        "Error al cargar datos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar MediaPlayer
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onPlayButtonClick(String songUrl) {
        Intent intent = new Intent(this, Reproductor.class);
        intent.putExtra("url", songUrl); // Pasar la URL de la canción al intent
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
