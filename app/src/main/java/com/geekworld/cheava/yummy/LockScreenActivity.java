package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.umeng.socialize.UMShareAPI;

import org.apache.commons.lang3.RandomUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class LockScreenActivity extends SwipeBackActivity{
    @Bind(R.id.fullscreen_content)TextView content;
    @Bind(R.id.background)ImageView background;
    @Bind(R.id.time)StrokeTextView time;

    private Handler handler = new Handler() {
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
                        bg = new WeakReference<Bitmap>(BitmapFactory.decodeResource(getResources(), R.mipmap.background));
                    }else{// 更新壁纸
                        bg = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path));
                    }
                    background.setImageBitmap(bg.get());
                    break;

                case Constants.SHOW_TIME:
                    String display_time = (String) msg.obj;
                    time.setText(display_time);
                    break;
            }
        }
    };

    private WeakReference<Bitmap> bg ;
    private Timer timer = new Timer();
    public static final String TAG = "LockScreenActivity";
    private long specialDelay = 0;
    private GestureDetector mGesture;
    private ContentProvider contentProvider = new ContentProvider(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);



        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(BaseApplication.getScreenWidth()/2);


        mGesture = new GestureDetector(this,mOnGesture);
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGesture.onTouchEvent(event);
            }
        });

        contentProvider.updateTime();
        bg = new WeakReference<Bitmap>(BitmapFactory.decodeResource(getResources(), R.mipmap.background));
        background.setImageBitmap(bg.get());
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.getBooleanExtra("isSpecialDay",false)){
                content.setText(R.string.unusual);
                background.setImageResource(R.mipmap.unusual);
                specialDelay = 10*1000;
            }
        }
        if (savedInstanceState != null) {
            //content.setText(savedInstanceState.getString("data_key"));
        }
        startRefresh(specialDelay);
        NaviHelper.getInstance(this).standby(null);
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

    private void startRefresh(long delay){
        Log.i(TAG,"startRefresh");
        try {
            timer.schedule(imgTask, delay, ((RandomUtil.Int(Constants.IMG_DUTY)) + Constants.SWITCH_TIME) * 1000);
            timer.schedule(wordTask, delay, ((RandomUtil.Int(Constants.WORD_DUTY)) + Constants.SWITCH_TIME) * 1000);
        }catch (Exception e){
            Log.w(TAG,"Fail to schedule");
        }
    }

    private void stopRefresh(){
        Log.i(TAG,"stopRefresh");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Logger.i("双击");
            //stopRefresh();
            String img = ImageUtil.screenShot(LockScreenActivity.this);
            BaseApplication.set(getResources().getString(R.string.screen_shot_path), img);
            ShareDialog shareDialog = new ShareDialog(LockScreenActivity.this);
            shareDialog.showDialog(img);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //finish();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    public static void recycleImageView(View view){
        if(view==null) return;
        if(view instanceof ImageView){
            Drawable drawable=((ImageView) view).getDrawable();
            if(drawable instanceof BitmapDrawable){
                Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                if (bmp != null && !bmp.isRecycled()){
                    ((ImageView) view).setImageBitmap(null);
                    bmp.recycle();
                    bmp=null;
                }
            }
        }
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
        startRefresh(specialDelay);
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
        stopRefresh();

        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentProvider.destroy();
        NaviHelper.getInstance(this).destroy();
        stopRefresh();
        recycleImageView(background);
        System.gc();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
