package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import robot.tangwa.discreminateexper.MyApplication;

/**
 * Created by Administrator on 2017/3/14.
 */

public class TW_SpeechRecognizerASR {

    private String TAG = MyApplication.TAG;
    private String mCloudGrammar;
    private int ret;

    private Context context;
    private SpeechRecognizer mAsr;


    public TW_SpeechRecognizerASR(Context context){
        this.context = context;
        mAsr = SpeechRecognizer.createRecognizer(context,null);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mAsr.setParameter(SpeechConstant.SUBJECT,"asr");
        // ABNF语法示例，可以说”北京到上海”
        mCloudGrammar =MyApplication.SPEECHCOMMAND;
        //3.开始识别,设置引擎类型为云端
        //2.构建语法文件
        ret = mAsr.buildGrammar("abnf", mCloudGrammar , grammarListener);
        if (ret != ErrorCode.SUCCESS){
            Log.d(TAG,"语法构建失败,错误码：" + ret);
        }else{
            Log.d(TAG,"语法构建成功");
        }

    }

    //构建语法监听器
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                if (!TextUtils.isEmpty(grammarId)) {
                    //构建语法成功，请保存grammarId用于识别
                    MyApplication.grammarId = grammarId;
                    Log.e(TAG,"输出grammarId为：：：：:"+grammarId);

                } else {
                    Log.d(TAG, "语法构建失败,错误码：：：" + error.getErrorCode());
                }
            }
        }
    };

    public void destory(){
        mAsr.destroy();
    }

    public void ASR(){

        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        Log.e(TAG,"grammarId:"+MyApplication.grammarId);
        //设置grammarId
        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, MyApplication.grammarId);
        ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG,"识别失败,错误码: " + ret);
        }else{
            Log.e(TAG,"识别出来了");
        }
    }


    //实现对象接口
    RecognizerListener mRecognizerListener = new RecognizerListener() {


        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            //音量值：0~30
        }

        @Override
        public void onBeginOfSpeech() {
            //开始录音时调用
        }

        @Override
        public void onEndOfSpeech() {
            //结束录音时调用
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

            /*结果回调*/
            Log.e(TAG,"语音识别返回值result："+recognizerResult.getResultString());
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e(TAG,"语音识别返回错误码code："+speechError.getErrorCode());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            //扩展调用接口
        }
    };

}




