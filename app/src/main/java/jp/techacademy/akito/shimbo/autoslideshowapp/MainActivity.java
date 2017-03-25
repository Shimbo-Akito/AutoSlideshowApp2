package jp.techacademy.akito.shimbo.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity{

    private static final int PERMISSION_REQUEST_CODE = 1;
    boolean playing = false;
    boolean moveToFirst;
    Timer mTimer;
    Cursor cursor;
    Handler mHandler = new Handler();
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button skipbutton =(Button) findViewById(R.id.skipButton);
        skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            //進むボタンの処理
            public void onClick(View v) {
                checkpermission();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if(moveToFirst) {
                            if(cursor.moveToFirst()){
                                showPicture();
                            }
                        }else{
                            skip();
                        }
                    }
                }
            }
        });
        Button backbutton =(Button) findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            //戻るボタンの処理
            public void onClick(View v) {
                checkpermission();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if(moveToFirst) {
                            if(cursor.moveToFirst()){
                                showPicture();
                            }
                        }else{
                            back();
                        }
                    }
                }
            }
        });
        Button playbackbutton =(Button) findViewById(R.id.playbackButton);
        playbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            //再生停止ボタンの処理
            public void onClick(View v) {
                checkpermission();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if(moveToFirst) {
                            if(cursor.moveToFirst()){
                                showPicture();
                            }
                        }
                            playback();

                    }
                }
            }
        });
    }

    public void checkpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                getContentsInfo();
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResult){
        switch(requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                    cursor.moveToFirst();
                    showPicture();
                }
                break;
            default:
                break;
        }
    }

    public void getContentsInfo() {
        if (cursor == null){
            ContentResolver resolver = getContentResolver();
            cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            count = cursor.getCount();

            moveToFirst = true;
        }else{
            moveToFirst = false;
        }
    }

    public void showPicture(){
        count = cursor.getCount();
        if(count == 0){

        }else{
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageview = (ImageView)findViewById(R.id.imageView);
        imageview.setImageURI(imageUri);
        }

    }
    public void skip(){
        if(cursor.moveToNext()){
            showPicture();
        }else{
            cursor.moveToFirst();
            showPicture();
        }
    }
    public void back(){
        if(cursor.moveToPrevious()){
            showPicture();
        }else{
            cursor.moveToLast();
            showPicture();
        }
    }
    public void playback(){
        Button skipbutton = (Button)findViewById(R.id.skipButton);
        Button backbutton = (Button)findViewById(R.id.backButton);
        Button playbackbutton = (Button)findViewById(R.id.playbackButton);
        if(playing != true){
            if(mTimer == null){
                timercreate();
            }else{
                mTimer.cancel();
                timercreate();
            }
            playbackbutton.setText("停止");
            playing = true;
            skipbutton.setEnabled(false);
            backbutton.setEnabled(false);
        }else{
            mTimer.cancel();
            skipbutton.setEnabled(true);
            backbutton.setEnabled(true);
            playing = false;
            playbackbutton.setText("再生");
        }
    }

    public void timercreate(){
        mTimer = new Timer();
        mTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                if(cursor.moveToNext()){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run(){
                            showPicture();
                        }
                    });

                }else{
                    cursor.moveToFirst();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run(){
                            showPicture();
                        }
                    });
                }
            }
        }, 2000,2000);
    }
}
