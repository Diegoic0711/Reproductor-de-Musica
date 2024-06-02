package com.example.reproductordemusica;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {
    // Componentes de la UI
    ImageButton play_pause, repeat_one; // Botones para reproducir/pausar y repetir
    MediaPlayer mp; // Instancia de MediaPlayer para manejar la reproducción de música
    ImageView iv; // ImageView para mostrar la carátula del álbum
    int repetir = 2, posicion = 0; // Variables de control para la repetición y la posición de la pista
    MediaPlayer vectormp[] = new MediaPlayer[3]; // Array de instancias de MediaPlayer para múltiples pistas
    SeekBar seekBar; // SeekBar para mostrar y controlar la posición de reproducción
    Runnable runnable; // Runnable para actualizar el SeekBar
    Handler handler; // Handler para gestionar las actualizaciones del SeekBar
    View mainLayout; // Vista principal para cambiar dinámicamente el fondo
    TextView song, artist;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajustar la UI para mostrar en pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar los componentes de la UI
        mainLayout = findViewById(R.id.main); // Referencia al layout principal
        play_pause = findViewById(R.id.play_pause); // Botón de reproducir/pausar
        repeat_one = findViewById(R.id.repeat_one); // Botón de repetir
        iv = findViewById(R.id.image_view); // ImageView para la carátula del álbum
        seekBar = findViewById(R.id.seekBar); // SeekBar para la posición de reproducción
        handler = new Handler();
        song = findViewById(R.id.song); //Textview del nombre de la pista o canción
        artist = findViewById(R.id.artist); //Textview del nombre del artista

        // Inicializar los reproductores de medios
        initializeMediaPlayers();
        // Configurar el listener del SeekBar
        setupSeekBar();
        // Configurar el listener de finalización del MediaPlayer
        setupMediaCompletionListener();

        // Configurar el fondo degradado inicial
        updateBackgroundColor(R.drawable.caratula); // Usa la carátula inicial como referencia
    }

    // Inicializar las instancias de MediaPlayer con los recursos de audio
    private void initializeMediaPlayers() {
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
    }

    // Configurar el listener de cambios del SeekBar
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    vectormp[posicion].seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Configurar el listener de finalización del MediaPlayer para manejar las transiciones de pistas
    private void setupMediaCompletionListener() {
        for (int i = 0; i < vectormp.length; i++) {
            final int index = i;
            vectormp[i].setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.seekTo(0);
                if (posicion < vectormp.length - 1) {
                    posicion++;
                } else {
                    posicion = 0;
                }
                updateUI();
                vectormp[posicion].start();
                updateSeekbar();
            });
        }
    }

    // Reproducir o pausar la pista actual
    public void PlayPause(View view) {
        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause();
            play_pause.setBackgroundResource(R.drawable.play_circle);
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(runnable);
        } else {
            vectormp[posicion].start();
            play_pause.setBackgroundResource(R.drawable.pause_circle);
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
            updateSeekbar();
        }
    }

    // Alternar la funcionalidad de repetición para la pista actual
    public void Repetir(View view) {
        if (repetir == 1) {
            repeat_one.setBackgroundResource(R.drawable.repeat);
            Toast.makeText(this, "No repetir", Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(false);
            repetir = 2;
        } else {
            repeat_one.setBackgroundResource(R.drawable.repeat_one);
            Toast.makeText(this, "Repetir", Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(true);
            repetir = 1;
        }
    }

    // Saltar a la siguiente pista
    public void Siguiente(View view) {
        if (posicion < vectormp.length - 1) {
            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop();
            }
            posicion++;
            vectormp[posicion].start();
            updateUI();
            updateSeekbar();
        } else {
            Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
        }
    }

    // Volver a la pista anterior
    public void Anterior(View view) {
        if (posicion >= 1) {
            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop();
                resetMediaPlayers();
            }
            posicion--;
            vectormp[posicion].start();
            updateUI();
            updateSeekbar();
        } else {
            Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
        }
    }

    // Reinicializar las instancias de MediaPlayer (útil al cambiar de pista)
    private void resetMediaPlayers() {
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
    }


    // Actualizar los componentes de la UI en función de la pista actual
    private void updateUI() {
        int imageResId;
        switch (posicion) {
            case 0:
                imageResId = R.drawable.caratula;
                song.setText("Test Track 1");
                artist.setText("Diego Caballero");
                break;
            case 1:
                imageResId = R.drawable.portada_1;
                song.setText("Test Track 2");
                artist.setText("Diego Caballero y alguien más");
                break;
            case 2:
                imageResId = R.drawable.portada_2;
                song.setText("Test Track 3");
                artist.setText("Diego Caballero y sus colegas");
                break;
            default:
                imageResId = R.drawable.caratula;
                song.setText("Test Track DEFAULT");
                artist.setText("Diego Caballero DEFAULT");
                break;
        }
        iv.setImageResource(imageResId);
        updateBackgroundColor(imageResId);
    }


    // Actualizar el color de fondo basado en la carátula del álbum
    private void updateBackgroundColor(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int defaultColor = 0x000000; // Color por defecto en caso de que la paleta falle
                int dominantColor = palette.getDominantColor(defaultColor);
                if (isColorLight(dominantColor)) {
                    dominantColor = palette.getVibrantColor(defaultColor); // Usar un color vibrante si el dominante es muy claro
                }
                if (dominantColor == defaultColor) {
                    dominantColor = palette.getMutedColor(defaultColor); // Usar un color apagado si no hay color vibrante disponible
                }
                int darkenedColor = darkenColor(dominantColor); // Oscurecer el color dominante
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{darkenedColor, dominantColor});
                mainLayout.setBackground(gradientDrawable);
            }
        });
    }



    // Oscurecer un color reduciendo su brillo
    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 20f; // Reducir el brillo para oscurecer el color
        return Color.HSVToColor(hsv);
    }


    // Verificar si un color es claro o oscuro
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }


    // Actualizar el SeekBar periódicamente para reflejar la posición de reproducción
    public void updateSeekbar() {
        seekBar.setMax(vectormp[posicion].getDuration());
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(vectormp[posicion].getCurrentPosition());
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    // Liberar los recursos del MediaPlayer cuando la actividad es destruida
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayer mediaPlayer : vectormp) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
    }
}
