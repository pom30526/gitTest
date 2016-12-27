#include <jni.h>
#include <string>
#include <map>
#include "SoundAnalysis.h"

using namespace std;

extern "C"
jstring
Java_com_example_lsm_lasttest_NdkTest_stringFromJNI(JNIEnv* env, jobject /* this */)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
jdouble
Java_com_example_lsm_lasttest_NdkTest_WavToHz(JNIEnv* env, jobject obj, jstring jname)
{
    double hz;
    const char* filename = env->GetStringUTFChars(jname, NULL);
    env->ReleaseStringUTFChars(jname, filename);

    WavFileForIO *myWav = new WavFileForIO(filename);
    CSoundAnalysis m_sndAnalysis;

    myWav->read();
    char *summary = myWav->getSummary();

    int i;
    short max_amp;
    short min_amp;
    const int N = 512;

    int nFileSize = myWav->myChunkSize - 44;
    //printf("nFileSize: %d\n", nFileSize );
    short *BUFFER = new short[nFileSize/2];
    memcpy(BUFFER, myWav->myData, nFileSize);
    //------------------------------------------------------------------------------------------
    for(i = 0 ; i < nFileSize/2 ; i++){
        if (BUFFER[i] > max_amp) max_amp = BUFFER[i];
        if (BUFFER[i] < min_amp) min_amp = BUFFER[i];
    }
    //------------------------------------------------------------------------------------------
    //WaveView2(&dc, BUFFER, nFileSize/2, CRect(0,0,400, 100), min_amp, max_amp);//Input Wav View
    //------------------------------------------------------------------------------------------
    int frame_length=256;	/* window size (points) */
    int frame_pitch=60;		/* window sift (points) */
    short ds[N]; /* short형 음성 데이터를 double형으로 변환한 것 */
    int num_frame = 0;
    //double *mag_spectrum = new double[512];
    //COMPLEX  *complex_data = new COMPLEX[512];
    //double maxSpectrum;
    //CRect rtFreq   = CRect(0,100,400, 200);
    //CRect rtSpectrogram = CRect(0, 200, 1200, 200 + 256);
    //------------------------------------------------------------------------------------------
    map<float,int> all;
    for (i= 0,num_frame = 0 ; i < nFileSize/2 ; i += frame_pitch, ++num_frame){
        m_sndAnalysis.set_window(&BUFFER[i],ds,frame_length,0);
        //m_sndAnalysis.PCM2FFT(ds, mag_spectrum, complex_data,  &maxSpectrum, 512, 512);
        //FreqView(&dc, mag_spectrum, maxSpectrum, rtFreq);
        float hz = m_sndAnalysis.Spectrogram(ds, N, 512);
        if (all.find(hz) == all.end()) all[hz] = 1;
        else all[hz]++;
    }

    float max_hz = 0;
    int maxa = 0;

    for (auto a : all) {
        if (maxa < a.first) {
            maxa = a.first;
            max_hz = a.second;
        }
    }

    //std::string hello = "Hello from C++";
    return max_hz;
}