package com.example.reproductordemusica;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class Reproductor extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ModelFirebase> modelFirebaseArrayList;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1; // Posición de la canción que se está reproduciendo actualmente
    private ImageButton playPauseButton, skipPreviousButton, skipNextButton, repeatOneButton;
    private ImageView albumCoverImageView;
    private ConstraintLayout backgroundLayout;
    private TextView song, artist, tiempoTranscurridoTextView, tiempoRestanteTextView;
    private SeekBar seekBar;
    private Handler handler;
    private boolean isRepeatOne = false;  // Variable para controlar el estado de repetición
    private Snackbar loadingSnackbar; // Snackbar para mostrar el mensaje de carga
    private static final int LOADING_DURATION = 3000; // Duración de la carga en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar MediaPlayer
        mediaPlayer = new MediaPlayer();



        // Inicializar modelFirebaseArrayList
        modelFirebaseArrayList = new ArrayList<>();

        // Configurar Firebase (asegúrate de que la configuración esté correctamente inicializada)
        FirebaseApp.initializeApp(this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Musica");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelFirebaseArrayList.clear();
                for (DataSnapshot readFirebase : snapshot.getChildren()) {
                    ModelFirebase itemsFirebase = readFirebase.getValue(ModelFirebase.class);
                    modelFirebaseArrayList.add(itemsFirebase);
                }
                // Seleccionar la primera canción de la lista obtenida
                if (!modelFirebaseArrayList.isEmpty()) {
                    currentPlayingPosition = 0; // Seleccionar la primera canción
                    showLoadingSnackbarAndPlay(); // Mostrar Snackbar de carga y reproducir la primera canción
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Reproductor.this, "Error al cargar datos de Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Configurar el botón de reproducción (play_pause)
        playPauseButton = findViewById(R.id.play_pause);
        playPauseButton.setOnClickListener(this);
        skipPreviousButton = findViewById(R.id.skip_previous);
        skipNextButton = findViewById(R.id.skip_next);
        skipPreviousButton.setOnClickListener(this);
        skipNextButton.setOnClickListener(this);

        // Inicializar el ImageView para la carátula del álbum
        albumCoverImageView = findViewById(R.id.image_view);
        song = findViewById(R.id.song);
        artist = findViewById(R.id.artist);
        tiempoTranscurridoTextView = findViewById(R.id.tiempoTranscurrido);
        tiempoRestanteTextView = findViewById(R.id.tiempoRestante);

        // Configuracion del boton repetir
        repeatOneButton = findViewById(R.id.repeat_one);
        repeatOneButton.setOnClickListener(this);

        // Inicializar la vista de fondo
        backgroundLayout = findViewById(R.id.main);

        // Inicializar el SeekBar
        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress); // Adelantar o atrasar la canción según el progreso del SeekBar
                    tiempoTranscurridoTextView.setText(millisecondsToTimer(progress));
                    int totalDuration = mediaPlayer.getDuration();
                    int remainingDuration = totalDuration - progress;
                    tiempoRestanteTextView.setText(millisecondsToTimer(remainingDuration));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No necesario para esta implementación
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No necesario para esta implementación
            }
        });
        // Inicializar el Handler para actualizar el SeekBar
        handler = new Handler();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_pause) {
            if (mediaPlayer.isPlaying()) {
                pauseSong();
            } else {
                mediaPlayer.start();
                // Iniciar el Handler para actualizar el SeekBar
                handler.postDelayed(updateSeekBar, 0);
                updatePlayPauseButtonIcon(true);
            }
        } else if (id == R.id.skip_previous) {
            skipToPreviousSong();
        } else if (id == R.id.skip_next) {
            skipToNextSong();
        } else if (id == R.id.repeat_one) {
            toggleRepeatOne(); // Método para manejar la repetición
        }
    }


    // Método para reproducir una canción
    private void playSong() {
        if (currentPlayingPosition != -1 && currentPlayingPosition < modelFirebaseArrayList.size()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(modelFirebaseArrayList.get(currentPlayingPosition).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();

                // Actualizar el estado de reproducción en el modelo y en la vista
                modelFirebaseArrayList.get(currentPlayingPosition).setPlaying(true);
                updatePlayPauseButtonIcon(true);

                // Cargar la carátula del álbum y cambiar el fondo
                loadAlbumCover(modelFirebaseArrayList.get(currentPlayingPosition).getCover_image());

                // Actualizar los TextViews con el nombre de la canción y el artista
                song.setText(modelFirebaseArrayList.get(currentPlayingPosition).getSong());
                artist.setText(modelFirebaseArrayList.get(currentPlayingPosition).getArtist());

                // Configurar el máximo del SeekBar con la duración de la canción
                seekBar.setMax(mediaPlayer.getDuration());

                // Iniciar el Handler para actualizar el SeekBar
                handler.postDelayed(updateSeekBar, 0);

                // Listener para detectar la finalización de la canción
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Si la repetición está activada, volver a reproducir la misma canción
                        if (isRepeatOne) {
                            mediaPlayer.seekTo(0); // Reiniciar la canción actual
                            mediaPlayer.start();
                        } else {
                            // Pasar a la siguiente canción si hay más canciones disponibles
                            if (currentPlayingPosition < modelFirebaseArrayList.size() - 1) {
                                currentPlayingPosition++;
                                showLoadingSnackbarAndPlay();
                            } else {
                                // Si no hay más canciones, podrías decidir qué hacer aquí
                                Toast.makeText(Reproductor.this, "No hay más canciones, reproduciendo desde el principio", Toast.LENGTH_SHORT).show();
                                currentPlayingPosition = 0; // Volver al principio
                                showLoadingSnackbarAndPlay();
                            }
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay canción seleccionada para reproducir", Toast.LENGTH_SHORT).show();
        }
    }



    // Runnable para actualizar el SeekBar
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);

                // Actualizar tiempo transcurrido
                tiempoTranscurridoTextView.setText(millisecondsToTimer(currentPosition));

                // Actualizar tiempo restante
                int totalDuration = mediaPlayer.getDuration();
                int remainingDuration = totalDuration - currentPosition;
                tiempoRestanteTextView.setText(millisecondsToTimer(remainingDuration));

                // Ejecutar cada 1000 ms (1 segundo)
                handler.postDelayed(this, 1000);
            }
        }
    };



    private String millisecondsToTimer(int milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convertir duración total en segundos
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;

        // Añadir segundos al timer final
        secondsString = (seconds < 10) ? "0" + seconds : "" + seconds;

        // Preparar el timer final en formato mm:ss
        finalTimerString = minutes + ":" + secondsString;

        // Devolver el timer final en formato hh:mm:ss
        return finalTimerString;
    }

    // Método para pausar la reproducción de una canción
    private void pauseSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            // Actualizar el estado de reproducción en el modelo y en la vista
            modelFirebaseArrayList.get(currentPlayingPosition).setPlaying(false);
            updatePlayPauseButtonIcon(false);
            // Detener la actualización del SeekBar mientras está en pausa
            handler.removeCallbacks(updateSeekBar);
        }
    }


    // Método para actualizar el ícono del botón de reproducción
    private void updatePlayPauseButtonIcon(boolean isPlaying) {
        if (isPlaying) {
            playPauseButton.setBackgroundResource(R.drawable.pause_circle); // Cambiar a ícono de pausa
        } else {
            playPauseButton.setBackgroundResource(R.drawable.play_circle); // Cambiar a ícono de reproducción
        }
    }

    // Método para cargar la carátula del álbum en el ImageView y cambiar el fondo
    private void loadAlbumCover(String coverImageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(coverImageUrl)
                .transform(new RoundedCornersTransformation(20, 0)) // Aquí defines el radio de las esquinas redondeadas
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        albumCoverImageView.setImageBitmap(resource);
                        setBackgroundColorFromBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Método requerido, pero no necesario para esta implementación
                    }
                });
    }

    // Método para establecer el color de fondo a partir de un Bitmap
    private void setBackgroundColorFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int defaultColor = ContextCompat.getColor(Reproductor.this, R.color.black);

                // Obtener colores de la paleta
                int dominantColor = palette.getDominantColor(defaultColor);
                int lightMutedColor = palette.getLightMutedColor(defaultColor);
                int darkMutedColor = palette.getDarkMutedColor(defaultColor);
                int vibrantColor = palette.getVibrantColor(defaultColor);

                // Elegir el color secundario más adecuado
                int secondaryColor;
                if (vibrantColor == dominantColor) {
                    secondaryColor = darkMutedColor;
                } else {
                    secondaryColor = vibrantColor;
                }

                // Configurar el fondo con un degradado
                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{dominantColor, secondaryColor}); // Colores para el degradado

                // Asignar el fondo al layout principal
                backgroundLayout.setBackground(gradientDrawable);
            }
        });
    }

    // Método para saltar a la canción anterior
    private void skipToPreviousSong() {
        // Desactivar la repetición si está activada
        if (isRepeatOne) {
            toggleRepeatOne();
        }

        if (currentPlayingPosition > 0) {
            currentPlayingPosition--;
            showLoadingSnackbarAndPlay(); // Mostrar Snackbar de carga y reproducir después
        } else {
            Toast.makeText(this, "No hay canción anterior", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para saltar a la siguiente canción
    private void skipToNextSong() {
        // Desactivar la repetición si está activada
        if (isRepeatOne) {
            toggleRepeatOne();
        }

        if (currentPlayingPosition < modelFirebaseArrayList.size() - 1) {
            currentPlayingPosition++;
            showLoadingSnackbarAndPlay(); // Mostrar Snackbar de carga y reproducir después
        } else {
            Toast.makeText(this, "No hay más canciones, reproduciendo desde el principio", Toast.LENGTH_SHORT).show();
            currentPlayingPosition = 0; // Volver al principio
            showLoadingSnackbarAndPlay(); // Mostrar Snackbar de carga y reproducir después
        }
    }

    // Método para mostrar el Snackbar de carga y reproducir después de un tiempo
    private void showLoadingSnackbarAndPlay() {
        loadingSnackbar = Snackbar.make(findViewById(android.R.id.content), "Cargando...", Snackbar.LENGTH_SHORT);
        loadingSnackbar.show();

        // Usar un Handler para reproducir la canción después de un tiempo
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingSnackbar.dismiss(); // Ocultar el Snackbar de carga
                playSong(); // Reproducir la canción después de mostrar el Snackbar
            }
        }, LOADING_DURATION);
    }

    // Método para mostrar el Snackbar de carga y preparar para reproducir después de un tiempo desde el SeekBar
    private void showLoadingSnackbarAndPlayFromSeekBar(int progress) {
        // Primero, detén la reproducción actual si está en curso
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        // Mostrar el Snackbar de carga solo si no está activada la repetición
        if (!isRepeatOne) {
            loadingSnackbar = Snackbar.make(findViewById(android.R.id.content), "Cargando siguiente canción...", Snackbar.LENGTH_SHORT);
            loadingSnackbar.show();
        }

        // Usar un Handler para preparar la canción después de un tiempo
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Ocultar el Snackbar de carga si no está activada la repetición
                if (!isRepeatOne && loadingSnackbar != null && loadingSnackbar.isShown()) {
                    loadingSnackbar.dismiss();
                }

                if (isRepeatOne) {
                    // Si está activada la repetición, reiniciar la canción actual
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                } else {
                    // Si no está activada la repetición, saltar a la siguiente canción
                    skipToNextSong(); // Saltar a la siguiente canción después de mostrar el Snackbar
                    prepareSong(); // Preparar la canción para reproducción después de pasar el tiempo especificado
                }
            }
        }, LOADING_DURATION); // LOADING_DURATION es el tiempo en milisegundos que defines, en este caso 3000 (3 segundos)
    }



    // Método para preparar la canción para reproducción sin iniciarla automáticamente
    private void prepareSong() {
        if (currentPlayingPosition != -1 && currentPlayingPosition < modelFirebaseArrayList.size()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(modelFirebaseArrayList.get(currentPlayingPosition).getUrl());
                mediaPlayer.prepare();
                // Actualizar la interfaz con la información de la canción nueva
                song.setText(modelFirebaseArrayList.get(currentPlayingPosition).getSong());
                artist.setText(modelFirebaseArrayList.get(currentPlayingPosition).getArtist());
                loadAlbumCover(modelFirebaseArrayList.get(currentPlayingPosition).getCover_image());
                seekBar.setMax(mediaPlayer.getDuration()); // Configurar el máximo del SeekBar con la duración de la canción

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al preparar la canción", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay canción seleccionada para reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para manejar el clic en el botón repeat_one
    private void toggleRepeatOne() {
        isRepeatOne = !isRepeatOne; // Cambiar el estado de repetición

        // Cambiar el icono del botón según el estado de repetición
        if (isRepeatOne) {
            repeatOneButton.setBackgroundResource(R.drawable.repeat_one); // Icono activado de repetición
            Toast.makeText(this, "Repetición activada", Toast.LENGTH_SHORT).show();
        } else {
            repeatOneButton.setBackgroundResource(R.drawable.repeat); // Icono desactivado de repetición
            Toast.makeText(this, "Repetición desactivada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        // Detener el handler de actualización del SeekBar
        handler.removeCallbacks(updateSeekBar);
    }
}
