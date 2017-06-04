package robot.tangwa.discreminateexper;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;



/**全局常量,变量，方法
 * Created by Administrator on 2017/3/10.
 */

public class MyApplication extends Application {

    /*陕西女：vixying 童声男：xiaoxin 童声女：nannan 东北女：xiaoqian
    *湖南男：xiaoqiang 普通老男：vils
    * 法国：Mariane 俄语：Allabent 英文女 catherine 英语男：henry*/
    public static final String VOICE_NAME = "vixying";

    public static final String TAG = "debug";
    public static final String USERWORDS = "唐娃1.5开发";//关键词上传
    public static final String SPEECHCOMMAND = "#ABNF 1.0 UTF-8;" +
            "language zh-CN;";
    public static String grammarId = "";


    //全局方法
    public void showTip(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }



}
