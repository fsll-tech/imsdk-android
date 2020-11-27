package sdk.im.qunar.com.xiaoxun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qunar.im.ui.activity.TabMainActivity;
import com.qunar.im.ui.sdk.QIMSdk;

import sdk.im.qunar.com.xiaoxun.permission.OnPermissionListener;
import sdk.im.qunar.com.xiaoxun.permission.PermissionX;


public class MainActivity extends AppCompatActivity {

    private Button autoLoginButton,startPlatForm;
    private TextView logcat_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this,TabMainActivity.class));
//        setContentView(R.layout.activity_main);
//
//        autoLoginButton = (Button) findViewById(R.id.autoLoginButton);
//        startPlatForm = (Button) findViewById(R.id.startPlatForm);
//        logcat_text = (TextView) findViewById(R.id.logcat_text);
//
//        startPlatForm.setText("启动" + CommonConfig.currentPlat);
    }

    /**
     * 初始化sdk
     *只在Application 里调用一次
     * @param view
     */
    public void initQIMSdk(View view) {
        toast("已初始化");
//        QIMSdk.getInstance().init(getApplication());
    }

    /**
     * 初始化导航配置
     *
     * @param view
     */
    public void configNavigation(View view) {
        String url = sdk.im.qunar.com.xiaoxun.UserInfo.NAV;//导航URl
        if(TextUtils.isEmpty(url)){
            toast("请配置正确的导航地址");
            return;
        }
        QIMSdk.getInstance().setNavigationUrl(url);
        toast("导航配置成功");
        logcat_text.append("导航地址：" + url + "\n");
    }

    /**
     * 登录
     *
     * @param view
     */
    public void login(View view) {
        if (!QIMSdk.getInstance().isConnected()){
            final ProgressDialog pd = ProgressDialog.show(this, "提示", "正在登录中。。。");
            if(QIMSdk.getInstance().isCanAutoLogin()){
                QIMSdk.getInstance().autoLogin((b, s) -> {
                    logcat_text.append(s);
                    pd.dismiss();
                    autoLoginButton.setText(s);
                    toast(s);
                });
            }else {
                final String uid = UserInfo.UID;//用户名
                final String password = UserInfo.PASSWORD;//密码
                QIMSdk.getInstance().login(uid, password, (b, s) -> {
                    logcat_text.append("Uid：" + uid + "\n" + "Password：" + password);
                    pd.dismiss();
                    autoLoginButton.setText(s);
                    toast(s);
                });
            }

        }
        else
            toast("已登录！");
    }

    /**
     * 普通二人会话
     * @param view
     */
    public void goToChat(View view){
        PermissionX.with(this)
                .storage()
                .setPermissionListener(new OnPermissionListener() {
                    @Override
                    public void onDenied() {

                    }

                    @Override
                    public void onGranted() {
                        QIMSdk.getInstance().goToChatConv(MainActivity.this,"jid",0);
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .start();
    }

    /**
     * 群会话
     * @param view
     */
    public void goToGroup(View view){
        QIMSdk.getInstance().goToGroupConv(this,"groupid",1);
    }

    public void startMainActivity(View view) {

        startActivity(new Intent(this,TabMainActivity.class));
    }

    /**
     * 会话页
     * @param view
     */
    public void startConversationActivity(View view){
        startActivity(new Intent(this,ConversationListActivity.class));
    }

    private void toast(final String msg) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
    }
}