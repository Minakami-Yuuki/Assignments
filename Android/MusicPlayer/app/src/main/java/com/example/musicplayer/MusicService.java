package com.example.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MusicService {

    // 获取SD卡下的音乐目录
    public static final File PATH = Environment.getExternalStoragePublicDirectory("/Music");
    // 存放所有MP3的绝对路径
    public List<String> musicList;
    // 定义多媒体对象 (音乐媒体)
    public MediaPlayer player;
    // 当前播放的歌曲标号
    public int songNum;
    // 当前播放的歌曲名
    public String songName;

    // 引入过滤器 (方便读取文件)
    static class MusicFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            // 返回当前目录所有以.mp3结尾的文件
            return (name.endsWith(".mp3"));
        }
    }

    // 扫描获取文件
    public MusicService() {
        super();
        player = new MediaPlayer();
        // 构造动态存储数组
        musicList = new ArrayList<String>();
        try {
            // 获取Music文件的二级目录
            File MUSIC_PATH = new File(String.valueOf(PATH));
            if (MUSIC_PATH.exists()){
                // 将获取到的所有文件存入数组中
                File[] files = MUSIC_PATH.listFiles(new MusicFilter());
                if (files == null || files.length == 0) {
                    Log.e("TAG", String.format("Data Empty!"));
                    return;
                }
                int length = files.length;
                // 当数组不空时，遍历数组文件
                if (length > 0) {
                    for (File file : Objects.requireNonNull(MUSIC_PATH.listFiles(new MusicFilter()))) {
                        // 将各个文件的绝对路径写入数组中
                        musicList.add(file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            Log.i("TAG", String.format("Load File Error!%s", e.getMessage()));
        }
    }

    // 截取音乐文件名
    // 格式：/A/B/C/xxx.mp3
    public void setPlayName(String dataSource) {
        File file = new File(dataSource);
        String name = file.getName();
        int index = name.lastIndexOf(".");
        songName = name.substring(0, index);
    }

    // 准备播放音乐
    public void play() {
        try {
            // 重置多媒体 (初始化)
            player.reset();
            // 得到当前播放音乐的路径
            String dataSource = musicList.get(songNum);
            // 截取歌名
            setPlayName(dataSource);
            // 指定参数为音频文件
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 为多媒体对象设置播放路径
            player.setDataSource(dataSource);
            // 准备播放
            player.prepare();
            // 开始播放
            player.start();
            // 当前多媒体对象播放完成时的监听时间
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    // 自动下一首歌
                    next();
                }
            });

        } catch (Exception e) {
            Log.v("MusicService", e.getMessage());
        }
    }

    // 继续播放
    public void goPlay() {
        // 获取当前播放进度
        int position = getCurrentProgress();

        // 设置当前MediaPlayer的播放位置 (ms为单位)
        player.seekTo(position);
        try {
            // 装载流媒体文件 准备播放
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.start();
    }

    // 获取当前进度
    public int getCurrentProgress() {
        if(player == null) {
            return 0;
        }
        else {
            return player.getCurrentPosition();
        }
    }

    // 下一首
    public void next() {
        // 若当前为最后一首 则自动跳转至开头歌曲
        // 否则自增为下一首
        songNum = (songNum == musicList.size() - 1 ? 0 : songNum + 1);
        play();
    }

    // 上一首
    public void last() {
        // 若当前为第一首 则自动跳转至结尾歌曲
        // 否则自减为上一首
        songNum = (songNum == 0 ? musicList.size() - 1 : songNum - 1);
        play();
    }

    // 暂停播放
    public void pause() {
        if (player != null && player.isPlaying()) {
            // 获取当前播放进度
            int position = player.getCurrentPosition();
            // 设置存储当前播放进度 (ms为单位)
            player.seekTo(position);
            player.pause();
        }
    }
}


