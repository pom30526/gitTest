package com.example.lsm.lasttest.Audio;

import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Caffe on 2016-12-14.
 */
public class JWavFileForIO {
    private String	myPath;
    public int 	myChunkSize;
    public int	    mySubChunk1Size;
    public short 	myFormat;
    public short 	myChannels;
    public int   	mySampleRate;
    public int   	myByteRate;
    public short 	myBlockAlign;
    public short 	myBitsPerSample;
    public int	    myDataSize;
    public byte[] 	myData, myData2;
    public RandomAccessFile raf;
    public int     myCurrentSize;

    public JWavFileForIO(String name)
    {
        myPath = name;
    }

    public Boolean read() throws IOException {
        raf = new RandomAccessFile(myPath, "r");
        raf.seek(4);
        byte[] buf = new byte[4];
        raf.read(buf, 0, 4);
        int[] arr = new int[4];
        int i;
        for(i=0;i<4;i++){
            arr[i] = (int)(buf[3-i] & 0xFF);
        }
        myChunkSize = ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
        //myChunkSize = raf.readInt();
        raf.seek(16);
        raf.read(buf, 0, 4);
        for(i=0;i<4;i++){
            arr[i] = (int)(buf[3-i] & 0xFF);
        }
        mySubChunk1Size = ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
        //mySubChunk1Size = raf.readInt();
        byte[] sbuf = new byte[2];
        raf.read(sbuf, 0, 2);
        short[] sarr = new short[2];
        for(i=0;i<2;i++){
            sarr[i] = (short)(sbuf[1-i] & 0xFF);
        }
        myFormat = (short)((sarr[0] << 8) + (sarr[1] << 0));
        //myFormat = raf.readShort();
        raf.read(sbuf, 0, 2);
        for(i=0;i<2;i++){
            sarr[i] = (short)(sbuf[1-i] & 0xFF);
        }
        myChannels = (short)((sarr[0] << 8) + (sarr[1] << 0));
        //myChannels = raf.readShort();
        raf.read(buf, 0, 4);
        for(i=0;i<4;i++){
            arr[i] = (int)(buf[3-i] & 0xFF);
        }
        mySampleRate = ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
        //mySampleRate = raf.readInt();
        raf.read(buf, 0, 4);
        for(i=0;i<4;i++){
            arr[i] = (int)(buf[3-i] & 0xFF);
        }
        myByteRate = ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
        //myByteRate = raf.readInt();
        raf.read(sbuf, 0, 2);
        for(i=0;i<2;i++){
            sarr[i] = (short)(sbuf[1-i] & 0xFF);
        }
        myBlockAlign = (short)((sarr[0] << 8) + (sarr[1] << 0));
        //myBlockAlign = raf.readShort();
        raf.read(sbuf, 0, 2);
        for(i=0;i<2;i++){
            sarr[i] = (short)(sbuf[1-i] & 0xFF);
        }
        myBitsPerSample = (short)((sarr[0] << 8) + (sarr[1] << 0));
        //myBitsPerSample = raf.readShort();
        raf.seek(40);
        raf.read(buf, 0, 4);
        for(i=0;i<4;i++){
            arr[i] = (int)(buf[3-i] & 0xFF);
        }
        myDataSize = ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
        //myDataSize = raf.readInt();

        myCurrentSize = 512;
        myData = new byte[ myCurrentSize ];
        raf.read(myData);//, 0, myCurrentSize);

        return true;
    }

    public void set_sub(int size) {
        myData2 = new byte[ size*2 ];
    }

    public Boolean next_read(int size) throws IOException
    {
        int i, j;
        size *= 2;
        if (myCurrentSize + size <= myChunkSize) {
            for (i = 0; i < 512-size; i++) {
                myData[i] = myData[i+size];
            }
            raf.read(myData2);
            for (i = 512-size, j = 0; i < 512; i++, j++) {
                myData[i] = myData2[j];
            }
            myCurrentSize += size;
        }
        else return false;

        return true;
    }

    public byte[] getSummary()
    {
        byte[] summary = new byte[256];

        return summary;
    }

}