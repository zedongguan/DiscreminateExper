package robot.tangwa.discreminateexper.intelligencelocal;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


import robot.tangwa.discreminateexper.MyApplication;

/**要使用语音+语记（第三方App）的方式进行离线听写
 * Created by Administrator on 2017/3/15.
 */

public class TW_SpeedRecognizerLocal {

    private String TAG = MyApplication.TAG;
    private int ret;

    private Context context;
    private SpeechRecognizer mIat;
    private RecognizerDialog mDialog;



    public TW_SpeedRecognizerLocal(Context context){
        this.context = context;
        //1.创建SpeechRecognizer对象，需传入初始化监听器
        mIat = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.e(TAG,"你好世界");
            }
        });
      /*  mDialog = new RecognizerDialog(context,null);
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                Log.e(TAG,"成功返回数据result:"+recognizerResult.getResultString());
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e(TAG,"听写返回数据error:"+speechError.getErrorCode());
            }
        });*/

                //2.构建语法（本地识别引擎目前仅支持BNF语法），方法同在线语法识别，详见Demo
                // 清空参数


    }

    public void startListener(){

        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mIat.setParameter(SpeechConstant.RESULT_TYPE,"json");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        mIat.setParameter(SpeechConstant.SUBJECT,"iat");
        mIat.setParameter(SpeechConstant.SUBJECT,"iat");
        ret = mIat.startListening(recognizerListener);
        //mDialog.show();//这里识别被打断
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
        }

        @Override
        public void onError(SpeechError speechError) {
            //发生错误调用函数，这里调用被打断，调用一次本地，有调用一次外部，
            Log.e(TAG,"语音听写返回值error："+speechError.getErrorCode());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            //扩展调用接口
        }
    };



}
