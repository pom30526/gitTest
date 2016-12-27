package com.example.lsm.lasttest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lsm.lasttest.Audio.JSoundAnalysis;
import com.example.lsm.lasttest.Streamming.StreamDatafromrec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lsm on 2016-12-12.
 */

public class NdkTest extends AppCompatActivity {
    Button bt1,bt2,bt3;
    StreamDatafromrec rec;
    JSoundAnalysis analysis;
    byte[] adata;
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rec = StreamDatafromrec.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ndktest);
        bt1 =(Button) findViewById(R.id.stbt);
        bt2 =(Button) findViewById(R.id.abcded);
        bt3 =(Button) findViewById(R.id.setting);
       // bt1.setText(stringFromJNI());
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        analysis = new JSoundAnalysis();
//        adata=rec.writeAudioDataToFile();
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startRecording();
                rec.startRecording();
                Log.d("st","녹음시작");
//                Log.d("st1",analysis.MemToHz(adata)+"");



            }
        });
       bt2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               rec.stopRecording();
               Log.d("st2","녹음종료");

           }
       });






    }

}

















//    JSoundAnalysis sa = new JSoundAnalysis();
//Log.d("test1","아직시작안함");

//         float hz = sa.WavToHz(Environment.getExternalStorageDirectory().getAbsolutePath()+"/AudioRecorder/speaker.wav");
//            bt1.setText(hz+" ////");
//          Log.d("pom200",hz+"");



//
//    static {
//        System.loadLibrary("native-lib");
//    }
//
//    public native String stringFromJNI();