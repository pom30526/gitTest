package com.example.lsm.lasttest.Audio;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

class Complex {
    private double re;   // the real part
    private double im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    public Complex() {
        re = 0.0;
        im = 0.0;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude
    public double abs() {
        return Math.hypot(re, im);
    }

    // return angle/phase/argument, normalized to be between -pi and pi
    public double phase() {
        return Math.atan2(im, re);
    }

    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // return a new object whose value is (this * alpha)
    public Complex scale(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {
        return new Complex(re, -im);
    }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re() { return re; }
    public double im() { return im; }
    public void setRe(double rr) {re = rr;}
    public void setIm(double ii) {im = ii;}

    // return a / b
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }



    // a static version of plus
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    // See Section 3.3.
    public boolean equals(Object x) {
        if (x == null) return false;
        if (this.getClass() != x.getClass()) return false;
        Complex that = (Complex) x;
        return (this.re == that.re) && (this.im == that.im);
    }

    // See Section 3.3.
    public int hashCode() {
        return Objects.hash(re, im);
    }
}

/**
 * Created by Caffe on 2016-12-13.
 */

public class JSoundAnalysis {
    private int m_frame_num_toggle;
    private int x_cnt;
    public int[] DOTPOINT = {32, 64, 128, 256, 512, 1024};
    public int[] LOG2 = {5, 6, 7, 8, 9, 10};

    public JSoundAnalysis()
    {
        x_cnt = 0;
    }

    public float WavToHz(String name) {
        String filename = name;

        JWavFileForIO myWav = new JWavFileForIO(filename);
        try {
            myWav.read();
        } catch (IOException e) {
            return 0.0f;
        }
        //byte []summary = myWav.getSummary();

        int i;
        final int N = 512;

        int nFileSize = 512;
        //printf("nFileSize: %d\n", nFileSize );
        short BUFFER[] = new short[nFileSize/2];

        //memcpy(BUFFER, myWav.myData, nFileSize);
        //------------------------------------------------------------------------------------------
        for(i = 0 ; i < nFileSize/2 ; i++){
            BUFFER[i] = (short) (((short)(myWav.myData[i*2]) << 8) + (short)(myWav.myData[i*2+1]));
            //BUFFER[i] = (short) (((short)(myWav.myData[i*2+1]) << 8) + (short)(myWav.myData[i*2])); // other endian
        }
        //------------------------------------------------------------------------------------------
        //WaveView2(&dc, BUFFER, nFileSize/2, CRect(0,0,400, 100), min_amp, max_amp);//Input Wav View
        //------------------------------------------------------------------------------------------
        int frame_length=256;	/* window size (points) */
        int frame_pitch=60;		/* window sift (points) */
        short ds[] = new short[N]; /* short형 음성 데이터를 double형으로 변환한 것 */
        int num_frame = 0;

        float hz = 0.0f;
        int count = 0;
        int idx, iii;
        ArrayList<Float> hz_info = new ArrayList<Float>();
        ArrayList<Integer> hz_count = new ArrayList<Integer>();
        int nChunkSize = myWav.myChunkSize-44;
        myWav.set_sub(frame_pitch);
        for (i = 0,num_frame = 0 ; i < nChunkSize/2 ; i += frame_pitch, ++num_frame){
            try {
                myWav.next_read(frame_pitch);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(iii = 0 ; iii < nFileSize/2 ; iii++){
                BUFFER[iii] = (short) (((short)(myWav.myData[iii*2]) << 8) + (short)(myWav.myData[iii*2+1]));
                //BUFFER[iii] = (short) (((short)(myWav.myData[iii*2+1]) << 8) + (short)(myWav.myData[iii*2])); // other endian
            }

            set_window(BUFFER,ds,frame_length,0);
            hz = Spectrogram(ds, N, 512); // error!
            Float chk = new Float(hz);
            if (hz_info.contains(chk)) {
                idx = hz_info.indexOf(chk);
                Integer cc = hz_count.get(idx);
                hz_count.set(idx, ++cc);
            }
            else {
                hz_info.add(hz);
                Integer ii = new Integer(1);
                hz_count.add(ii);
            }
        }

        Integer max = hz_count.get(0);
        Float max_hz = hz_info.get(0);
        for (idx = 1; idx < hz_info.size(); idx++) {
            if (max.compareTo(hz_count.get(idx)) < 0) {
                max = hz_count.get(idx);
                max_hz = hz_info.get(idx);
            }
        }

        //std::string hello = "Hello from C++";
        if (max.intValue() == 0) return 0.0f;
        return max_hz.floatValue();
    }

    public float MemToHz(byte[] buffer) {
        if (buffer.length < 512) {
            return -1;
        }
        else if (buffer.length == 512) {
            int i;
            int N = buffer.length;

            int nFileSize = N;
            //printf("nFileSize: %d\n", nFileSize );
            short BUFFER[] = new short[nFileSize / 2];

            //memcpy(BUFFER, myWav.myData, nFileSize);
            //------------------------------------------------------------------------------------------
            for (i = 0; i < nFileSize / 2; i++) {
                BUFFER[i] = (short) (((short) (buffer[i * 2]) << 8) + (short) (buffer[i * 2 + 1]));
            }
            //------------------------------------------------------------------------------------------
            //WaveView2(&dc, BUFFER, nFileSize/2, CRect(0,0,400, 100), min_amp, max_amp);//Input Wav View
            //------------------------------------------------------------------------------------------
            int frame_length = nFileSize / 2;	/* window size (points) */
            //int frame_pitch=60;		/* window sift (points) */
            short ds[] = new short[N]; /* short형 음성 데이터를 double형으로 변환한 것 */
            int num_frame = 0;

            float hz = 0.0f;
            int count = 0;
            int idx, iii;

            set_window(BUFFER, ds, frame_length, 0);
            hz = Spectrogram(ds, N, nFileSize); // error!
            return hz;
        }
        else {
            int i;
            final int N = 512;

            int nFileSize = 512;
            //printf("nFileSize: %d\n", nFileSize );
            short BUFFER[] = new short[nFileSize/2];

            //memcpy(BUFFER, myWav.myData, nFileSize);
            //------------------------------------------------------------------------------------------
            for(i = 0 ; i < nFileSize/2 ; i++){
                BUFFER[i] = (short) (((short)(buffer[i*2]) << 8) + (short)(buffer[i*2+1]));
            }
            //------------------------------------------------------------------------------------------
            //WaveView2(&dc, BUFFER, nFileSize/2, CRect(0,0,400, 100), min_amp, max_amp);//Input Wav View
            //------------------------------------------------------------------------------------------
            int frame_length=256;	/* window size (points) */
            int frame_pitch=60;		/* window sift (points) */
            short ds[] = new short[N]; /* short형 음성 데이터를 double형으로 변환한 것 */
            int num_frame = 0;

            float hz = 0.0f;
            int count = 0;
            int idx, iii;
            ArrayList<Float> hz_info = new ArrayList<Float>();
            ArrayList<Integer> hz_count = new ArrayList<Integer>();
            for (i = 0,num_frame = 0 ; i < buffer.length-512; i += frame_pitch*2, ++num_frame){
                for(iii = 0 ; iii < nFileSize/2 ; iii++){
                    BUFFER[iii] = (short) (((short)(buffer[i+iii*2]) << 8) + (short)(buffer[i+iii*2+1]));
                }

                set_window(BUFFER,ds,frame_length,0);
                hz = Spectrogram(ds, N, 512); // error!
                Float chk = new Float(hz);
                if (hz_info.contains(chk)) {
                    idx = hz_info.indexOf(chk);
                    Integer cc = hz_count.get(idx);
                    hz_count.set(idx, ++cc);
                }
                else {
                    hz_info.add(hz);
                    Integer ii = new Integer(1);
                    hz_count.add(ii);
                }
            }

            Integer max = hz_count.get(0);
            Float max_hz = hz_info.get(0);
            for (idx = 1; idx < hz_info.size(); idx++) {
                if (max.compareTo(hz_count.get(idx)) < 0) {
                    max = hz_count.get(idx);
                    max_hz = hz_info.get(idx);
                }
            }

            //std::string hello = "Hello from C++";
            if (max.intValue() == 0) return 0.0f;
            return max_hz.floatValue();
        }
    }

    public void set_window(short[] s,short[] ds,int n,int mode)
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
                    ds[i]=(short)((s[i])*(0.5-0.5*Math.cos((double)i * wk)));
                }
                break;
            case 2: /* Hamming */
                wk=6.283185307/(double)n;
                for (i=0; i<n; i++) {
                    ds[i]=(short)(s[i]*(0.54-0.46*Math.cos((double)i * wk)));
                }
                break;
            case 3: /* Blackman */
                wk=6.283185307/(double)n;
                for (i=0; i<n; i++) {
                    ds[i]=(short)(s[i]*
                            (0.42-0.5*Math.cos((double)i * wk)+0.08*Math.cos((double)i *wk*2.0)));
                }
                break;
            case 4: /* Bartlett */
                wk=2.0/(double)n;
                for (i=0; i<n/2; i++) {
                    ds[i]=(short)(s[i]*((double)i * wk));
                }
                for (; i<n; i++) {
                    ds[i]=(short)(s[i]*((double)(n-i) * wk));
                }
                break;
        }
    }

    public float Spectrogram(short[] data, int datapoint, int fft_point)
    {
        int i, j, k;
        //--------------------------------------------------------------------------------------------------
        Complex[] data1, data2;
        double   real, imag;
        //--------------------------------------------------------------------------------------------------
        int point_num = datapoint;
        //--------------------------------------------------------------------------------------------------
        double[] mag_spec = new double[point_num];
        short[] window = new short[point_num];
        //--------------------------------------------------------------------------------------------------
        data1 = new Complex[point_num];
        data2 = new Complex[point_num];
        //--------------------------------------------------------------------------------------------------
        set_window(data,window,point_num,0);
        //--------------------------------------------------------------------------------------------------
        for(k = 0; k < point_num; k++) {
            data1[k] = new Complex(window[k], 0.0);
            data2[k] = new Complex();
        }
        //--------------------------------------------------------------------------------------------------
        GoFFT(LOG2[4], data1, data2, 1);
        //--------------------------------------------------------------------------------------------------
        double maxSpec = -9999;
        int max_index = 0;
        for (i =0 ; i < fft_point/2; i++)
        {
            real = data2[i].re();
            imag = data2[i].im();
            mag_spec[i] = Math.sqrt(real*real + imag*imag);
            if(maxSpec < mag_spec[i]){
                maxSpec = mag_spec[i];
                max_index = i;
            }
        }

        //cout << max_index << endl;
        float fHerz = max_index*31.25f + 2.5f;
        //cout << max_index << " - " << fHerz << "Hz" << endl;
        //--------------------------------------------------------------------------------------------------
        // 최초 스펙트럼과의 차감 처리
        //--------------------------------------------------------------------------------------------------
        double[] mag_spec_old = new double[512];
        double max_spec_old;
        boolean bFirst = true;
        if(bFirst){
            for(int y=0; y<fft_point/2; y++){
                mag_spec_old[y] = mag_spec[y];
            }
            max_spec_old = maxSpec;
            bFirst = false;
        }

        for(int y=0; y<fft_point/2; y++){
            mag_spec[y] = Math.abs(mag_spec_old[y] - mag_spec[y]);
        }
        //--------------------------------------------------------------------------------------------------
        int Col;
        for(int y=0; y<fft_point/2; y++){
            Col = (int)(mag_spec[y]*255/maxSpec);
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

        return fHerz;
    }

    public void GoFFT(int log2N, Complex[] data1, Complex[] data2, int sign)
    {
        int     i=0, j=0, p=0, j1=0, j2=0, k=0, k1=0, bit=0;
        double  a=0, b=0, deg=0;

        deg = 2.*Math.PI/DOTPOINT[log2N - 5];
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
                    a = data1[k1].re()*Math.cos(sign*deg*bit) + data1[k1].im()*Math.sin(sign*deg*bit);
                    b = data1[k1].im()*Math.cos(sign*deg*bit) - data1[k1].re()*Math.sin(sign*deg*bit);
                    data1[k1].setRe(data1[k].re() - a);
                    data1[k1].setIm(data1[k].im() - b);
                    data1[k].setRe(data1[k].re() + a);
                    data1[k].setIm(data1[k].im() + b);
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
                FFT_Swap(data1[k], data1[bit]);
        }

        for (k = 0; k < DOTPOINT[log2N - 5]; k++){
            data2[k].setRe(data1[k].re());
            data2[k].setIm(data1[k].im());
        }
    }

    public int Bit_Reverse(int bit, int r)
    {
        int  i, bitr;
        bitr = 0;
        for (i = 0; i < r; i++)
        {
            bitr <<= 1;
            bitr |= (bit&1);
            bit  >>= 1;
        }
        return(bitr);
    }

    public void FFT_Swap(Complex data1, Complex data2)
    {
        double  temp;

        temp     = data1.re();
        data1.setRe(data2.re());
        data2.setRe(temp);
        temp     = data1.im();
        data1.setIm(data2.im());
        data2.setIm(temp);
    }
}
