package com.dock.androidutils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.culiu.core.utils.debug.DebugLog;
import com.culiu.core.utils.notification.ToastUtils;
import com.culiu.core.utils.string.StringUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTest;

    private EditText mEtPhone;

    private Button mBtnCheckPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTvTest = (TextView) findViewById(R.id.tv_test_method);
        mEtPhone = (EditText) findViewById(R.id.et_phone);

        mBtnCheckPhone = (Button) findViewById(R.id.btn_check_phone);
        mBtnCheckPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = mEtPhone.getText().toString();
                if (!isMatchPwd(phone)) {
                    ToastUtils.showShort(MainActivity.this, "密码必须包含大、小写字母、数字、标点中的至少2种");
                } else {
                    ToastUtils.showShort(MainActivity.this, "密码可以");
                }
            }
        });
        testUtilsMethod();
    }


    private boolean isMatchPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }

        String regex = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,16}$";
        return  pwd.matches(regex);
    }


    private void testUtilsMethod() {
        String testStr = StringUtils.Int2Str(10);
        mTvTest.setText(testStr);
        DebugLog.d("jiangcheng", testStr);
    }
}
