package robot.tangwa.discreminateexper.intelligencelocal;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;


/**语音合成  字符传变成文字
 * Created by Administrator on 2017/3/15.
 */

public class TW_SpeechSynthesizerLocal {

  //  private String voice_name = MyApplication.VOICE_NAME;

    private Context context;
    private SpeechSynthesizer mTts;


    public TW_SpeechSynthesizerLocal(Context context){
        this.context = context;
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        if(mTts ==null){
            mTts = SpeechSynthesizer.createSynthesizer(context,null);
        }else{
            mTts = SpeechSynthesizer.getSynthesizer();
        }
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL); //设置本地
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
    }
    public void startSpeaking(String speakWords,String voice_name){

        mTts.setParameter(SpeechConstant.VOICE_NAME, voice_name);//设置发音人
        //3.开始合成
        mTts.startSpeaking(speakWords,mSynListener);
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener(){
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {}
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        //开始播放
        public void onSpeakBegin() {}
        //暂停播放
        public void onSpeakPaused() {}
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        //恢复播放回调接口
        public void onSpeakResumed() {}
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
    };




    }
