package com.example.harrisdong.surfaceviewadmediaplayer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maning.mnvideoplayerlibrary.listener.OnCompletionListener;
import com.maning.mnvideoplayerlibrary.listener.OnNetChangeListener;
import com.maning.mnvideoplayerlibrary.listener.OnScreenOrientationListener;
import com.maning.mnvideoplayerlibrary.player.MNViderPlayer;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MinActivity";
    private final String Url1 = "http://mp4.vjshi.com/2016-12-22/e54d476ad49891bd1adda49280a20692.mp4";
    private final String Url2 = "http://mp4.vjshi.com/2016-12-22/e54d476ad49891bd1adda49280a20692.mp4";
    //这个地址是错误的
    private final String url3 = "http://weibo.com/p/23044451f0e5c4b762b9e1aa49c3091eea4d94";
    //本地视频
    private final String url4 = "/storage/emulated/0/Movies/Starry_Night.mp4";
    private MNViderPlayer mnViderPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initPlayer();
//        请求权限调节亮度
        requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("视频调节亮度需要访问权限");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        Uri.parse("package" + getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 100);
                    }
                });
                builder.show();
            }
        }
    }

    private void initPlayer() {
//        设置宽高比
        mnViderPlayer.setWidthAndHeightProportion(16, 9);
//        设置电量监听
        mnViderPlayer.setIsNeedBatteryListen(true);
//        设置网络监听
        mnViderPlayer.setIsNeedNetChangeListen(true);
//        第一次进来先设置数据
        mnViderPlayer.setDataSource(Url2,"标题");
//        播放完成监听
        mnViderPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.e(TAG,"播放完成-----");
            }
        });
        mnViderPlayer.setOnScreenOrientationListener(new OnScreenOrientationListener() {
            @Override
            public void orientation_landscape() {
                Toast.makeText(MainActivity.this, "横屏", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void orientation_portrait() {
                Toast.makeText(MainActivity.this, "竖屏", Toast.LENGTH_SHORT).show();
            }
        });
//        网络监听
        mnViderPlayer.setOnNetChangeListener(new OnNetChangeListener() {
            @Override
            public void onWifi(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "当前为WIFI状态", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMobile(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "请注意，当前网络状态切换为3G/4G网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoAvailable(MediaPlayer mediaPlayer) {
                Toast.makeText(MainActivity.this, "当前网络不可用，检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        mnViderPlayer = findViewById(R.id.mMediaPlarer);

    }
    public void btn01(View view) {
        mnViderPlayer.playVideo(Url1, "标题1");
    }

    public void btn02(View view) {
        //position表示需要跳转到的位置
        mnViderPlayer.playVideo(Url2, "标题2", 30000);
    }

    public void btn03(View view) {
        mnViderPlayer.playVideo(url3, "标题3");
    }
    public void btn04(View view) {
        if (hasPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            //判断本地有没有这个文件
            File file = new File(url4);
            if (file.exists()) {
                mnViderPlayer.playVideo(url4, "标题4");
            } else {
                Toast.makeText(MainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
            }
        } else {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            Toast.makeText(this, "没有存储权限", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mnViderPlayer.pauseVideo();
    }

    @Override
    public void onBackPressed() {

        if (mnViderPlayer.isFullScreen()){
            mnViderPlayer.setOrientationPortrait();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mnViderPlayer!=null){
            mnViderPlayer.destroyVideo();
            mnViderPlayer=null;
        }
        super.onDestroy();
    }
    public boolean hasPermission(Context context, String permission) {
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "存储权限申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "存储权限申请失败", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
