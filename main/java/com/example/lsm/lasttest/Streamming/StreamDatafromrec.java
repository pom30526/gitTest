package com.example.lsm.lasttest.Streamming;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import com.example.lsm.lasttest.Audio.JSoundAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lsm on 2016-12-22.
 */

public class StreamDatafromrec  {

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int BufferElements2Rec =512;
    private int BytesPerElement =2;

    byte[] bData;
    float test;
    private JSoundAnalysis analysis;
    private static StreamDatafromrec instance;


    private  StreamDatafromrec(){

    }

    public static  StreamDatafromrec getInstance(){
        if(instance ==null){
            instance = new StreamDatafromrec();
        }
        return instance;
    }
    public void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        analysis = new JSoundAnalysis();

        recorder.startRecording();
        isRecording = true;
        // 녹음 중일 때 스래드를 통해서 파일로 저장합니다.
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();


            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }
    //byte 파일로 반환하는 부분 short 타입의 배열이 들어갑니다.
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }
    public void writeAudioDataToFile() {
        // Write the output audio in byte

        //String filePath = "/sdcard/test.pcm";  //pcm으로 저장되는 부분입니다.
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, BufferElements2Rec);
            //System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                 bData = short2byte(sData); // bData[] 이부분을 전송하면 byte[]타입으로 받을 수 있습니다.
                test=analysis.MemToHz(bData);
                Thread.sleep(200);
                System.out.println(test);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




        try {


            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
           // new File("/sdcard/test.pcm").delete();
            String filepath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.pcm";
           // new File(filepath).delete();
        }
    }


}
