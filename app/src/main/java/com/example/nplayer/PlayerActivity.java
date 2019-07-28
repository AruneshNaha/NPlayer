package com.example.nplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.PorterDuff.Mode.SRC_IN;

public class PlayerActivity extends AppCompatActivity {

    Button btn_next,btn_prev,btn_pause;
    TextView songName;
    SeekBar Songseek;

    String sname;
    static MediaPlayer mymediapayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateseekbar;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btn_next=(Button)findViewById(R.id.next);
        btn_pause=(Button)findViewById(R.id.pause);
        btn_prev=(Button)findViewById(R.id.previous);

        songName=(TextView) findViewById(R.id.SongName);

        Songseek=(SeekBar)findViewById(R.id.seekBar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateseekbar = new Thread(){
            @Override
            public void run() {
                int totalduration=mymediapayer.getDuration();
                int currentposition=0;

                while (currentposition<totalduration){
                    try{
                        sleep(500);
                        currentposition=mymediapayer.getCurrentPosition();
                        Songseek.setProgress(currentposition);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        if(mymediapayer!=null){
            mymediapayer.stop();
            mymediapayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs=(ArrayList)bundle.getParcelableArrayList("Songs");
        sname=mySongs.get(position).getName().toString();

        String songname=i.getStringExtra("songname");
        songName.setText(songname);
        songName.setSelected(true);

        position=bundle.getInt("pos",0);

        Uri u = Uri.parse(mySongs.get(position).toString());

        mymediapayer=MediaPlayer.create(getApplicationContext(),u);

        mymediapayer.start();
        Songseek.setMax(mymediapayer.getDuration());

        updateseekbar.start();
        Songseek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        Songseek.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), SRC_IN);

        Songseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mymediapayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Songseek.setMax(mymediapayer.getDuration());


                if(mymediapayer.isPlaying()){
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mymediapayer.pause();
                }
                else {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mymediapayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mymediapayer.stop();
                mymediapayer.release();
                position = ((position+1)%mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());

                mymediapayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName().toString();
                songName.setText(sname);

                mymediapayer.start();
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mymediapayer.stop();
                mymediapayer.release();
                position=((position-1)<0?(mySongs.size()-1):(position-1));

                Uri u = Uri.parse((mySongs.get(position).toString()));

                mymediapayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName().toString();
                songName.setText(sname);

                mymediapayer.start();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}


