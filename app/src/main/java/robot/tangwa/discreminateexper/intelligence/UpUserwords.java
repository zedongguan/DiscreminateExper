package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import robot.tangwa.discreminateexper.MyApplication;

import static android.content.ContentValues.TAG;

/**上传说识别关键字，
 * Created by Administrator on 2017/3/14.
 */

public class UpUserwords {

    private String contents = MyApplication.USERWORDS;
    private int ret;

    private Context context;
    private SpeechRecognizer mIat;


    public UpUserwords(Context context){
        this.context = context;
        mIat = SpeechRecognizer.getRecognizer();
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //指定引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    public void update_userwords(){
        ret = mIat.updateLexicon("userword", contents, lexiconListener);
        if(ret != ErrorCode.SUCCESS){
            Log.d(TAG,"上传用户词表失败：" + ret);
        }
    }
    //上传用户词表监听器。
    private LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if(error != null){
                Log.d(TAG,error.toString());
            }else{
                Log.d(TAG,"上传成功！");
            }
        }
    };


}
