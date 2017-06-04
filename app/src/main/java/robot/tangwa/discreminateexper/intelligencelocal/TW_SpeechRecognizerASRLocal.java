package robot.tangwa.discreminateexper.intelligencelocal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ContactManager;

import robot.tangwa.discreminateexper.MyApplication;

/**
 * Created by Administrator on 2017/3/14.
 */

public class TW_SpeechRecognizerASRLocal {

    private String TAG = MyApplication.TAG;
    private int ret;

    private Context context;
    private SpeechRecognizer mAsr;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地词典
    private String mLocalLexicon = null;
    private String contents;
    private boolean isGrammar = true;

    private static final String GRAMMAR_TYPE_BNF = "bnf";


    public TW_SpeechRecognizerASRLocal(Context context) {
        this.context = context;
        //1.创建SpeechRecognizer对象，需传入初始化监听器
        mAsr = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.e(TAG, "初始化：" + i);
            }
        });
        mLocalLexicon = "张海羊\n刘婧\n王锋\n";
        mLocalGrammar = FucUtil.readFile(context, "call.bnf", "utf-8");
        // 获取联系人，本地更新词典时使用
        ContactManager mgr = ContactManager.createManager(context, mContactListener);
        mgr.asyncQueryAllContactsName();

    }


    public void ASR() {
        //每次点击之前需要清空一下本地参数，进行切换识别&听写，可以加一个判断，全局变量用于节省资源
        mAsr.setParameter(SpeechConstant.PARAMS,null);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //指定引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //这个参数很重要，不能没有，也不能写成GRAMMAR_LIST ：指定更新的识别文件
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");//指定准备去识别的文件
        mAsr.setParameter(SpeechConstant.ASR_THRESHOLD, "30");
        mAsr.setParameter(SpeechConstant.SUBJECT,"asr");
        if(isGrammar){

            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            //指定引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            contents = new String(mLocalGrammar);
            Log.e(TAG,"grammar:"+contents);
            ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, contents, mLocalGrammarListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.e(TAG,"本地语法构建失败");
            }else{
                Log.e(TAG,"本地语法构建成功");
            }
            isGrammar = false;
        }
        mAsr.setParameter(SpeechConstant.SUBJECT,"asr");
        ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "识别失败,错误码: " + ret);
        } else {
            Log.e(TAG, "识别出来了");
        }
    }

    /**
     * 更新词典监听器。
     */
    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
                Log.e(TAG, "词典更新成功");
            } else {
                Log.e(TAG, "词典更新失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 本地构建语法监听器。
     */
    private GrammarListener mLocalGrammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                Log.e(TAG, "语法构建成功：" + grammarId);
                contents = new String(mLocalLexicon);
                Log.e(TAG,"lexicon:"+contents);
                mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
                mAsr.setParameter(SpeechConstant.GRAMMAR_LIST, "call");
                ret = mAsr.updateLexicon("<contact>", contents, mLexiconListener);
                if (ret != ErrorCode.SUCCESS) {
                    if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                        //未安装则跳转到提示安装页面
                        Log.e(TAG, "没有下载语记");
                    } else {
                        Log.e(TAG, "新词典失败,错误码：" + ret);
                    }
                }
            }else{
                Log.e(TAG,"语法构建失败");
            }
        }};

        /**
         * 获取联系人监听器。
         */
        private ContactManager.ContactListener mContactListener = new ContactManager.ContactListener() {
            @Override
            public void onContactQueryFinish(String contactInfos, boolean changeFlag) {
                //获取联系人
                mLocalLexicon = contactInfos;
            }
        };
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
                Log.e(TAG, "语音识别返回值result：" + recognizerResult.getResultString());
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e(TAG, "语音识别返回错误码code：" + speechError.getErrorCode());
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
                //扩展调用接口
            }
        };

}




