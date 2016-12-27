//
// Created by Caffe on 2016-12-11.
//

#ifndef SOUNDLEADER_SOUNDANALYSIS_H
#define SOUNDLEADER_SOUNDANALYSIS_H

#include <stdio.h>
#include <fstream>
#include <iostream>
#include <string>

#include <math.h>

using namespace std;

// for FFT
static int DOTPOINT[6] = {32, 64, 128, 256, 512, 1024};
static int LOG2[6] = {5, 6, 7, 8, 9, 10};

#define pi  3.141592

typedef struct
{
    double  r;
    double  i;
} COMPLEX;

class WavFileForIO {

private:
    string	myPath;
public:
    int 	myChunkSize;
    int	    mySubChunk1Size;
    short 	myFormat;
    short 	myChannels;
    int   	mySampleRate;
    int   	myByteRate;
    short 	myBlockAlign;
    short 	myBitsPerSample;
    int	    myDataSize;
    char* 	myData;

    void setPath( const string& newPath ) {
        myPath = newPath;
    }

    ~WavFileForIO() {
        myChunkSize = 0;
        mySubChunk1Size = 0;
        myFormat = 0;
        myChannels = 0;
        mySampleRate = 0;
        myByteRate = 0;
        myBlockAlign = 0;
        myBitsPerSample = 0;
        myDataSize = 0;
    }

    WavFileForIO( const string& filePath ) {
        myPath = filePath;
    }

    bool read(){
        ifstream inFile( myPath.c_str(), ios::in | ios::binary);
        inFile.seekg(4, ios::beg);
        inFile.read( (char*) &myChunkSize, 4 ); // read the ChunkSize
        inFile.seekg(16, ios::beg);
        inFile.read( (char*) &mySubChunk1Size, 4 ); //read the SubChunk1Size
        //inFile.seekg(20, ios::beg);
        inFile.read( (char*) &myFormat, sizeof( short )); // read the file format.  This should be 1 for PCM
        //inFile.seekg(22, ios::beg);
        inFile.read( (char*) &myChannels, sizeof( short) ); // read the # of channels (1 or 2)
        //inFile.seekg(24, ios::beg);
        inFile.read( (char*) &mySampleRate, sizeof( int) ); // read the samplerate
        //inFile.seekg(28, ios::beg);
        inFile.read( (char*) &myByteRate, sizeof( int )); // read the byterate
        //inFile.seekg(32, ios::beg);
        inFile.read( (char*) &myBlockAlign, sizeof(short ) ); // read the blockalign
        //inFile.seekg(34, ios::beg);
        inFile.read( (char*) &myBitsPerSample, sizeof(short ) ); // read the bitspersample
        inFile.seekg(40, ios::beg);
        inFile.read( (char*) &myDataSize, sizeof( int )); // read the size of the data

        myData = new char[ myChunkSize ];
        inFile.seekg( 44, ios::beg );
        inFile.read( myData, myChunkSize );

        inFile.close();

        return true;
    }

    bool save() {
        fstream myFile ( myPath.c_str(), ios::out | ios::binary);

        //myChunkSize = ;
        mySubChunk1Size = 16;
        myFormat = 1;
        myChannels = 1;
        mySampleRate = 16000;
        myByteRate = 16000*2;
        myBlockAlign = 2;
        myBitsPerSample = 16;
        //myByteRate = 11025; myBlockAlign = 1;
        myBitsPerSample = 8;

        myFile.seekp( 0, ios::beg );
        myFile.write( "RIFF", 4 );
        myFile.write( (char*) &myChunkSize, 4 );
        myFile.write( "WAVE", 4 );
        myFile.write( "fmt ", 4 );
        myFile.write( (char*) &mySubChunk1Size, 4 );
        myFile.write( (char*) &myFormat, 2 );
        myFile.write( (char*) &myChannels, 2 );
        myFile.write( (char*) &mySampleRate, 4 );
        myFile.write( (char*) &myByteRate, 4 );
        myFile.write( (char*) &myBlockAlign, 2 );
        myFile.write( (char*) &myBitsPerSample, 2 );
        myFile.write( "data", 4 );
        myFile.write( (char*) &myDataSize, 4 );
        myFile.write( myData, myDataSize );

        return true;
    }

    char *getSummary() {
        char *summary = new char[250];
        //printf( "myChunkSize: %d\nmySubChunk1Size:%d\nmyFormat: %d\nmyChannels: %d\nmySampleRate: %d\nmyByteRate:%d\nmyBlockAlign: %d\nmyBitsPerSample: %d\n\n",
        //	myChunkSize, mySubChunk1Size, myFormat, myChannels, mySampleRate, myByteRate,myBlockAlign, myBitsPerSample );
        sprintf( summary, " Format: %d\n Channels: %d\n SampleRate: %d\n ByteRate: %d\n BlockAlign: %d\n BitsPerSample: %d\n DataSize: %d\n",
                 myFormat, myChannels, mySampleRate, myByteRate,myBlockAlign, myBitsPerSample, myDataSize );

        return summary;
    }
};


class CSoundAnalysis
{
public:
    CSoundAnalysis(void)
    {
        x_cnt = 0;
        SetColor();
    }

    ~CSoundAnalysis(void) {}

    void set_window(short *s,short *ds,int n,int mode)
    {
        int i;
        double wk;
        switch (mode) {
            case 0: /* Rectangular */
                for (i=0; i<n; i++) {
                    ds[i]=(short)s[i];
                }
                break;
            case 1: /* Hanning */
                wk=6.283185307/(double)n;
                for (i=0; i<n; i++) {
                    ds[i]=(short)s[i]*(0.5-0.5*cos((double)i * wk));
                }
                break;
            case 2: /* Hamming */
                wk=6.283185307/(double)n;
                for (i=0; i<n; i++) {
                    ds[i]=(short)s[i]*(0.54-0.46*cos((double)i * wk));
                }
                break;
            case 3: /* Blackman */
                wk=6.283185307/(double)n;
                for (i=0; i<n; i++) {
                    ds[i]=(short)s[i]*
                          (0.42-0.5*cos((double)i * wk)+0.08*cos((double)i *wk*2.0));
                }
                break;
            case 4: /* Bartlett */
                wk=2.0/(double)n;
                for (i=0; i<n/2; i++) {
                    ds[i]=(short)s[i]*((double)i * wk);
                }
                for (; i<n; i++) {
                    ds[i]=(short)s[i]*((double)(n-i) * wk);
                }
                break;
        }
    }

//---------------------------------------------------------------------------
    int Bit_Reverse(int bit, int r)
    {
        int  i, bitr;
        bitr = 0;
        for (i = 0; i < r; i++)
        { bitr <<= 1;
            bitr |= (bit&1);
            bit  >>= 1;
        }
        return(bitr);
    }

//---------------------------------------------------------------------------
    void FFT_Swap(COMPLEX *data1, COMPLEX *data2)
    {
        double  temp;

        temp     = data1->r;
        data1->r = data2->r;
        data2->r = temp;
        temp     = data1->i;
        data1->i = data2->i;
        data2->i = temp;
    }

    void PCM2FFT(short *data, double *mag_spectrum, COMPLEX *complex_data, double *maxSpectrum, int datapoint, int fft_point)
    {
        int i, j, k, w;

        int log2point = log((double)fft_point)/log((double)2);
        //cout << log2point << endl;

        COMPLEX  *data1, *data2;
        double   real, imag;
        const int point_num = fft_point;//512;

        short *window = new short[point_num];

        data1 = (COMPLEX *)malloc(sizeof(COMPLEX)*point_num);
        data2 = (COMPLEX *)malloc(sizeof(COMPLEX)*point_num);

        set_window(data,window,point_num,2);

        for(k = 0,w=0; k < point_num; k++,w++) {
            data1[w].r = window[k];
            data1[w].i = 0.;
        }

        GoFFT(log2point, data1, data2, 1);

        double max = -9999;
        for (i =0 ; i < point_num; i++)
        {
            complex_data[i] = data2[i];
            real = data2[i].r;
            imag = data2[i].i;
            mag_spectrum[i] = sqrt((double)(real*real + imag*imag));
            if(max < mag_spectrum[i]) max = mag_spectrum[i];
        }

        *maxSpectrum = max;

        delete []window;
        delete []data1;
        delete []data2;

        return;
    }

    void PCM2IFFT(COMPLEX *complex_input_data, COMPLEX *complex_output_data, int datapoint, int fft_point)
    {
        int i, j, k, w;

        int log2point = log((double)fft_point)/log((double)2);

        GoFFT(log2point, complex_input_data, complex_output_data, -1);

        return;
    }

    void GoFFT(int log2N, COMPLEX *data1, COMPLEX *data2, int sign)
    {
        int     i=0, j=0, p=0, j1=0, j2=0, k=0, k1=0, bit=0;
        double  a=0, b=0, deg=0;

        deg = 2.*pi/DOTPOINT[log2N - 5];
        k  = 0;
        j1 = log2N - 1;
        j2 = DOTPOINT[log2N - 5];

        for(j = 0; j < log2N; j++){
            j2 = j2/2;
            for(;;){
                for(i = 0; i < j2; i++){
                    p = k >> j1;
                    bit = Bit_Reverse(p,log2N);
                    k1 = k + j2;
                    a = data1[k1].r*cos(sign*deg*bit) + data1[k1].i*sin(sign*deg*bit);
                    b = data1[k1].i*cos(sign*deg*bit) - data1[k1].r*sin(sign*deg*bit);
                    data1[k1].r = data1[k].r - a;
                    data1[k1].i = data1[k].i - b;
                    data1[k].r  = data1[k].r + a;
                    data1[k].i  = data1[k].i + b;
                    k++;
                }
                k += j2;
                if (k >= DOTPOINT[log2N - 5]) break;
            }
            k = 0;
            j1--;
        }

        for(k = 0; k < DOTPOINT[log2N - 5]; k++){
            bit = Bit_Reverse(k,log2N);
            if (bit > k)
                FFT_Swap(&data1[k], &data1[bit]);
        }

        for (k = 0; k < DOTPOINT[log2N - 5]; k++){
            data2[k].r = data1[k].r;
            data2[k].i = data1[k].i;
        }
    }

    int m_frame_num_toggle;
    int x_cnt;
    float Spectrogram(short *data, int datapoint, int fft_point)
    {
        int i, j, k, w;
        //--------------------------------------------------------------------------------------------------
        COMPLEX  *data1, *data2;
        double   real, imag;
        //--------------------------------------------------------------------------------------------------
        const int point_num = datapoint;
        //--------------------------------------------------------------------------------------------------
        double* mag_spec = new double[point_num];
        short *window = new short[point_num];
        //--------------------------------------------------------------------------------------------------
        data1 = (COMPLEX *)malloc(sizeof(COMPLEX)*point_num);
        data2 = (COMPLEX *)malloc(sizeof(COMPLEX)*point_num);
        //--------------------------------------------------------------------------------------------------
        set_window(data,window,point_num,0);
        //--------------------------------------------------------------------------------------------------
        for(k = 0,w=0; k < point_num; k++,w++) {
            data1[w].r = window[k];
            data1[w].i = 0.;
        }
        //--------------------------------------------------------------------------------------------------
        GoFFT(LOG2[4], data1, data2, 1);
        //--------------------------------------------------------------------------------------------------
        double maxSpec = -9999;
        int max_index = 0;
        for (i =0 ; i < fft_point/2; i++)
        {
            real = data2[i].r;
            imag = data2[i].i;
            mag_spec[i] = sqrt(real*real + imag*imag);
            if(maxSpec < mag_spec[i]){
                maxSpec = mag_spec[i];
                max_index = i;
            }
        }

        //cout << max_index << endl;
        float fHerz = max_index*31.25 + 2.5;
        //cout << max_index << " - " << fHerz << "Hz" << endl;
        //--------------------------------------------------------------------------------------------------
        // 최초 스펙트럼과의 차감 처리
        //--------------------------------------------------------------------------------------------------
        static double mag_spec_old[512];
        static double max_spec_old;
        static bool bFirst = true;
        if(bFirst){
            for(int y=0; y<fft_point/2; y++){
                mag_spec_old[y] = mag_spec[y];
            }
            max_spec_old = maxSpec;
            bFirst = false;
        }

        for(int y=0; y<fft_point/2; y++){
            mag_spec[y] = fabs(mag_spec_old[y] - mag_spec[y]);
        }
        //--------------------------------------------------------------------------------------------------
        int Col;
        for(int y=0; y<fft_point/2; y++){
            Col = mag_spec[y]*255/maxSpec;
            if( Col > 255) Col = 255;
            if( Col < 1) Col = 1;
            //dc->SetPixel(view_rect.left + x_cnt%view_rect.Width(),
            //             view_rect.bottom - y,PaletteColor[255 - Col]);
            //view_rect.bottom - y*view_rect.Height()/(fft_point/2),PaletteColor[255 - Col]);
        }
        //--------------------------------------------------------------------------------------------------
        //if(m_frame_num_toggle%2 == 0){
        x_cnt++;
        //}
        m_frame_num_toggle++;
        //--------------------------------------------------------------------------------------------------
        delete []mag_spec;
        delete []window;
        delete []data1;
        delete []data2;

        return fHerz;
    }


    void SetColor()
    {
        /*for(int i = 0; i < 86; i++)	PaletteColor[i] = RGB(255,255, 255 - i*3);
        for(int i = 0; i < 86; i++)	PaletteColor[86 + i] = RGB(255, 255 - i*3, 0);
        for(int i = 0; i < 86; i++)	PaletteColor[86*2 + i] = RGB(255 - i*3, 0,0);*/
    }
};

#endif //SOUNDLEADER_SOUNDANALYSIS_H
