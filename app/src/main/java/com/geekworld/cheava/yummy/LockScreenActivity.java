package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.RandomUtils;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class LockScreenActivity extends SwipeBackActivity{
    @Bind(R.id.fullscreen_content)TextView content;
    @Bind(R.id.background)ImageView background;
    @Bind(R.id.time)StrokeTextView time;

    private Handler handler = new Handler() {
        @DebugLog
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SHOW_WORD:
                    String response = (String) msg.obj;
                    if (response == null) response = getString(R.string.dummy_content);
                    if (response.length() > Constants.MAX_CHAR) return;
                    // 更新文字
                    String text = BaseApplication.prettifyText(response);
                    content.setText(text);
                    break;
                case Constants.SHOW_IMG:
                    String path = (String) msg.obj;
                    if (path == null) {
                        background.setImageResource(R.drawable.background);
                    }else{// 更新壁纸
                        background.setImageBitmap(BitmapFactory.decodeFile(path));
                    }
                    break;

                case Constants.SHOW_TIME:
                    String display_time = (String) msg.obj;
                    time.setText(display_time);
                    break;
            }
        }
    };


    private Timer timer = new Timer();
    public static final String TAG = "LockScreenActivity";
    private Context context;
    private final Activity activity = this;
    private ContentProvider contentProvider = new ContentProvider(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        BaseApplication.saveDisplaySize(this);
        Log.d(TAG,"onCreate");
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.activity_lock_screen);
        BaseApplication.saveDisplaySize(context);
        ButterKnife.bind(this);
        background.setImageResource(R.drawable.background);
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGesture.onTouchEvent(event);
            }
        });
        contentProvider.updateTime();
        long delay = 0;
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.getBooleanExtra("isSpecialDay",false)){
                content.setText(R.string.unusual);
                //content.setTextColor( getResources().getColor(R.color.blue_btn_bg_color));
                background.setImageResource(R.drawable.unusual);
                delay = 10*1000;
            }
        }else{
            contentProvider.updateWord();
            contentProvider.updateImage();
        }
        if (savedInstanceState != null) {
            //content.setText(savedInstanceState.getString("data_key"));
        }


        timer.schedule(imgTask, delay, ((RandomUtil.Int(Constants.IMG_DUTY)) + Constants.SWITCH_TIME) * 1000);
        timer.schedule(wordTask, delay, ((RandomUtil.Int(Constants.WORD_DUTY)) + Constants.SWITCH_TIME) * 1000);
    }




    private TimerTask wordTask = new TimerTask(){
        public void run() {
            contentProvider.updateWord();
        }
    };

    private TimerTask imgTask = new TimerTask(){
        public void run() {
            contentProvider.updateImage();
        }
    };



    private GestureDetector mGesture = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Logger.i("双击");
            String img = ImageUtil.screenShot(LockScreenActivity.this);
            ShareDialog shareDialog = new ShareDialog(LockScreenActivity.this);
            shareDialog.showDialog(img);
            return true;
        }
    });

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //contentProvider.updateWord();
        //contentProvider.updateImage();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel( );
            timer = null;
        }
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("last_word", (String) content.getText());
        //outState.putString("last_image_path", (String) content.getText());
    }
}
