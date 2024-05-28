package com.example.reproductordemusica;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

        vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
        vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
        vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);

        play_pause.setOnClickListener(new View.OnClickListener() {//initialize playback
            @Override
            public void onClick(View v) {
                //This helps us change the button from play to pause
                if(vectormp[posicion].isPlaying()){
                    mp.pause();
                    play_pause.setBackgroundResource((R.drawable.play_circle));
                    Toast.makeText(MainActivity.this,"Pausa",Toast.LENGTH_SHORT).show();
                }else{//esto nos ayuda a cambiar el boton de pausa a play
                    vectormp[posicion].start();
                    play_pause.setBackgroundResource(R.drawable.pause_circle);
                    Toast.makeText(MainActivity.this,"Play",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Method to Stop Playback
    public void Stop(View view){
        if (vectormp[posicion] != null){
            vectormp[posicion].stop();

            vectormp[0] = MediaPlayer.create(this, R.raw.manifiesto);
            vectormp[1] = MediaPlayer.create(this, R.raw.mas_de_lo_mismo);
            vectormp[2] = MediaPlayer.create(this, R.raw.tamo_es_pa_gozar);
            posicion = 0;
            play_pause.setBackgroundResource(R.drawable.fondo);
            iv.setImageResource(R.drawable.caratula);
            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
            play_pause.setBackgroundResource((R.drawable.play_circle));
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

}