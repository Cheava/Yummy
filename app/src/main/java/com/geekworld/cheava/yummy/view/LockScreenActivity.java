package com.geekworld.cheava.yummy.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekworld.cheava.yummy.bean.Constants;
import com.geekworld.cheava.yummy.bean.Event;
import com.geekworld.cheava.yummy.bean.Screenshot;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.geekworld.cheava.yummy.utils.DataUtils;
import com.geekworld.cheava.yummy.utils.DateTimeUtil;
import com.geekworld.cheava.yummy.utils.ImageUtil;
import com.geekworld.cheava.yummy.presenter.ContentProvider;
import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.utils.RandomUtil;
import com.geekworld.cheava.yummy.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.UMShareAPI;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
/*
* @class LockScreenActivity
* @desc  锁屏界面
* @author wangzh
*/
public class LockScreenActivity extends SwipeBackActivity{
    @Bind(R.id.fullscreen_content)TextView content;
    @Bind(R.id.background)ImageView background;
    @Bind(R.id.time)StrokeTextView time;


    public static final String TAG = "LockScreenActivity";
    protected long specialDelay = 0;
    private WeakReference<Bitmap> bg ;
    private ShareDialog shareDialog;
    private GestureDetector mGesture;
    private ContentProvider contentProvider;

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
        mSwipeBackLayout.setEdgeSize(CacheUtil.getScreenWidth()/2);

        mGesture = new GestureDetector(this,mOnGesture);
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGesture.onTouchEvent(event);
            }
        });

        time.setText(DateTimeUtil.getCurrentTimeString());
        bg = new WeakReference<Bitmap>(BitmapFactory.decodeResource(getResources(), R.mipmap.background));
        background.setImageBitmap(bg.get());

        NaviHelper.getInstance(this).standby(null);
        EventBus.getDefault().register(this);
        contentProvider = new ContentProvider();
        contentProvider.startRefresh(specialDelay);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTime(Event.TimeEvent timeEvent){
        time.setText(timeEvent.time);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateImage(Event.ImageEvent imageEvent) {
        String path = imageEvent.path;
        if (path == null) {
            bg = new WeakReference<Bitmap>(BitmapFactory.decodeResource(getResources(), R.mipmap.background));
        } else {
            bg = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path));
        }
        background.setImageBitmap(bg.get());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateWord(Event.WordEvent wordEvent) {
        String word = wordEvent.word;
        if (word == null) word = getString(R.string.dummy_content);
        String text = BaseApplication.prettifyText(word);
        content.setText(text);
    }

    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Logger.i("双击");
            //stopRefresh();
            Screenshot screenshot = ImageUtil.screenShot(LockScreenActivity.this);
            CacheUtil.setScreenshot(screenshot);
            String img = screenshot.getImgPath();
            if(img == null){
                ToastUtil.showShort(getApplicationContext(),"截图失败");
                return true;
            }else{
                ToastUtil.showShort(getApplicationContext(),"截图成功");
            }
            shareDialog = new ShareDialog(LockScreenActivity.this);
            shareDialog.showDialog(img);
            return true;
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i("DEBUG","绘制完成");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }


    @Override
    protected void onStop() {
        super.onStop();
        //stopRefresh();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post("Destroy");
        shareDialog = null;
        contentProvider.destroy();
        NaviHelper.getInstance(this).destroy();
        recycleImageView(background);
        System.gc();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
