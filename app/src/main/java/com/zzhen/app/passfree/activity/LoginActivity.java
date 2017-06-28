package com.zzhen.app.passfree.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zzhen.app.passfree.R;
import com.zzhen.app.passfree.services.UserServices;
import com.zzhen.app.passfree.util.Constant;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private EditText edt_username;
    private EditText edt_password;
    private Button btn_login;
    private Button btn_register;

    private Intent intent;

    private UserServices userServices;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userServices = UserServices.getInstance(LoginActivity.this);
        edt_username = (EditText)findViewById(R.id.et_username);
        edt_password = (EditText)findViewById(R.id.et_password);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);

        intent = new Intent(LoginActivity.this, CodeActivity.class);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                int ret = userServices.login(username, password);

                switch(ret){
                    case Constant.PARAM_NULL:
                        Toast.makeText(LoginActivity.this, "Parameter is null, please input username and password", Toast.LENGTH_SHORT).show();
                        break;

                    case Constant.LOGIN_FAILED_NOT_REGISTER:
                        Toast.makeText(LoginActivity.this, "You have not registered, please register first", Toast.LENGTH_SHORT).show();
                        break;
                    case Constant.WRONG_PASSWORD:
                        Toast.makeText(LoginActivity.this, "Wrong User Name or Password ", Toast.LENGTH_SHORT).show();
                        break;
                    case Constant.LOGIN_SUCCESS:
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        loginHandle(username);
                        break;
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                int ret = userServices.register(username, password);

                switch(ret){
                    case Constant.PARAM_NULL:
                        Toast.makeText(LoginActivity.this, "Parameter is null, please input username and password", Toast.LENGTH_SHORT).show();
                        break;

                    case Constant.HAS_BEEN_REGISTERED:
                        Toast.makeText(LoginActivity.this, "You have registered, please login", Toast.LENGTH_SHORT).show();
                        break;
                    case Constant.INSERT_DATA_FAILED:
                        Toast.makeText(LoginActivity.this, "Insert data failed", Toast.LENGTH_SHORT).show();
                        break;
                    case Constant.REGISTER_SUCCESS:
                        Toast.makeText(LoginActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
    }

    private void loginHandle(String username){
        Long userid = userServices.getUserIDByUserName(username);
        if(userid.equals(0L) || userid == 0L){
            Log.e(TAG, "user login error");
            Toast.makeText(LoginActivity.this, "User login failed", Toast.LENGTH_SHORT).show();
            return;
        }

        intent.putExtra(getString(R.string.USER_ID), userid);

        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(LoginActivity.this, "Click Again to Exit Application", Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
