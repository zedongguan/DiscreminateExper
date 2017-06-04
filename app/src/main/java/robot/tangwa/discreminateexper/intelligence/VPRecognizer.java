package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

import robot.tangwa.discreminateexper.MyApplication;

/**基于科大讯飞的声纹识别
 * Created by Administrator on 2017/3/19.
 */

public class VPRecognizer {
    private static final String TAG = MyApplication.TAG;

    private static final int PWD_TYPE_TEXT = 1;
    // 自由说由于效果问题，暂不开放
//	private static final int PWD_TYPE_FREE = 2;
    private static final int PWD_TYPE_NUM = 3;
    // 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
    private int mPwdType = PWD_TYPE_TEXT;
    private Context context;
    // 声纹识别对象
    private SpeakerVerifier mVerifier;
    // 声纹AuthId，用户在云平台的身份标识，也是声纹模型的标识
    // 请使用英文字母或者字母和数字的组合，勿使用中文字符
    private String mAuthId = "child_1";
    // 文本声纹密码
    private String mTextPwd = "芝麻开门";
    // 数字声纹密码  8段数字
    private String mNumPwd = "";
    // 数字声纹密码段，默认有5段
    private String[] mNumPwdSegs;


    public VPRecognizer(Context context){
        this.context = context;
        // 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
        mVerifier = SpeakerVerifier.createVerifier(context,mInitListener);
        // 通过setParameter设置密码类型，pwdType的取值为1、2、3，分别表示文本密码、自由说和数字密码
        Log.e(TAG,"mverifier:"+mVerifier);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
    }

    public void speech_verify(){
        mVerifier.setParameter(SpeechConstant.PARAMS,null);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
        mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
        // 首先设置声纹密码类型
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        // 对于文本和数字密码，必须设置声纹注册时用的密码文本，pwdText的取值为“芝麻开门”或者是从云平台拉取的数字密码。自由说略过此步
        mVerifier.setParameter(SpeechConstant.ISV_PWD, "芝麻开门");
        // 调用sendRequest方法查询或者删除模型，cmd的取值为“que”或“del”，表示查询或者删除，auth_id是声纹对应的用户标识，操作结果以异步方式回调SpeechListener类型对象listener的onBufferReceived方法进行处理，处理方法详见Demo示例
        mVerifier.sendRequest("que", mAuthId,  mPwdListenter);
        Log.e(TAG,"mAuthId"+mAuthId);

    }
    public void del_speech_verify(){
        mVerifier.setParameter(SpeechConstant.PARAMS,null);
        mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
        // 首先设置声纹密码类型
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        // 对于文本和数字密码，必须设置声纹注册时用的密码文本，pwdText的取值为“芝麻开门”或者是从云平台拉取的数字密码。自由说略过此步
        mVerifier.setParameter(SpeechConstant.ISV_PWD, "芝麻开门");
        // 调用sendRequest方法查询或者删除模型，cmd的取值为“que”或“del”，表示查询或者删除，auth_id是声纹对应的用户标识，操作结果以异步方式回调SpeechListener类型对象listener的onBufferReceived方法进行处理，处理方法详见Demo示例
        mVerifier.sendRequest("del", mAuthId,  mPwdListenter);
        Log.e(TAG,"mAuthId"+mAuthId);


    }
    public void speech_register(){
        mVerifier.setParameter(SpeechConstant.PARAMS,null);
        // 设置业务类型为训练
        mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
        // 设置密码类型
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
    /* 对于文本密码和数字密码，必须设置密码的文本内容，pwdText的取值为“芝麻开门
     ”或者是从云端拉取的数字密码(每8位用“-”隔开，如“62389704-45937680-32758406-29530846-58206497”)
    。自由说略过此步*/
        mVerifier.setParameter(SpeechConstant.ISV_PWD, "芝麻开门");
        // 设置声纹对应的auth_id，它用于标识声纹对应的用户，为空时表示这是一个匿名用户
        mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
        /* 开始注册，当得到注册结果时，SDK会将其封装成VerifierResult对象，
        回调VerifierListener对象listener的onResult方法进行处理，处理方法详见Demo示例*/
        mVerifier.startListening(mVerifyListener);//开始注册
    }
    VerifierListener mVerifyListener =new VerifierListener() {
        public void onVolumeChanged(int volume) {}
        public void onResult(VerifierResult result) {
            if (result.ret == 0) {
                // 验证通过
                Log.e(TAG,"声纹识别结果："+"验证通过");
            }
            else {
                // 验证不通过
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        Log.e(TAG, "声纹识别结果：" + "内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        Log.e(TAG, "声纹识别结果：" + "出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        Log.e(TAG, "声纹识别结果：" + "太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        Log.e(TAG, "声纹识别结果：" + "录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        Log.e(TAG, "声纹识别结果：" + "验证不通过，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        Log.e(TAG, "声纹识别结果：" + "音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        Log.e(TAG, "声纹识别结果：" + "音频长达不到自由说的要求");
                        break;
                    default:
                        Log.e(TAG, "声纹识别结果：" + "验证不通过");
                        break;
                }
            }
        }
        public void onError(SpeechError error) {
            switch (error.getErrorCode()) {
                case ErrorCode.MSP_ERROR_NOT_FOUND:
                    Log.e(TAG, "声纹识别结果：" + "模型不存在，请先注册");
                    break;

                default:
                    Log.e(TAG, "声纹识别结果：" +  error.getPlainDescription(true));
                    break;
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            Log.e(TAG, "先预留");
        }

        public void onEndOfSpeech() {
            Log.e(TAG, "结束说话");
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            //Log.e(TAG, "音量改变");

        }

        public void onBeginOfSpeech() {
            Log.e(TAG, "开始说话");
        }};

    /*
    * 获取到声纹密码后，就要进行声纹训练，进行注册*/
    public void getPwd() {

        // 通过调用getPasswordList方法来获取密码。mPwdListener是一个回调接口，当获取到密码后，SDK会调用其中的onBufferReceived方法对云端返回的JSON格式（具体格式见附录12.4）的密码进行处理，处理方法详见声纹Demo示例
        mVerifier.getPasswordList(mPwdListenter);

    }


    private SpeechListener mPwdListenter = new SpeechListener() {
        public void onEvent(int eventType, Bundle params) {
        }

        public void onBufferReceived(byte[] buffer) {
            mTextPwd = new String(buffer);
            Log.e(TAG,"获取到的声纹密码"+mTextPwd);
        }

        public void onCompleted(SpeechError error) {
            Log.e(TAG,"获取密码完成or验证完成");
        }
    };


    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (ErrorCode.SUCCESS == i) {
                Log.e(TAG,"引擎初始化成功");
            } else {
                Log.e(TAG,"引擎初始化失败，错误码：" + i);
            }
        }
    };



}
