package robot.tangwa.discreminateexper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/*import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;

*//**
 * Created by Administrator on 2017/3/18.
 *//*

public class SpeechWakeup {

    private StringBuffer param;
    private String resPath;
    private String TAG = MyApplication.TAG;

    private Context context;

    public SpeechWakeup(Context context){
        this.context = context;
        param =new StringBuffer(); //1.加载唤醒词资源，resPath为唤醒资源路径
        resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/58c260ad.jet");

        param.append(ResourceUtil.IVW_RES_PATH+"="+resPath);
        param.append(","+ResourceUtil.ENGINE_START+"="+ SpeechConstant.ENG_IVW);
        SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START,param.toString());
        //2.创建VoiceWakeuper对象
        VoiceWakeuper mIvw = VoiceWakeuper.createWakeuper(context, null);


        //唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD,"0:"+10);
        //设置当前业务类型为唤醒
        mIvw.setParameter(SpeechConstant.IVW_SST,"wakeup");
        //设置唤醒一直保持，直到调用stopListening，传入0则完成一次唤醒后，会话立即结束（默认0）
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE,"1");
        //4.开始唤醒
        mIvw.startListening(mWakeuperListener);
        Log.e(TAG,"初始化成功，语音唤醒");
    }

        //听写监听器
        private WakeuperListener mWakeuperListener = new WakeuperListener() {
        public void onResult(WakeuperResult result) {
            String text = result.getResultString();
            Log.e(TAG,"语音唤醒"+text);
            Log.e(TAG,"收到唤醒消息");
        }
        public void onError(SpeechError error) {}
        public void onBeginOfSpeech() {}
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
        //当使用唤醒+识别功能时获取识别结果
        //arg1:是否最后一个结果，1:是，0:否。
            RecognizerResult reslut = ((RecognizerResult)obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
            Log.e(TAG,reslut.getResultString());
            Log.e(TAG,"收到唤醒消息");
            }
        }

            @Override
            public void onVolumeChanged(int i) {

            }
        };

}*/
