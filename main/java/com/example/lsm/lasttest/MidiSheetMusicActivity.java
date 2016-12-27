/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.example.lsm.lasttest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;

import com.example.lsm.lasttest.guiHelper.ClefSymbol;
import com.example.lsm.lasttest.midi.model.MidiOptions;


/** @class MidiSheetMusicActivity
 * This is the launch activity for MidiSheetMusic.
 * It simply displays the splash screen, and a button to choose a song.
 */
public class MidiSheetMusicActivity extends Activity {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadImages();
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.choose_song);
        Button button1 =(Button) findViewById(R.id.turning);//turning 상대음 바로가기
        Button button2 = (Button) findViewById(R.id.ndk);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MidiSheetMusicActivity.this,NdkTest.class);
                startActivity(intent);

            }
        });
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        chooseSong();
                    }
                }
        );
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turning();
            }
        });
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    /** Start the ChooseSongActivity when the "Choose Song" button is clicked */
    private void chooseSong() {
        Intent intent = new Intent(this, ChooseSongActivity.class);
        startActivity(intent);
    }
    private void turning(){
        Intent intent = new Intent(this,Turing.class);
        startActivity(intent);

        //intent.putExtra("midi",getAssets().open("abc.mid"));
    }


    /** Load all the resource images */
    private void loadImages() {
        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);
        MidiPlayer.LoadImages(this);
    }

    /** Always use landscape mode for this activity. */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

