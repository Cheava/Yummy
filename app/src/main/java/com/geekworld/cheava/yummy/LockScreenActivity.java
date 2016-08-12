package com.geekworld.cheava.yummy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class LockScreenActivity extends SwipeBackActivity{
    @Bind(R.id.fullscreen_content)TextView content;
    @Bind(R.id.background)ImageView background;

    private Handler handler = new Handler() {
        @DebugLog
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SHOW_WORD:
                    String response = (String) msg.obj;
                    if (response == null) return;
                    if (response.length() > Constants.MAX_CHAR) return;
                    // 更新文字
                    String text = prettifyText(response);
                    content.setText(text);
                    break;
                case Constants.SHOW_IMG:
                    String path = (String) msg.obj;
                    if (path == null) return;
                    // 更新壁纸
                    background.setImageBitmap(BitmapFactory.decodeFile(path));
                    break;
            }
        }
    };

    Timer timer = new Timer();
    public static final String TAG = "LockScreenActivity";
    Context context;
    ContentProvider contentProvider = new ContentProvider(handler);

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

        background.setImageResource(R.drawable.new_bg);

        if (savedInstanceState != null) {
            //content.setText(savedInstanceState.getString("data_key"));
        }

        timer.schedule(imgTask, 0, Constants.IMG_DUTY * 1000);
        timer.schedule(wordTask, 0, Constants.WORD_DUTY * 1000);
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

    private String prettifyText(String text){
        String result;
        result = text.replace("，","，\r\n");
        result = result.replace("。","。\r\n");
        result = result.replace("！","！\r\n");
        result = result.replace("？","？\r\n");
        result = result.replace(" "," \r\n");
        return result;
    }

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
        if (timer != null) {
            timer.cancel( );
            timer = null;
        }
        super.onDestroy();
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
        outState.putString("last_image_path", (String) content.getText());
    }
}
