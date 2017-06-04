package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ContactManager;

import robot.tangwa.discreminateexper.MyApplication;


/**上传联系人数据   使语音识别更精准
 * Created by Administrator on 2017/3/13.
 */

public class UpConstantData {
    private String TAG = MyApplication.TAG;
    private int ret;

    private Context context;
    private SpeechRecognizer mIat;

    public UpConstantData(Context context) {
        this.context = context;
        mIat = SpeechRecognizer.getRecognizer();
    }
    //上传联系人监听器。
    private LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                Log.d(TAG, error.toString());
            } else {
                Log.d(TAG, "手机联系人上传成功！");
            }
        }
    };
    public void upload() {
        //获取ContactManager实例化对象
        ContactManager mgr = ContactManager.createManager(context, new ContactManager.ContactListener() {
            @Override
            public void onContactQueryFinish(String contactInfos, boolean b) {
                //指定引擎类型
                mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
                ret = mIat.updateLexicon("contact", contactInfos, lexiconListener);
                if(ret != ErrorCode.SUCCESS){
                    Log.d(TAG,"上传联系人失败：" + ret);
                }
            }
        });
        mgr.asyncQueryAllContactsName();
    }
}
