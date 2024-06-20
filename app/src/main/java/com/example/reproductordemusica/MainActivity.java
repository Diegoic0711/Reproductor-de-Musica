package com.example.reproductordemusica;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

public class MainActivity extends AppCompatActivity {
    // Componentes de la UI ... UI components
    ProgressBar progressBar;
    ImageButton play_pause, repeat_one; // Botones para reproducir/pausar y repetir ... Buttoms for play/pause and repeat
    MediaPlayer mp; // Instancia de MediaPlayer para manejar la reproducción de música ... Instance the MediaPlayer to manage the music player
    ImageView iv; // ImageView para mostrar la carátula del álbum ... ImageView to show the album cover
    int repetir = 2, posicion = 0; // Variables de control para la repetición y la posición de la pista ... Control variables for the repetition and position of song
    MediaPlayer vectormp[] = new MediaPlayer[3]; // Array de instancias de MediaPlayer para múltiples pistas ... instance array of MediaPlayer for multiple songs
    SeekBar seekBar; // SeekBar para mostrar y controlar la posición de reproducción ... SeekBar to show and control the reproduction position
    Runnable runnable; // Runnable para actualizar el SeekBar ... Runnable for updating the Seekbar
    Handler handler; // Handler para gestionar las actualizaciones del SeekBar ... Handler for manage the updates of the seekbar
    View mainLayout; // Vista principal para cambiar dinámicamente el fondo ... Main view to change a dinamic background
    TextView song, artist;
    TextView tiempoTranscurridoTextView;
    TextView tiempoRestanteTextView;
    private final String[] audioNames = {"manifiesto", "mas_de_lo_mismo", "tamo_es_pa_gozar"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Ajustar la UI para mostrar en pantalla completa ... Adjust the UI to show it FullScreen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar los componentes de la UI ... Inicialize UI components
        progressBar = findViewById(R.id.progressBar);
        mainLayout = findViewById(R.id.main); // Referencia al layout principal ... Main Layout Reference
        play_pause = findViewById(R.id.play_pause); // Botón de reproducir/pausar ...  Play/Pause Button
        repeat_one = findViewById(R.id.repeat_one); // Botón de repetir ... Repit Button
        iv = findViewById(R.id.image_view); // ImageView para la carátula del álbum ... Album cover ImageView
        seekBar = findViewById(R.id.seekBar); // SeekBar para la posición de reproducción ... SeekBar for the reproduction position
        handler = new Handler();
        song = findViewById(R.id.song); //Textview del nombre de la pista o canción ... Name of the song TextView
        artist = findViewById(R.id.artist); //Textview del nombre del artista ...  Artist name TextView
        tiempoTranscurridoTextView = findViewById(R.id.tiempoTranscurrido);
        tiempoRestanteTextView = findViewById(R.id.tiempoRestante);
        // Inicializar los reproductores de medios ... Initialize Media Reproductors
        initializeMediaPlayers();
        // Configurar el listener del SeekBar ...  config Listener SeekBar
        setupSeekBar();
        // Configurar el listener de finalización del MediaPlayer ... config finalization listener of the MediaPlayer
        setupMediaCompletionListener();
        // Configurar el fondo degradado inicial ... config initial gradient background
        updateBackgroundColor(R.drawable.caratula); // Usa la carátula inicial como referencia ... Use initial cover as reference
    }
    // Inicializar las instancias de MediaPlayer con los recursos de audio ...  Initialize the MediaPlayer instance of Audio resorces
    private void initializeMediaPlayers() {
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
    }
    // Configurar el listener de cambios del SeekBar ... config listener of the SeekBar
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    vectormp[posicion].seekTo(i);
                    if (i == seekBar.getMax()) {
                        progressBar.setVisibility(View.VISIBLE);
                        play_pause.setBackgroundResource(R.drawable.pause_circle);
                    }
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                }, 1000);
            }

        });

        vectormp[posicion].setOnCompletionListener(mp -> {
            progressBar.setVisibility(View.VISIBLE);

            handler.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                Siguiente(null); // Pasar a la siguiente canción después de 2 segundos
            }, 1000);
        });
    }
    // Configurar el listener de finalización del MediaPlayer para manejar las transiciones de pistas ... Config the finalize listener for the Media player so it can handle the song transition
    private void setupMediaCompletionListener() {
        for (int i = 0; i < vectormp.length; i++) {
            final int index = i;
            vectormp[i].setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.seekTo(0);
                mediaPlayer.pause(); // Pausar al final
                play_pause.setBackgroundResource(R.drawable.play_circle);
                // Esperar 2 segundos y luego reproducir la siguiente canción
                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                if (posicion < vectormp.length - 1) {
                    posicion++;
                } else {
                    posicion = 0;
                }
                updateUI();
                   vectormp[posicion].seekTo(0); // Reiniciar la posición de la nueva pista a 0
                updateSeekbar();
                    updateUI();
                    vectormp[posicion].start();
                    play_pause.setBackgroundResource(R.drawable.pause_circle);
                    updateSeekbar();
                }, 3000);
            });
        }
    }

    // Reproducir o pausar la pista actual ...  play or pause the actual song
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
    // Alternar la funcionalidad de repetición para la pista actual ... Alter the fuctionality of repetion for the actual song
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

    // Saltar a la siguiente pista ...  goes to the next song
    public void Siguiente(View view) {

        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause(); // Pausar la pista actual
            handler.removeCallbacks(runnable); // Detener el runnable que actualiza el SeekBar
            play_pause.setBackgroundResource(R.drawable.play_circle);
        }
        // Mostrar la ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {

            progressBar.setVisibility(View.GONE);


            if (posicion < vectormp.length - 1) {
                if (vectormp[posicion].isPlaying()) {
                    vectormp[posicion].stop();
                }
                play_pause.setBackgroundResource(R.drawable.pause_circle);
               DisableRepetir();
                posicion++;
                vectormp[posicion].seekTo(0); // Reiniciar la posición de la nueva pista a 0
                vectormp[posicion].start();
                updateUI();
                updateSeekbar();
            } else {
                Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
                play_pause.setBackgroundResource(R.drawable.play_circle);
            }
        }, 1000); // 1000 milisegundos = 1segundos (simulación de carga)

    }
    private void DisableRepetir(){
        if (repetir == 1) {
            Repetir(null); // Llama a la función Repetir para desactivar el loop y actualizar el ícono
        }
    }


    // Volver a la pista anterior ...  goes back to the previus song
    public void Anterior(View view) {

        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause(); // Pausar la pista actual
            handler.removeCallbacks(runnable); // Detener el runnable que actualiza el SeekBar
            play_pause.setBackgroundResource(R.drawable.play_circle);
        }

        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {

            progressBar.setVisibility(View.GONE);
            if (posicion >= 1) {
                if (vectormp[posicion].isPlaying()) {
                    vectormp[posicion].stop();
                    handler.removeCallbacks(runnable);
                    resetMediaPlayers();
                }
                play_pause.setBackgroundResource(R.drawable.pause_circle);
                if (repetir == 1) {
                    Repetir(null); // Llama a la función Repetir para desactivar el loop y actualizar el ícono
                }
                posicion--;
                vectormp[posicion].seekTo(0); // Reiniciar la posición de la nueva pista a 0
                vectormp[posicion].start();
                updateUI();
                updateSeekbar();
            } else {
                Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
            }
        },1000);
    }

    // Reinicializar las instancias de MediaPlayer (útil al cambiar de pista) ... Reset the Instance of the MediaPlayer
    private void resetMediaPlayers() {
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
    }


    // Actualizar los componentes de la UI en función de la pista actual ... Update the Ui components in fuction of the actual song
    private void updateUI() {
        int imageResId;
        switch (posicion) {
            case 0:
                imageResId = R.drawable.caratula;
                song.setText("Manifiesto");
                artist.setText("La Scene");
                break;
            case 1:
                imageResId = R.drawable.portada_1;
                song.setText("Mas de lo Mismo");
                artist.setText("Entrenos");
                break;
            case 2:
                imageResId = R.drawable.portada_2;
                song.setText("Tamos es pa gozar");
                artist.setText("Mecanik Informal");
                break;
            default:
                imageResId = R.drawable.caratula;
                song.setText("Test Track DEFAULT");
                artist.setText("Ponganos una A");
                break;
        }
        iv.setImageResource(imageResId);
        updateBackgroundColor(imageResId);
    }
    // Actualizar el color de fondo basado en la carátula del álbum ... Changes the colour background base in the cover of the album
    private void updateBackgroundColor(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int defaultColor = 0x000000; // Color por defecto en caso de que la paleta falle ...  Default colour in case the pallete fails
                int dominantColor = palette.getDominantColor(defaultColor);
                if (isColorLight(dominantColor)) {
                    dominantColor = palette.getVibrantColor(defaultColor); // Usar un color vibrante si el dominante es muy claro ...  Uses a vibrant colour if the dominant is too light
                }
                if (dominantColor == defaultColor) {
                    dominantColor = palette.getMutedColor(defaultColor); // Usar un color apagado si no hay color vibrante disponible ... Uses a off colour if there`s not available colour vibrant
                }
                int darkenedColor = darkenColor(dominantColor); // Oscurecer el color dominante ...  Darkest the dominant colour
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{darkenedColor, dominantColor});
                mainLayout.setBackground(gradientDrawable);
            }
        });
    }
    // Oscurecer un color reduciendo su brillo ... darkest a colour Reducing the brightness
    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 20f; // Reducir el brillo para oscurecer el color ...  Reduce brightness of the colour
        return Color.HSVToColor(hsv);
    }
    // Verificar si un color es claro o oscuro ... Verifice if the colour is light or dark
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
    // Actualizar el SeekBar periódicamente para reflejar la posición de reproducción ... Updates SeekBar periodically so it reflec the posicion of reproduction
    public void updateSeekbar() {
        seekBar.setMax(vectormp[posicion].getDuration());
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(vectormp[posicion].getCurrentPosition());
                actualizarTiempo(); // Llama al método para actualizar los TextViews ... Calls the method to update the Textviews
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }
    // Método para formatear el tiempo en minutos y segundos ... Method to Reset the time in minutes and seconds
    private String formatearTiempo(int segundos) {
        int minutos = segundos / 60;
        int segundosRestantes = segundos % 60;
        return String.format("%d:%02d", minutos, segundosRestantes);
    }

    // Método para actualizar los TextViews con el tiempo transcurrido y restante ... Method to Update the TextViews with the elapsed and remaining time
    private void actualizarTiempo() {
        int duracionTotal = vectormp[posicion].getDuration() / 1000;
        int posicionActual = vectormp[posicion].getCurrentPosition() / 1000;

        tiempoTranscurridoTextView.setText(formatearTiempo(posicionActual));
        tiempoRestanteTextView.setText(String.format("%s", formatearTiempo(duracionTotal - posicionActual)));
    }
    // Liberar los recursos del MediaPlayer cuando la actividad es destruida ... Free resorces of the Media player when the activity is destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayer mediaPlayer : vectormp) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
    }
    public void ShowAudioList(View view) {
         // Crear un diálogo AlertDialog con ListView ... Create a dialog AlertDialog with ListView
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lista de Audios");
        builder.setItems(audioNames, (dialog, which) -> {
            // Actualiza la posición actual ... Update the actual position
            if (which != posicion) { // Solo si la selección es diferente a la posición actual... Only if the selection is different of the actual position
                vectormp[posicion].stop();
                handler.removeCallbacks(runnable);
                posicion = which;
                // Reproduce la pista seleccionada ... Reproduce the selected song
                vectormp[posicion].start();
                play_pause.setBackgroundResource(R.drawable.pause_circle);
                updateSeekbar();
                updateUI();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
