package com.example.musicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.zhy.m.permission.MPermissions;



import java.io.File;

public class MainActivity extends AppCompatActivity implements Runnable {

    // 开始播放 和 暂停播放的标志位
    int flag = 1;
    // 播放 和 暂停 的内容显示
    private TextView txtInfo;
    // 进度条
    private SeekBar seekBar;
    // 音乐具体服务
    private MusicService musicService;
    // 处理改变进度条事件
    private Handler handler;
    // 自动更新时长
    int UPDATE = 0x101;

    // 歌名
    private EditText song;
    // 开始按钮
    private Button btnStart;
    // 暂停按钮
    private Button btnPause;
    // 继续播放按钮
    private Button btnContinue;
    // 下载按钮
    private Button btnDownload;
    // 下一首按钮
    private Button btnLast;
    // 上一首按钮
    private Button btnNext;
    // 刷新按钮
    private Button refresh;


    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 请求权限 (获取地区信息、读、写 SDcard)
        getPermission();
        musicService = new MusicService();
        try {
            // 读取MP3列表
            setListViewAdapter();
        } catch (Exception e) {
            Log.i("TAG", "Get ListInfo Error!");
        }

        // 开始按钮
        btnStart = findViewById(R.id.btn_star);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 引入flag作为标志
                    // 当flag为 1 时, 此时 Musicplayer 内没有歌曲, 故执行 musicService.play() 函数 (即自动从第一首歌开始播放)
                    // 第一次播放后 flag自增
                    // 再次点击 “开始/暂停” 时 flag > 1 就执行 继续播放 或 暂停播放
                    if (flag == 1) {
                        musicService.play();
                        flag++;
                    }
                    else if (!musicService.player.isPlaying()) {
                        musicService.goPlay();
                    }
                    else if (musicService.player.isPlaying()) {
                        musicService.pause();
                    }
                } catch (Exception e) {
                    Log.i("LAT", "Start Error!");
                }

            }
        });

        // 歌名
        song = findViewById(R.id.song);

        // 下载按钮
        btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取歌曲id
                String pathId = song.getText().toString();
                System.out.println(pathId);
                // 下载地址url
                String pathLast = "http://music.163.com/song/media/outer/url?id=" + pathId +".mp3";
                System.out.println(pathLast);
                // 开启下载的活动
                Intent intent = new Intent(MainActivity.this, DownMusicService.class);
                intent.putExtra("path", pathLast);
                startService(intent);
            }
        });

        // 继续播放按钮
        btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                try {
                    musicService.goPlay();
                    txtInfo.setText("Continue Play!");
                } catch (Exception e) {
                    Log.i("LAT", "Pause Error!");
                }

            }
        });

        // 暂停按钮
        btnPause = findViewById(R.id.btn_stop);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                try {
                    musicService.pause();
                    // 当点击停止按钮时
                    // flag 默认为 1
                    flag = 1;
                    txtInfo.setText("Pause Play!");
                } catch (Exception e) {
                    Log.i("LAT", "Pause Error!");
                }

            }
        });

        // 上一首按钮
        btnLast =  findViewById(R.id.btn_last);
        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    musicService.last();
                } catch (Exception e) {
                    Log.i("LAT", "the Last Music Error!");
                }

            }
        });

        // 下一首按钮
        btnNext =  findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    musicService.next();
                } catch (Exception e) {
                    Log.i("LAT", "the Next Music Error!");
                }

            }
        });

        // 刷新按钮
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 获取最新的当前页面
                    Intent newIntent = getIntent();
                    finish();
                    startActivity(newIntent);
                } catch (Exception e){
                    Log.i("TAG","Refresh Error!");
                }
            }
        });

        // 进度条
        seekBar =  findViewById(R.id.sb);
        // 设置监听 (长度 停止状态 停止时间)
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // 用于监听SeekBar进度值改变时
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            // 用于监听SeekBar开始拖动时
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            // 用于监听SeekBar停止拖动时
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 显示当前进度
                int progress = seekBar.getProgress();
                Log.i("TAG:", "" + progress + "");

                // 得到该首歌曲最长秒数
                int musicMax = musicService.player.getDuration();

                // 跳到该曲该秒
                int seekBarMax = seekBar.getMax();
                musicService.player.seekTo(musicMax * progress / seekBarMax);
            }
        });

        // 歌曲信息
        txtInfo =  findViewById(R.id.tv1);
        // 自动改变进度条的线程
        Thread t = new Thread(this);
        // 实例化一个handler对象 用于处理当前进程
        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 更新UI
                // 获得最大秒数
                int mMax = musicService.player.getDuration();

                // 歌曲名称变更
                if (msg.what == UPDATE) {
                    try {
                        // 获取新进程的进度条 和 歌曲信息 (时长)
                        seekBar.setProgress(msg.arg1);
                        // (ms为单位计算)
                        txtInfo.setText(setPlayInfo(msg.arg2 / 1000, mMax / 1000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    seekBar.setProgress(0);
                    txtInfo.setText("Pause Play!");
                }
            }
        };

        t.start();

    }

    // 定义需要启用的权限数量
    private static final int ACCESS_FINE_LOCATION = 3;

    // 开启权限
    @TargetApi(Build.VERSION_CODES.M)
    private void getPermission() {
        MPermissions.requestPermissions(MainActivity.this, ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        );
    }

    // 向当前ListView 添加 音乐的MP3名称
    private void setListViewAdapter() {
        String[] str = new String[musicService.musicList.size()];
        int i = 0;
        // 将文件路径中的MP3名称读出 并加载到 ListView 中
        for (String path : musicService.musicList) {
            File file = new File(path);
            str[i++] = file.getName();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, str);
        ListView listView = findViewById(R.id.lv1);
        listView.setAdapter(adapter);
    }

    // 线程启动 (同步更新歌曲进度)
    @Override
    public void run() {
        int position, mMax, sMax;
        while (!Thread.currentThread().isInterrupted()) {
            // 若当前线程正在播放歌曲
            if (musicService.player != null && musicService.player.isPlaying()) {
                // 当前歌曲播放进度 (秒)
                position = musicService.getCurrentProgress();
                // 当前歌曲最大秒数
                mMax = musicService.player.getDuration();
                // seekBar的最大值 (需要计算百分比)
                sMax = seekBar.getMax();

                //获取一个Message
                Message m = handler.obtainMessage();
                // seekBar进度条的百分比
                m.arg1 = position * sMax / mMax;
                // 当前位置
                m.arg2 = position;
                // 设置为更新状态
                m.what = UPDATE;
                // 对线程进行处理 (即发送信息告诉活动进行更新)
                handler.sendMessage(m);

                // 每间隔1秒发送一次更新消息
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    // 设置当前播放的信息
    private String setPlayInfo(int position, int max) {
        String info = "正在播放:\t\t" + musicService.songName + "\t\t\t\t";

        // 设置播放时间的跳转逻辑

        // 当前进度：
        int pMinutes = 0;
        // 当前秒数 >= 60
        while (position >= 60) {
            pMinutes++;
            position -= 60;
        }

        // 格式 00:00
        String now = (pMinutes < 10 ? "0" + pMinutes : pMinutes) + ":"
                + (position < 10 ? "0" + position : position);

        // 总进度：
        int mMinutes = 0;
        while (max >= 60) {
            mMinutes++;
            max -= 60;
        }
        String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
                + (max < 10 ? "0" + max : max);

        // 返回格式： 歌名 + 当前播放时间 / 总歌曲时间
        return info + now + " / " + all;
    }

}