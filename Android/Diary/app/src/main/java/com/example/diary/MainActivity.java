package com.example.diary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.diary.DatabaseHelper.DB_NAME;
import static com.example.diary.DatabaseHelper.TABLE_NAME;
import static com.example.diary.DatabaseHelper.VERSION;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    // 日记标题列表
    private List<String> diary = new ArrayList<>();
    // 插入 (1) 和 更新 (0)
    public static final int TAG_INSERT = 1;
    public static final int TAG_UPDATE = 0;
    // 点中所选项 (标题)
    private String select_item;
    // 日记id
    private int Id;
    // 以Listview排序
    ListView listView;
    // 数组适配器：绑定单一格式数据 (此处未Title)
    ArrayAdapter<String> adapter;
    // 下拉刷新
    private SwipeRefreshLayout swipeRefresh;

    // 获取数据库
    public static DatabaseHelper getDbHelper(){
        return dbHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取主活动各类组件
        Button add = findViewById(R.id.add);
        // 监听 swipe_refresh 设置下拉即刷新页面
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout
                .OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                Toast.makeText(MainActivity.this,"Refresh Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // 创建数据库
        dbHelper = new DatabaseHelper(MainActivity.this, DB_NAME, null, VERSION);
        dbHelper.getWritableDatabase();
        init();

        // 设置日记编辑监听 (用于日记的创建)
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 到detail类中进行插入
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("TAG", TAG_INSERT);
                startActivity(intent);
            }
        });

        // 设置日记更新监听 (用于日记的更新)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 到detail类中进行插入
                Intent intent = new Intent(MainActivity.this, Detail.class);
                Id = getDiaryId(position);
                // 获取需要更新日记的id
                intent.putExtra("ID", Id);
                intent.putExtra("TAG", TAG_UPDATE);
                startActivity(intent);
            }
        });
    }

    private void init(){
        db = dbHelper.getWritableDatabase();
        diary.clear();
        // 查询数据库，先将title一列添加到列表项目中
        // 为了接下来要在主页面用Listview显示所有的日记标题
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String diary_item;
            do{
                diary_item = cursor.getString(cursor.getColumnIndex("title"));
                diary.add(diary_item);
            }while(cursor.moveToNext());
        }
        cursor.close();
        // 用于显示Listview的内容
        adapter = new ArrayAdapter<>(
                MainActivity.this, android.R.layout.simple_list_item_1, diary);
        listView = findViewById(R.id.list_item);
        listView.setAdapter(adapter);
    }

    // 自动刷新列表
    private void refresh(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        init();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    // 获取当前点击的日记id
    private int getDiaryId(int position){
        int Id;
        select_item = diary.get(position);
        // 获取数据库数据
        db = dbHelper.getWritableDatabase();
        // 查表 (目标为当前标题)
        try (Cursor cursor = db.query(TABLE_NAME, new String[]{"id"}, "title=?",
                new String[]{select_item}, null, null, null)) {
            cursor.moveToFirst();
            // 获取id
            Id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return Id;
    }
}
