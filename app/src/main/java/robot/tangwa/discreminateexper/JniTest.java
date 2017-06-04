package robot.tangwa.discreminateexper;

/**
 * Created by Administrator on 2017/3/10.
 */

public class JniTest {

    static{
        System.loadLibrary("jnitest");
    }

    public static native String hello_jni();


}
