package robot.tangwa.discreminateexper;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.cloud.SpeechUtility;

import robot.tangwa.discreminateexper.intelligence.VPRecognizer;

/**学习科大讯飞声纹和人脸识别
 * Created by Administrator on 2017/3/19.
 */

public class LoginActivity extends Activity implements View.OnClickListener{

    private Button bt_login_register,bt_login_getPwd,bt_login_verify,bt_login_delSV;
    private EditText et_login_username,et_login_pwd;
    private TextView tv_login_show;
    private VPRecognizer speechVerify;

    private String username,pwd;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        SpeechUtility.createUtility(this, "appid=58c260ad");

        bt_login_getPwd = (Button) findViewById(R.id.bt_login_getPwd);
        bt_login_verify = (Button) findViewById(R.id.bt_login_verify);
        bt_login_register = (Button) findViewById(R.id.bt_login_register);
        bt_login_delSV = (Button) findViewById(R.id.bt_login_delSV);
        bt_login_delSV.setOnClickListener(this);
        bt_login_getPwd.setOnClickListener(this);
        bt_login_verify.setOnClickListener(this);
        bt_login_register.setOnClickListener(this);
        et_login_username = (EditText) findViewById(R.id.et_login_username);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        tv_login_show = (TextView) findViewById(R.id.tv_login_show);
        initSpeechVerify();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_login_getPwd:
                getPwd();
                break;
            case R.id.bt_login_register:
                register();
                break;
            case R.id.bt_login_verify:
                verify();
                break;
            case R.id.bt_login_delSV:
                del_verify();
                break;
            default:
                break;
        }
    }

    private void del_verify() {
        speechVerify.del_speech_verify();
    }

    private void initSpeechVerify() {
        speechVerify = new VPRecognizer(this);
    }

    private void verify() {
        speechVerify.speech_verify();
    }

    private void register() {
        speechVerify.speech_register();
    }

    private void getPwd() {
        speechVerify.getPwd();
    }


}
