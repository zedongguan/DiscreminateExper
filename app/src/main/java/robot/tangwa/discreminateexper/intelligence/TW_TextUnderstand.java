package robot.tangwa.discreminateexper.intelligence;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

import robot.tangwa.discreminateexper.MyApplication;

/**文本语义理解
 * Created by Administrator on 2017/3/15.
 */

public class TW_TextUnderstand {

    private String TAG = MyApplication.TAG;

    private Context context;
    private TextUnderstander mTextUnderstander;


    public TW_TextUnderstand(Context context){
        //创建文本语义理解对象
        this.context = context;
        mTextUnderstander = TextUnderstander.createTextUnderstander(context, null);
    }


    public void startUnderstand(String contents){
        //开始语义理解
        mTextUnderstander.understandText(contents, searchListener);
    }

    //初始化监听器
    TextUnderstanderListener searchListener = new TextUnderstanderListener(){
    //语义结果回调
    public void onResult(UnderstanderResult result){
        Log.e(TAG,"文本语义理解返回值result:"+result.getResultString());
    }
    //语义错误回调
    public void onError(SpeechError error) {
        Log.e(TAG,"文本语义理解返回错误码error:"+error.getErrorCode());
    }};

}
