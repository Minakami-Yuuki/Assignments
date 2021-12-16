package com.example.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity{

    // 定义一个SharedPreferences对象 (主要用户存储用户名和密码)
    private SharedPreferences pref;
    // 调用SharedPreferences对象的edit()方法来获取一个SharedPreferences.Editor对象
    // 用以添加要保存的数据
    private SharedPreferences.Editor editor;
    private EditText adminEdit;     // 用户名输入框
    private EditText passwordEdit;  // 密码输入框
    private Button login;           // 登陆选项
    private CheckBox savePassword;  // 保存密码复选框
    private CheckBox showPassword;  // 显示或隐藏密码复选框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 获取各组件或对象的实例
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        // 登录按钮 用户名 密码 是否显示和记住密码
        login = findViewById(R.id.login_button);
        adminEdit = findViewById(R.id.admin);
        passwordEdit = findViewById(R.id.password);
        savePassword = findViewById(R.id.save_password);
        showPassword = findViewById(R.id.show_password);

        // 默认为不记住密码
        final boolean isSave = pref.getBoolean("save_password", false);
        // 当 "Rem psw" 勾选时, 从SharedPreferences对象中读出保存的内容, 并显示出来
        if(isSave){
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            adminEdit.setText(account);
            passwordEdit.setText(password);
            // 把光标移到文本末尾处
            adminEdit.setSelection(adminEdit.getText().length());
            passwordEdit.setSelection(passwordEdit.getText().length());
            savePassword.setChecked(true);
        }

        // 用户点击登录时的处理事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String account = adminEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                // 用户名和密码正确
                if(account.equals("Misaki")&&password.equals("20011104")){
                    // 将密码存入pref用于显示密码
                    editor = pref.edit();
                    // "Rem psw" 勾选
                    if(savePassword.isChecked()){
                        editor.putBoolean("save_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    }
                    // 否则pref不存储密码 下次还需要自行输入
                    else{
                        editor.clear();
                    }
                    // 提交进行数据存储
                    editor.apply();

                    // Toast显示登入成功
                    Toast.makeText(Login.this,"Root!",Toast.LENGTH_SHORT).show();
                    // 启动活动
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    // 活动结束
                    finish();
                }
                // 若用户名或密码错误
                // 则Toast显示错误, 请重新输入
                else{
                    Toast.makeText(Login.this,"Error! Plz sign again!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 用户点击 "show psw" 复选框
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击复选框即显示密码
                showOrhidePassword(passwordEdit, showPassword.isChecked());
            }
        });

    }

    // 当用户退出界面时, 检测是否勾选记住密码
    // 若勾选则保存用户输入的用户名及密码
    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor = pref.edit();
        String account = adminEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        // 点击了保存密码则存储账号和密码
        if(savePassword.isChecked()){
            editor.putBoolean("save_password", true);
            editor.putString("account", account);
            editor.putString("password", password);
        }
        // 未点击则清除保存
        else{
            editor.clear();
        }
        editor.apply();
    }

    // 显示或隐藏密码
    private void showOrhidePassword(EditText passwordEdit, boolean isShow){
        // 需要记住光标开始的位置
        int pos = passwordEdit.getSelectionStart();
        if(isShow) {
            passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else {
            passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        passwordEdit.setSelection(pos);
    }

}