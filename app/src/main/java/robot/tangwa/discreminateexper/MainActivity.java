package robot.tangwa.discreminateexper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import robot.tangwa.discreminateexper.intelligence.TW_SpeechRecognizer;
import robot.tangwa.discreminateexper.intelligence.TW_SpeechRecognizerASR;
import robot.tangwa.discreminateexper.intelligence.TW_SpeechSynthesizer;
import robot.tangwa.discreminateexper.intelligence.TW_SpeechUnderstand;
import robot.tangwa.discreminateexper.intelligence.TW_TextUnderstand;
import robot.tangwa.discreminateexper.intelligence.UpConstantData;
import robot.tangwa.discreminateexper.intelligence.UpUserwords;
import robot.tangwa.discreminateexper.intelligencelocal.ApkInstaller;
import robot.tangwa.discreminateexper.intelligencelocal.FucUtil;
import robot.tangwa.discreminateexper.intelligencelocal.TW_SpeechRecognizerASRLocal;
import robot.tangwa.discreminateexper.intelligencelocal.TW_SpeechSynthesizerLocal;
import robot.tangwa.discreminateexper.intelligencelocal.TW_SpeedRecognizerLocal;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //初始化变量 or 常量
    public static String TAG=MyApplication.TAG;
    private Context context = MainActivity.this;
    private boolean isUpdate  = true;
    private String voice_name ;
    private String speak_content;

    //初始化对象
    private Button bt_main_listen_write,bt_main_recognizer,bt_main_synthesizer,
            bt_main_speechUnderstand,bt_main_textUnderstand;
    private EditText et_main_voiceName,et_main_speakContent;
    private TextView tv_main_showResult;
    private TW_SpeechRecognizer speechRecognizer;
    public TW_SpeechRecognizer.SuccessCallback successCallback;
    public TW_SpeechRecognizer.FailCallback failCallback;
    private TW_SpeechRecognizerASR mAsr;
    private TW_SpeechSynthesizer mTts;
    private TW_SpeechUnderstand s_understand;
    private TW_TextUnderstand t_understand;
    // 语记安装助手类
    ApkInstaller mInstaller;

    //初始化本地语音
    private TW_SpeedRecognizerLocal mIat_local;
    private TW_SpeechRecognizerASRLocal mAsr_local;
    private TW_SpeechSynthesizerLocal mTts_local;
    //private SpeechWakeup wakeup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, "appid=58c260ad");

        bt_main_listen_write = (Button) findViewById(R.id.bt_main_listen_write);
        tv_main_showResult = (TextView) findViewById(R.id.tv_main_showResult);
        bt_main_recognizer = (Button) findViewById(R.id.bt_main_recognizer);
        bt_main_synthesizer = (Button) findViewById(R.id.bt_main_synthesizer);
        et_main_speakContent = (EditText) findViewById(R.id.et_main_speakContent);
        et_main_voiceName = (EditText)findViewById(R.id.et_main_voiceName);
        bt_main_speechUnderstand = (Button) findViewById(R.id.bt_main_speechUnderstand);
        bt_main_textUnderstand = (Button) findViewById(R.id.bt_main_textUnderstand);
        bt_main_speechUnderstand.setOnClickListener(this);
        bt_main_textUnderstand.setOnClickListener(this);
        bt_main_synthesizer.setOnClickListener(this);
        bt_main_listen_write.setOnClickListener(this);
        bt_main_recognizer.setOnClickListener(this);
        initOnline();//初始化线上识别

//        mInstaller = new ApkInstaller(this);
//        initLocal();
//        check_install_yuji();
        //初始化语音唤醒
        //wakeup = new SpeechWakeup(this);


    }

    private void check_install_yuji() {

        /**
         * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
         */
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            mInstaller.install();
        } else {
            String result = FucUtil.checkLocalResource();
            Log.e(TAG,"检查数据返回结果result:"+result);
        }
    }

    private void initLocal() {
        //初始化本地识别
        //语音听写
       mIat_local = new TW_SpeedRecognizerLocal(context);
        //命令词识别
       mAsr_local = new TW_SpeechRecognizerASRLocal(this);
        //语音合成
        mTts_local = new TW_SpeechSynthesizerLocal(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_main_listen_write:
//                mIat_local.startListener();
               online_iat();
                break;
            case R.id.bt_main_recognizer:
//               mAsr_local.ASR();
                online_asr();
                break;
            case R.id.bt_main_synthesizer:
                voice_name = et_main_voiceName.getText().toString().trim();
                speak_content  = et_main_speakContent.getText().toString();
                mTts_local.startSpeaking(speak_content,voice_name);
               // online_tts();
                break;
            case R.id.bt_main_textUnderstand://文本语义理解

//               online_t_understand();
                break;
            case R.id.bt_main_speechUnderstand://语音语义理解

//                online_s_understand();
                break;
            default:
                break;

        }
    }

    private void online_s_understand() {
        s_understand.startUnderstand();
        //自己创建语义对话框*/
    }

    private void online_t_understand() {
        speak_content = et_main_speakContent.getText().toString().trim();
        t_understand.startUnderstand(speak_content);
    }

    private void online_tts() {
        mTts.startSpeaking(speak_content,voice_name);
        speechRecognizer.showDialog();
        //因为调用默认UI接口就会返回启动监听返回数据
    }

    private void online_asr() {
        SpeechRecognizer.getRecognizer().setParameter(SpeechConstant.SUBJECT,"asr");
        mAsr.ASR();
        speechRecognizer.showDialog();
    }

    private void online_iat() {
        SpeechRecognizer.getRecognizer().setParameter(SpeechConstant.SUBJECT,"iat");
        SpeechRecognizer.getRecognizer().startListening(speechRecognizer.getRecognizerListener());
        speechRecognizer.showDialog();
    }

    private void showReturnData() {
        successCallback = new TW_SpeechRecognizer.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                tv_main_showResult.setText(result);
                Log.e(TAG,"成功返回JSON数据"+result);
            }
        };
        failCallback = new TW_SpeechRecognizer.FailCallback() {
            @Override
            public void onFail(int error_num) {
                tv_main_showResult.setText(error_num+"");
                Log.e(TAG,"失败返回失败码："+error_num);
            }
        };
    }

    public void initOnline(){
//这段处理语音听写方法
        speechRecognizer = new TW_SpeechRecognizer(context);
        showReturnData();
        speechRecognizer.setReturnDataCallback(successCallback,failCallback);//设置监听对象
        //这段是唐娃说话
        mTts = new TW_SpeechSynthesizer(context);
        //这段处理语音识别方法
        mAsr = new TW_SpeechRecognizerASR(context);
        isUpdate = false;
        //这段处理语音语义理解
        s_understand = new TW_SpeechUnderstand(context);
        //这段处理文本语义理解
        t_understand = new TW_TextUnderstand(context);
        if(isUpdate) {
            /*仅支持上传一次*/
            //上传联系人到讯飞服务器，提高识别 精准度
            UpConstantData upConstanData = new UpConstantData(context);
            upConstanData.upload();
            //上传关键字到服务器，提高识别，精准度
            UpUserwords upUserwords = new UpUserwords(context);
            upUserwords.update_userwords();
        }
    }
}
