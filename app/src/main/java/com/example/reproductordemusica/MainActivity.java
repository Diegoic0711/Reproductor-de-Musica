package com.example.reproductordemusica;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button play_pause, repeat_one; //Variable for pause and repeat buttons
    MediaPlayer mp;//Variable for music
    ImageView iv; //Variable for song images
    int repetir = 2, posicion = 0; //used for repeat interactions
    MediaPlayer vectormp [] = new MediaPlayer[3]; //Vector used to manage songs
    SeekBar seekBar; //Variable to manage seek bar
    Runnable runnable; // Use to seekbar
    Handler handler;// Use to seekbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        play_pause=(Button)findViewById(R.id.play_pause);//look for the created button
        mp=MediaPlayer.create(this,R.raw.manifiesto);//search for the song to play
        repeat_one = (Button)findViewById(R.id.repeat_one);
        iv = (ImageView)findViewById(R.id.image_view);
        seekBar = findViewById(R.id.seekBar); //Search for seekbar
        handler=new Handler();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mp.seekTo(i);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
                if(posicion < vectormp.length -1){
                    posicion++;
                    vectormp[posicion].start();
                    updateSeekbar();
                    if (posicion == 0){
                        iv.setImageResource(R.drawable.caratula);
                    }else if (posicion == 1){
                        iv.setImageResource(R.drawable.portada_1);
                    }else if (posicion == 2){
                        iv.setImageResource(R.drawable.portada_2);
                    }
                }else {
                    posicion = 0;
                    vectormp[posicion].start();
                    updateSeekbar();
                    iv.setImageResource(R.drawable.caratula);
                }
            }
        });
        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
        updateSeekbar();
    }
    //Play&Pause Method
    public void PlayPause(View view){
        if(vectormp[posicion].isPlaying()){
            //This helps us change the button from play to pause
            vectormp[posicion].pause();
            play_pause.setBackgroundResource(R.drawable.play_circle);
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
        } else {
            //This helps us change the button from pause to play
           vectormp[posicion].start();
           play_pause.setBackgroundResource(R.drawable.pause_circle);
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
            updateSeekbar();
        }
    }
    //repeat a track method
    public void Repetir (View view){
        if(repetir == 1){
            repeat_one.setBackgroundResource(R.drawable.repeat);
            Toast.makeText(this, "No repetir", Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(false);
            repetir = 2;
        }else {
            repeat_one.setBackgroundResource(R.drawable.repeat_one);
            Toast.makeText(this, "Repetir", Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(true);
            repetir = 1;
        }
    }
    //next song
    public void Siguiente(View view){
        if(posicion < vectormp.length -1){
            if(vectormp[posicion].isPlaying()){
                vectormp[posicion].stop();
                posicion++;
                vectormp[posicion].start();

                if (posicion == 0){
                    iv.setImageResource(R.drawable.caratula);
                }else if (posicion == 1){
                    iv.setImageResource(R.drawable.portada_1);
                }else if (posicion == 2){
                    iv.setImageResource(R.drawable.portada_2);
                }
            }else {
                posicion ++;

                if (posicion == 0){
                    iv.setImageResource(R.drawable.caratula);
                }else if (posicion == 1){
                    iv.setImageResource(R.drawable.portada_1);
                }else if (posicion == 2){
                    iv.setImageResource(R.drawable.portada_2);
                }
            }
        }else {
            Toast.makeText(this, "No hay mas canciones", Toast.LENGTH_SHORT).show();
        }
    }
    //Method to return to the previous song
    public  void Anterior(View view){
        if (posicion >= 1){
            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop();
                vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
                vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
                vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
                posicion--;
                if (posicion == 0) {
                    iv.setImageResource(R.drawable.caratula);
                } else if (posicion == 1) {
                    iv.setImageResource(R.drawable.portada_1);
                } else if (posicion == 2) {
                    iv.setImageResource(R.drawable.portada_2);
                }
                vectormp[posicion].start();
            }else {
                posicion--;
                if (posicion == 0){
                    iv.setImageResource(R.drawable.caratula);
                }else if (posicion == 1){
                    iv.setImageResource(R.drawable.portada_1);
                }else if (posicion == 2){
                    iv.setImageResource(R.drawable.portada_2);
                }
            }
        }else{
            Toast.makeText(this, "No hay mas canciones", Toast.LENGTH_SHORT).show();
        }
    }
    public void updateSeekbar(){
        int currentPosition = mp.getCurrentPosition();
        seekBar.setProgress(currentPosition);
        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable,1000);
    }
}