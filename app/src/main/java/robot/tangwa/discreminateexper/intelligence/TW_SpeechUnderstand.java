package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

import robot.tangwa.discreminateexper.MyApplication;

/**语音语义理解
 * Created by Administrator on 2017/3/15.
 */

public class TW_SpeechUnderstand {

    private String TAG = MyApplication.TAG;

    private Context context;
    private SpeechUnderstander understander;

    public TW_SpeechUnderstand(Context context){
        this.context = context;
        //1.创建文本语义理解对象
        understander = SpeechUnderstander.createUnderstander(context, null);
        //2.设置参数，语义场景配置请登录http://osp.voicecloud.cn/
        understander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

    }
    public void startUnderstand(){
        //3.开始语义理解
        understander.startUnderstanding(mUnderstanderListener);
    }
    // XmlParser为结果解析类，见SpeechDemo
    private SpeechUnderstanderListener mUnderstanderListener = new SpeechUnderstanderListener(){
        public void onResult(UnderstanderResult result) {
            String text = result.getResultString();
            Log.e(TAG,"语音语义理解输出result："+text);
        }
        public void onError(SpeechError error) {}//会话发生错误回调接口

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
        }
        public void onBeginOfSpeech() {}//开始录音
        public void onVolumeChanged(int volume){} //音量值0~30
        public void onEndOfSpeech() {}//结束录音
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}//扩展用接口
    };




}
