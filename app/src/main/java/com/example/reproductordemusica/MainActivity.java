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

import com.google.firebase.storage.FirebaseStorage;


public class MainActivity extends AppCompatActivity {
    //UI components
    ProgressBar progressBar;
    ImageButton play_pause, repeat_one; // Buttons for play/pause and repeat
    ImageView iv; //ImageView to show the album cover
    int repetir = 2, posicion = 0; // Control variables for the repetition and position of song
    MediaPlayer vectormp[] = new MediaPlayer[3];//Array of MediaPlayer instances for multiple tracks
    SeekBar seekBar; //SeekBar to show and control the reproduction position
    Runnable runnable; //Runnable for updating the Seekbar
    Handler handler; //Handler for manage the updates of the seekbar
    View mainLayout; //Main view to change a dinamic background
    TextView song, artist;
    TextView tiempoTranscurridoTextView;
    TextView tiempoRestanteTextView;
    private final String[] audioNames = {"manifiesto", "mas_de_lo_mismo", "tamo_es_pa_gozar"};

    //Enter firebase storage to the application
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menuprincipal);//Use the main menu as the main screen
        //Adjust the UI to show it FullScreen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Inicialize UI components
        progressBar = findViewById(R.id.progressBar);
        mainLayout = findViewById(R.id.main); //Main Layout Reference
        play_pause = findViewById(R.id.play_pause); //Play/Pause Button
        repeat_one = findViewById(R.id.repeat_one); //Repit Button
        iv = findViewById(R.id.image_view); //Album cover ImageView
        seekBar = findViewById(R.id.seekBar); //SeekBar for the reproduction position
        handler = new Handler();
        song = findViewById(R.id.song); //Name of the song TextView
        artist = findViewById(R.id.artist); //Artist name TextView
        tiempoTranscurridoTextView = findViewById(R.id.tiempoTranscurrido);
        tiempoRestanteTextView = findViewById(R.id.tiempoRestante);
        //Initialize Media Reproductors
        initializeMediaPlayers();
        //config Listener SeekBar
        setupSeekBar();
        //Config finalization listener of the MediaPlayer
        setupMediaCompletionListener();
        //Config initial gradient background
        updateBackgroundColor(R.drawable.caratula); //Use initial cover as reference
    }
    //Initialize the MediaPlayer instance of Audio resorces
    private void initializeMediaPlayers() {
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
    }
    //Config listener of the SeekBar
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
                Siguiente(null); //Skip to the next song after 2 seconds
            }, 1000);
        });
    }
    //Config the finalize listener for the Media player so it can handle the song transition
    private void setupMediaCompletionListener() {
        for (int i = 0; i < vectormp.length; i++) {
            final int index = i;
            vectormp[i].setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.seekTo(0);
                mediaPlayer.pause(); // Pausar al final
                play_pause.setBackgroundResource(R.drawable.play_circle);
                //Wait 2 seconds and then play the next song
                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                if (posicion < vectormp.length - 1) {
                    posicion++;
                } else {
                    posicion = 0;
                }
                updateUI();
                   vectormp[posicion].seekTo(0); //Reset the position of the new track to 0
                updateSeekbar();
                    updateUI();
                    vectormp[posicion].start();
                    play_pause.setBackgroundResource(R.drawable.pause_circle);
                    updateSeekbar();
                }, 3000);
            });
        }
    }

    // play or pause the actual song
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
    //Alter the fuctionality of repetion for the actual song
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

    //goes to the next song
    public void Siguiente(View view) {
        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause(); //Pause the current track
            handler.removeCallbacks(runnable); //Stop the runnable that updates the SeekBar
            play_pause.setBackgroundResource(R.drawable.play_circle);
        }
        //Show the ProgressBar
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
                vectormp[posicion].seekTo(0); //Reset the position of the new track to 0
                vectormp[posicion].start();
                updateUI();
                updateSeekbar();
            } else {
                Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
                play_pause.setBackgroundResource(R.drawable.play_circle);
            }
        }, 1000); //1000 milliseconds = 1seconds (load simulation)

    }
    private void DisableRepetir(){
        if (repetir == 1) {
            Repetir(null); //Call the Repeat function to disable the loop and update the icon
        }
    }
    //Goes back to the previus song
    public void Anterior(View view) {
        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause(); //Pause current track
            handler.removeCallbacks(runnable); //Stop the runnable that updates the SeekBar
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
                DisableRepetir();
                posicion--;
                vectormp[posicion] = MediaPlayer.create(MainActivity.this,
                        getResources().getIdentifier(audioNames[posicion], "raw",
                                getPackageName()));
                vectormp[posicion].seekTo(0); //Reset the position of the new track to 0
                vectormp[posicion].start();
                updateUI();
                updateSeekbar();
            } else {
                Toast.makeText(this, "No hay más canciones", Toast.LENGTH_SHORT).show();
            }
        },1000);
    }
    //Reset the Instance of the MediaPlayer
    private void resetMediaPlayers() {
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
    }
    //Update the Ui components in fuction of the actual song
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
    //Changes the colour background base in the cover of the album
    private void updateBackgroundColor(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                int defaultColor = 0x000000; //Default colour in case the pallete fails
                int dominantColor = palette.getDominantColor(defaultColor);
                if (isColorLight(dominantColor)) {
                    dominantColor = palette.getVibrantColor(defaultColor);//Uses a vibrant colour
                    // if the dominant is too light
                }
                if (dominantColor == defaultColor) {
                    dominantColor = palette.getMutedColor(defaultColor); //Uses a off colour if
                    // there`s not available colour vibrant
                }
                int darkenedColor = darkenColor(dominantColor); //Darkest the dominant colour
                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{darkenedColor, dominantColor});
                mainLayout.setBackground(gradientDrawable);
            }
        });
    }
    //Darkest a colour Reducing the brightness
    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 20f; //Reduce brightness of the colour
        return Color.HSVToColor(hsv);
    }
    //Verifice if the colour is light or dark
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
    //Updates SeekBar periodically so it reflec the posicion of reproduction
    public void updateSeekbar() {
        seekBar.setMax(vectormp[posicion].getDuration());
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(vectormp[posicion].getCurrentPosition());
                actualizarTiempo(); //Calls the method to update the Textviews
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }
    //Method to Reset the time in minutes and seconds
    private String formatearTiempo(int segundos) {
        int minutos = segundos / 60;
        int segundosRestantes = segundos % 60;
        return String.format("%d:%02d", minutos, segundosRestantes);
    }

    //Method to Update the TextViews with the elapsed and remaining time
    private void actualizarTiempo() {
        int duracionTotal = vectormp[posicion].getDuration() / 1000;
        int posicionActual = vectormp[posicion].getCurrentPosition() / 1000;

        tiempoTranscurridoTextView.setText(formatearTiempo(posicionActual));
        tiempoRestanteTextView.setText(String.format("%s", formatearTiempo
                (duracionTotal - posicionActual)));
    }
    //Free resorces of the Media player when the activity is destroy
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
        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause();
            handler.removeCallbacks(runnable);
            play_pause.setBackgroundResource(R.drawable.play_circle);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//Create an AlertDialog
        // with ListView
        builder.setTitle("Lista de Audios");
        builder.setItems(audioNames, (dialog, which) -> {
            //Update the actual position
            if (which != posicion) { //Only if the selection is different of the actual position
                vectormp[posicion].stop();
                vectormp[posicion].reset();
                vectormp[posicion].release();
                posicion = which;
                //Play the selected track
                vectormp[posicion] = MediaPlayer.create(MainActivity.this,
                        getResources().getIdentifier(audioNames[posicion], "raw",
                                getPackageName()));
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
