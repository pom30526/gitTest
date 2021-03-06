//package com.example.lsm.lasttest.Audio;
//
///**
// * Created by lsm on 2016-12-07.
// */
//
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//
//public class test extends AppCompatActivity {
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        start1=(Button)findViewById(R.id.start);
//        stop1=(Button)findViewById(R.id.stop);
//        txt=(TextView)findViewById(R.id.textView1);
//
//        start1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startRecord();
//                txt.setText("錄音中");
//            }
//        });
//        stop1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                stopRecord();
//                txt.setText("結束了");
//
//            }
//        });
//    }
//
//
//    private String getFilename(){
//        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
//
//        if(file.exists()){
//            file.delete();
//        }
//
//        return (file.getAbsolutePath() + "/speaker.wav" );
//    }
//
//    private String getTempFilename(){
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
//
//        if(!file.exists()){
//            file.mkdirs();
//        }
//
//        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
//
//        if(tempFile.exists())
//            tempFile.delete();
//
//        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
//    }
//
//    private void startRecord(){
//
//        createAudioRecord();
//        audioRecord.startRecording();
//
//        isRecording = true;
//
//        recordingThread = new Thread(new Runnable() {
//            public void run() {
//                writeAudioDataToFile();
//            }
//        },"AudioRecorder Thread");
//
//        recordingThread.start();
//    }
//
//    private void writeAudioDataToFile(){
//        byte data[] = new byte[recBufSize];
//        String filename = getTempFilename();
//        FileOutputStream os = null;
//
//        try {
//            os = new FileOutputStream(filename);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        int read = 0;
//
//        if(null != os){
//            while(isRecording){
//                read = audioRecord.read(data, 0, recBufSize);
//
//                if(AudioRecord.ERROR_INVALID_OPERATION != read){
//                    try {
//                        os.write(data);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            try {
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void stopRecord(){
//        if(null != audioRecord){
//            isRecording = false;
//
//            audioRecord.stop();
//            audioRecord.release();
//
//            audioRecord = null;
//            recordingThread = null;
//        }
//
//        copyWaveFile(getTempFilename(),getFilename());
//        deleteTempFile();
//    }
//
//    private void deleteTempFile() {
//        File file = new File(getTempFilename());
//
//        file.delete();
//    }
//
//    private void copyWaveFile(String inFilename,String outFilename){
//        FileInputStream in = null;
//        FileOutputStream out = null;
//        long totalAudioLen = 0;
//        long totalDataLen = totalAudioLen + 36;
//        long longSampleRate = frequency;
//        int channels = 1;
//        long byteRate = RECORDER_BPP * frequency * channels/8;
//
//        byte[] data = new byte[recBufSize];
//
//        try {
//            in = new FileInputStream(inFilename);
//            out = new FileOutputStream(outFilename);
//            totalAudioLen = in.getChannel().size();
//            totalDataLen = totalAudioLen + 36;
//
//            //AppLog.logString("File size: " + totalDataLen);
//
//            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
//                    longSampleRate, channels, byteRate);
//
//            while(in.read(data) != -1){
//                out.write(data);
//            }
//
//            in.close();
//            out.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void WriteWaveFileHeader(
//            FileOutputStream out, long totalAudioLen,
//            long totalDataLen, long longSampleRate, int channels,
//            long byteRate) throws IOException {
//
//        byte[] header = new byte[44];
//
//        header[0] = 'R';  // RIFF/WAVE header
//        header[1] = 'I';
//        header[2] = 'F';
//        header[3] = 'F';
//        header[4] = (byte) (totalDataLen & 0xff);
//        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
//        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
//        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
//        header[8] = 'W';
//        header[9] = 'A';
//        header[10] = 'V';
//        header[11] = 'E';
//        header[12] = 'f';  // 'fmt ' chunk
//        header[13] = 'm';
//        header[14] = 't';
//        header[15] = ' ';
//        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
//        header[17] = 0;
//        header[18] = 0;
//        header[19] = 0;
//        header[20] = 1;  // format = 1
//        header[21] = 0;
//        header[22] = (byte) channels;
//        header[23] = 0;
//        header[24] = (byte) (longSampleRate & 0xff);
//        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
//        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
//        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
//        header[28] = (byte) (byteRate & 0xff);
//        header[29] = (byte) ((byteRate >> 8) & 0xff);
//        header[30] = (byte) ((byteRate >> 16) & 0xff);
//        header[31] = (byte) ((byteRate >> 24) & 0xff);
//        header[32] = (byte) (1 * 16 / 8);  // block align
//        header[33] = 0;
//        header[34] = RECORDER_BPP;  // bits per sample
//        header[35] = 0;
//        header[36] = 'd';
//        header[37] = 'a';
//        header[38] = 't';
//        header[39] = 'a';
//        header[40] = (byte) (totalAudioLen & 0xff);
//        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
//        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
//        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
//        out.write(header, 0, 44);
//    }
//    public void createAudioRecord(){
//        recBufSize = AudioRecord.getMinBufferSize(frequency,
//                channelConfiguration, EncodingBitRate);
//
//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
//                channelConfiguration, EncodingBitRate, recBufSize);
//        System.out.println("AudioRecord成功");
//    }
//
//
//}
