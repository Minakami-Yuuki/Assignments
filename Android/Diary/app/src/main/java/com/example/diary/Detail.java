package com.example.diary;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;


import static com.example.diary.DatabaseHelper.TABLE_NAME;
import static com.example.diary.MainActivity.TAG_INSERT;
import static com.example.diary.MainActivity.TAG_UPDATE;
import static com.example.diary.MainActivity.dbHelper;
import static com.example.diary.MainActivity.getDbHelper;

public class Detail extends  AppCompatActivity {

    private SQLiteDatabase db;  // 数据库
    EditText title;             // 标题
    EditText author;            // 作者
    TextView time;              // 时间
    EditText content;           // 内容
    Button pictureChoice;       // 从相册选择照片
    ImageView picture;          // 选择的照片

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int tag;            // tag 用来区分数据库的插入和更新操作
    private int id;             // 日记id
    private Toolbar toolbar;    // 自建标题栏
    private static final int CHOICE_PHOTO = 2;  // 主活动2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载活动和组件
        setContentView(R.layout.activity_detail);
        toolbar = findViewById(R.id.toolbar);           // Toolbar加载
        setSupportActionBar(toolbar);

        // 临时存放区
        // 获取全局域Preference
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        title = findViewById(R.id.detail_title);        // 标题
        author = findViewById(R.id.detail_author);      // 作者
        time = findViewById(R.id.detail_time);          // 保存时间
        content = findViewById(R.id.detail_content);    // 内容
        pictureChoice =findViewById(R.id.detail_pictureChoice); // 照片选择按钮
        picture = findViewById(R.id.detail_picture);    // 所选照片

        // 默认作者名
        editor = pref.edit();
        editor.putString("author", "Misaki");
        editor.apply();
        String temp_author = pref.getString("author", "");
        author.setText(temp_author);

        // 默认时间格式 (系统当前时间)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        time.setText(simpleDateFormat.format(date));

        // 点击选择照片监听
        pictureChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查是否具有开启相册的权限
                if (ContextCompat.checkSelfPermission(Detail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Detail.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else {
                    openAlbum();
                }
            }
        });

        // 将数据写进数据库
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        tag = intent.getIntExtra("TAG",-1);
        switch(tag){
            // TAG == 1
            case TAG_INSERT:
                break;
            // TAG == 0
            case TAG_UPDATE:
                // 获取新id
                id = intent.getIntExtra("ID",-1);
                // 在原数据库中以每行为代价搜索 id == 新id 的行
                try (Cursor cursor = db.query(TABLE_NAME, null, "id=?",
                        new String[]{String.valueOf(id)}, null, null, null)) {
                    // 进行从数据库行头插入
                    if (cursor.moveToFirst()) {
                        String select_title = cursor.getString(cursor.getColumnIndex("title"));
                        String select_author = cursor.getString(cursor.getColumnIndex("author"));
                        String select_content = cursor.getString(cursor.getColumnIndex("content"));
                        title.setText(select_title);
                        author.setText(select_author);
                        content.setText(select_content);
                        // 特殊：以字节数组存储 Blob类型 的照片文件
                        byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
                        // 以大图形式保存 (部分图片可能会溢出)
                        picture.setImageBitmap(bitmap);

                    }
                }
                break;
            default:
                break;
        }
    }

    // 打开系统相册
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        // 第2个主活动 标明与MainActivity同等关系
        startActivityForResult(intent, CHOICE_PHOTO);
    }

    // 判断是否有权限打开系统相册 (需要在Manifest中赋予相关权限)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else {
                    Toast.makeText(this,"You denied the permission ?",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    // 关于 Android4.4 之前和之后的照片处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOICE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断虚拟机版本号 ( ?> v4.4)
                    if (Build.VERSION.SDK_INT >= 19) {
                        // v4.4 及其以上版本采用此方法处理照片
                        handleImageOnKitKat(data);
                    } else {
                        // v4.4 以下采用此方法处理照片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    // 地址解析:    ( >= v4.4)
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        // 创建URI
        Uri uri = data.getData();

        if(DocumentsContract.isDocumentUri(this, uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);    // 显示所选中照片
    }

    // 地址解析     ( < v4.4)
    private void handleImageBeforeKitKat(Intent data){
        // 创建URI
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        // 显示所选中照片
        displayImage(imagePath);
    }

    // 获取图片的路径
    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection,null, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    // 显示照片
    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            // 以大图显示:
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    // 将menu中的toolbar添加进来 (最上方标题栏)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.menu_main);
        return super.onCreateOptionsMenu(menu);
    }

    // 设置 "Save" 、 "Delete" 、 "Return" 按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // 保存日记
            case R.id.save:
                // 添加日记 (新建)
                if(tag == TAG_INSERT) {
                    // 将所写内容临时保存到 ContextValues 中 (类似于HashTable: key - values)
                    ContentValues values = new ContentValues();
                    values.put("title", title.getText().toString());
                    values.put("author", author.getText().toString());
                    values.put("content", content.getText().toString());
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    // 图片压缩 (默认1 : 1)
                    Bitmap bitmap = ((BitmapDrawable)picture.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    values.put("picture", os.toByteArray());
                    // 将所有数值插入数据库
                    db.insert(TABLE_NAME, null, values);
                    // 清除临时保存的数据
                    values.clear();
                    Toast.makeText(this, "Save!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                // 修改日记 (原有)
                else if(tag == TAG_UPDATE){
                    // 将修改的数据据保存与ContentValues中
                    ContentValues values = new ContentValues();
                    values.put("title", title.getText().toString());
                    values.put("author", author.getText().toString());
                    values.put("content", content.getText().toString());
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    // 图片压缩 (默认1 : 1)
                    Bitmap bitmap = ((BitmapDrawable)picture.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    values.put("picture",os.toByteArray());
                    // 进行更新 (id)
                    db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
                    finish();
                    break;
                }
            // 删除日记
            case R.id.delete:
                // 根据 id 删除当前选中日记
                db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
                Toast.makeText(this, "Delete!", Toast.LENGTH_SHORT).show();
                finish();
                break;
            // 返回页面
            case R.id.goBack:
                finish();
                Toast.makeText(this, "Return!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}