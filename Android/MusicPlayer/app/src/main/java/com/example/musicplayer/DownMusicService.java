package com.example.musicplayer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class DownMusicService extends IntentService {

    // 设置音乐存储的相对地址 (位于 SDcard 上)
    public static final File PATH = Environment.getExternalStoragePublicDirectory("/Music");
    // 下载音乐的名称
    private String fileName;

    protected void onHandleIntent(Intent intent) {

        final String url = intent.getStringExtra("path");
        System.out.println(url);
        // 获取音乐的id
        String str = url.substring(0, url.indexOf(".mp3"));
        fileName = str.substring(str.lastIndexOf("=") + 1);

        // 设置开始下载的时间
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);

        // 建立http请求
        // 开启下载
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {

            // 若下载失败
            @Override
            public void onFailure( Call call, IOException e) {
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
                // 刷新当前页面UI
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable(){
                    public void run(){
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            // 成功响应
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // 设置缓冲区 准备写流数据
                BufferedSink bufferedSink = null;
                try {
                    // 设置文件路径 和 存储信息
                    File dest = new File(PATH, fileName + ".mp3");
                    // sink进行数据流写入
                    Sink sink = Okio.sink(dest);
                    // 写缓存
                    bufferedSink = Okio.buffer(sink);
                    assert response.body() != null;
                    // 缓存写入
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD", "download success");
                    Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));

                    // 刷新当前页面UI
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                            public void run() { Toast.makeText(getApplicationContext(), "下载成功!", Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("DOWNLOAD", "download failed");
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "下载失败!", Toast.LENGTH_LONG).show();
                            }
                        });

                    } finally {
                        if (bufferedSink != null) {
                            // 关闭缓存
                            bufferedSink.close();
                        }
                    }
                }
        });

        Intent intentNew = new Intent(DownMusicService.this, MainActivity.class);
        // 切换回主活动
        intentNew.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentNew);
    }


    public DownMusicService() {
        super("");
    }
}