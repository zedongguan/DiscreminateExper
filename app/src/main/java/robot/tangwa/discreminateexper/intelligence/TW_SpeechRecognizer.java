package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import robot.tangwa.discreminateexper.MyApplication;


/**唐娃的语音识别
 *     基于讯飞识别进行处理和接口化封装
 * Created by Administrator on 2017/3/13.
 */

public class TW_SpeechRecognizer{

    private String TAG = MyApplication.TAG;
    private Context context;
    private String result;

    private RecognizerDialog mDialog;
    private SuccessCallback successCallback;
    private FailCallback failCallback;
    private SpeechRecognizer mIat;


    public TW_SpeechRecognizer(Context context){
        this.context = context;
        //初始化对象
        mIat= SpeechRecognizer.createRecognizer(context,null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        initDialog();
    }

    public void destory(){
        mIat.destroy();
    }

    //实现对象接口
    RecognizerListener recognizerListener = new RecognizerListener() {


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
            //默认返回JSON数据结果//b：是否为最后一个结果  true：是，false：否
            Log.e(TAG,"语音听写返回值result："+recognizerResult.getResultString());
            successCallback.onSuccess(recognizerResult.getResultString());
        }

        @Override
        public void onError(SpeechError speechError) {
            //发生错误调用函数
            Log.e(TAG,"语音听写返回值error："+speechError.getErrorCode());
            failCallback.onFail(speechError.getErrorCode());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            //扩展调用接口
        }
    };

    /**
     * 调用讯飞初始化识别界面
     * */
    public void initDialog() {

        mDialog = new RecognizerDialog(context,null);
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        mDialog.setListener(new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.e("debug","返回的数据位result：：："+recognizerResult.getResultString());
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("debug","返回的错误码error：：：："+speechError.getErrorCode());
        }
    });
        mDialog.setListener(null);
    }
    public void showDialog(){
        mDialog.show();
    }

    public RecognizerListener getRecognizerListener(){
        return recognizerListener;
    }

    public void setReturnDataCallback(SuccessCallback successCallback, FailCallback failCallback) {
        this.successCallback = successCallback;
        this.failCallback = failCallback;
    }

    public interface SuccessCallback{
        void onSuccess(String result);
    }

    public interface FailCallback{
        void onFail(int error_num);
    }


}
