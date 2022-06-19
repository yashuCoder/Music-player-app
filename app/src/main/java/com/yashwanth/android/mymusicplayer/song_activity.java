package com.yashwanth.android.mymusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class song_activity extends AppCompatActivity {
    TextView song_name;
    CardView play_button;
    ImageView previous,next;
    ArrayList<File> songsFiles;
    LottieAnimationView lottie;
    ImageView playNPause;
    String current_song_name;
    MediaPlayer mediaPlayer;
    CircularSeekBar seekBar;
    Thread updateSeek;
    int position;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        play_button = findViewById(R.id.play_button);
        song_name = findViewById(R.id.song_name_play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        playNPause = findViewById(R.id.play_n_pause);
        lottie = findViewById(R.id.animationView);
        seekBar = findViewById(R.id.seek_bar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songsFiles = (ArrayList)bundle.getParcelableArrayList("song_file");
        current_song_name = intent.getStringExtra("song_name");
        Log.d("songName", ""+current_song_name);
        song_name.setText(current_song_name);
        position = intent.getIntExtra("position",0);

        Uri uri = Uri.parse(songsFiles.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();

        seekBar.setMax(mediaPlayer.getDuration());


        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playNPause.setImageResource(R.drawable.play);
                    lottie.pauseAnimation();
                }
                else{
                    playNPause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                    lottie.playAnimation();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                if(position!=0){
                    position = position - 1;
                }
                else{
                    position = songsFiles.size() - 1;
                }
                Uri uri = Uri.parse(songsFiles.get(position).toString());
                mediaPlayer = MediaPlayer.create(song_activity.this,uri);
                mediaPlayer.start();

                seekBar.setMax(mediaPlayer.getDuration());

                song_name.setText(songsFiles.get(position).getName().replace(".mp3",""));

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float v, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar circularSeekBar) {
                mediaPlayer.seekTo((int)circularSeekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar circularSeekBar) {

            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition<=mediaPlayer.getCurrentPosition()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        Log.d("currentPosition", ""+currentPosition+" "+mediaPlayer.getDuration());
                        sleep(800);
                        if(currentPosition==mediaPlayer.getDuration()){
                            playNext();
                        }

                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();


    }
    public void playNext(){
        Log.d("nextSong", "playNext: next song is playing");
        mediaPlayer.stop();
        if(position!=songsFiles.size()){
            position = position + 1;
        }
        else{
            position = 0;
        }
        Uri uri = Uri.parse(songsFiles.get(position).toString());
        mediaPlayer = MediaPlayer.create(song_activity.this,uri);
        mediaPlayer.start();

        seekBar.setMax(mediaPlayer.getDuration());

        song_name.setText(songsFiles.get(position).getName().replace(".mp3",""));

    }
}