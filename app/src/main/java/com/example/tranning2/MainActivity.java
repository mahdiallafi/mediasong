package com.example.tranning2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    Button button,button1,button3,button4,button5;
    MediaRecorder mediaRecord;
    TextView textView;
    private SoundPool soundPool;
    private  int sound1;
    BufferedReader reader;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        sound1 = soundPool.load(this, R.raw.song, 1);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button:
                        soundPool.play(sound1, 1, 1, 0, 0, 1);
                        soundPool.autoPause();

                        break;
                }
            }
        });
        textView=findViewById(R.id.textView);
        button1=findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              String text="";
                try {
                    InputStream inputStream= getAssets().open("song.txt");
                   reader=new BufferedReader(new InputStreamReader(inputStream));
                   String line=reader.readLine();
                   while (line !=null){
                       Log.d("StackOverflow", line);
                       line=reader.readLine();
                      // textView.setText(line);
                   }

                   /* int size=inputStream.available();
                    byte[] buffer=new byte[size];
                    inputStream.read(buffer);
                    inputStream.close();
                    text=new String(buffer);*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
            //    textView.setText(text);
            }
        });

        button3=findViewById(R.id.button3);
        ///here we check for the record
        if(MicrophoneOn()){
            Microphonepermission();
            System.out.println("here"+MicrophoneOn());
        }
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaRecord=new MediaRecorder();
                    mediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecord.setOutputFile(getRecordFile());
                    mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecord.prepare();
                    mediaRecord.start();
                    Toast.makeText(MainActivity.this, "Recording is start", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Recording cant", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button4=findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecord.stop();
                mediaRecord.reset();
                mediaRecord.release();
                mediaRecord=null;
                Toast.makeText(MainActivity.this, "Recording is stop", Toast.LENGTH_SHORT).show();
            }
        });
        button5=findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setDataSource(getRecordFile());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Recording is play", Toast.LENGTH_SHORT).show();
                }catch(IllegalStateException| IOException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "play is not play", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean MicrophoneOn(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }else
        {
            return false;
        }
    }
    private void Microphonepermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.RECORD_AUDIO
            },0);
        }
    }
    private String getRecordFile(){
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File musicDirectory=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file=new File(musicDirectory,"testRecord.mp3");
        return file.getPath();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool=null;
    }
}