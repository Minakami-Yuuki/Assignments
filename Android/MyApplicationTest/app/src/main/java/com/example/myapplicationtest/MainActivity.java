package com.example.myapplicationtest;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity{

    private NotificationManager manager;

    private Notification notification;

    private final String TAG = "Misaki";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置系统服务
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 接收通知
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 设置并创建通知等级为最高级
            NotificationChannel channel = new NotificationChannel("misaki", "测试通知", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 设置通知 (三个基本通知条件 标题 内容 图标)
        // 另可添加 (大图标 小图标颜色 跳转意图 点击自动取消)
        notification = new NotificationCompat.Builder(this, "misaki")
                .setContentTitle("Notification!!!")
                .setContentText("Why U Touch Me ???")
                .setSmallIcon(R.drawable.ic_android_red_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3989a7fd74febc4aa4a8fc4827d321))
                .setColor(Color.parseColor("#FF3700B3"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

//        Button button = findViewById(R.id.btn);
//        button.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn:
//                replaceFragment(new BlankFragment1());
//                break;
//        }
//    }
//
//    // 动态切换fragment
//    public void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        // 获取Transaction
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.Test_One, fragment);
//        transaction.commit();
//    }

    public void sendNotification(View view) {
        manager.notify(1, notification);

    }

    public void cancelNotification(View view) {
        manager.cancel(1);
    }

    public void alertClick(View view) {

        // 获取自己创建的视图view
        View dialogView = getLayoutInflater().inflate(R.layout.viewtest, null);

        // 创建AlertBuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置方法 (图标 标题 消息 自定义布局 创建Dialog 显示show 按钮布局)
        builder.setIcon(R.drawable.ic_android_black_24dp)
                .setTitle("Mizore")
                .setMessage("!Liz and Aoi Bird!")
                // 使用listener监听接口方法
                // (确定按钮)
                .setPositiveButton("Nopp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onclick: Noppp!");
                    }
                })
                // (取消按钮)
                .setNegativeButton("Yepp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onclick: Yeppp!");
                    }
                })
                // (中间按钮)
                .setNeutralButton("Midd", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onclick: Middd!");
                    }
                })
                .setView(dialogView)
                .create()
                .show();
    }

    public void popupClick(View view) {

        // 获取视图view
        View popupView = getLayoutInflater().inflate(R.layout.viewtest, null);

        // 设置方法 (内容view 相对控件位置<有无偏移位> 是否获取焦点 设置背景 设置退出)
        // 优先创建位无参方法 (可自行设置参数)
        // 设置 视图内容 window大小 是否通过点击空白处取消显示
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // 设置popWindow背景
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.fe6d98ffedf0c6fc3df4c28d1acebb8));

        // 显示show 可选择携带是否要偏移量 (一般选择在view正下方 anchor)
        popupWindow.showAsDropDown(view);
        // 选择再次点击为关闭状态 (通常在按钮执行结束后采用)
        // popupWindow.dismiss();
    }
}