package com.geekworld.cheava.yummy.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.bean.Constants;
import com.geekworld.cheava.yummy.bean.Event;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.geekworld.cheava.yummy.utils.DataUtils;
import com.geekworld.cheava.yummy.utils.DateTimeUtil;
import com.geekworld.cheava.yummy.utils.RandomUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import hugo.weaving.DebugLog;

/**
 * The type Content provider.
 */
/*
* @class ContentProvider
* @desc  接收View层的信号，获取展示数据，处理后提交给View层
* @author wangzh
*/
public class ContentProvider {
    public static final String TAG = "ContentProvider";
    /**
     * The Context.
     */
    Context context = BaseApplication.context();

    private Timer timer = new Timer();
    /**
     * The Show word id.
     */
    static int show_word_id = 0;
    /**
     * The Show img id.
     */
    static int show_img_id = 0;

    //记录已出现的id
    static private HashSet<Integer> wordHashSet=new HashSet<Integer>();
    static private HashSet<Integer> imgHashSet = new HashSet<Integer>();


    /**
     * Instantiates a new Content provider.
     * 变量初始化
     */

    public ContentProvider() {
        //注册时间监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(receiver, intentFilter);
    }


    private TimerTask wordTask = new TimerTask(){
        public void run() {
            updateWord();
        }
    };

    private TimerTask imgTask = new TimerTask(){
        public void run() {
            updateImage();
        }
    };

    public void startRefresh(long delay){
        Log.i(TAG,"startRefresh");
        try {
            timer.schedule(imgTask, delay, ((RandomUtil.Int(Constants.IMG_DUTY)) + Constants.SWITCH_TIME) * 1000);
            timer.schedule(wordTask, delay, ((RandomUtil.Int(Constants.WORD_DUTY)) + Constants.SWITCH_TIME) * 1000);
        }catch (Exception e){
            Log.w(TAG,"Fail to schedule");
        }
    }

    public void stopRefresh(){
        Log.i(TAG,"stopRefresh");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Update image.
     */
    public void updateImage() {
        int sum = DataUtils.getImgSum();
        if(sum<=0){
            //暂无内容，一般是安装后第一次运行程序
            CacheUtil.setNeedRefreshImg(true);
        }else if(sum==1){
            show_img_id = 1;
            EventBus.getDefault().post(new Event().new ImageEvent(DataUtils.loadImage(show_img_id)));
        }else{
            show_img_id = getImgId(sum);
            EventBus.getDefault().post(new Event().new ImageEvent(DataUtils.loadImage(show_img_id)));
        }
    }

    /**
     * Update word.
     */
    public void updateWord() {
        int sum = DataUtils.getWordSum();
        if(sum<=0){
            //暂无内容，一般是安装后第一次运行程序
            CacheUtil.setNeedRefreshWord(true);
        }else if(sum==1){
            show_word_id = 1;
            String result = DataUtils.loadWord(show_word_id);
            if("ID不存在".equals(result)){
                result = context.getString(R.string.dummy_content);
            }
            EventBus.getDefault().post(new Event().new WordEvent(result));
        }else{
            show_word_id = getWordId(sum);
            String result = DataUtils.loadWord(show_word_id);
            if(result ==null || "ID不存在".equals(result)|| result.length() > Constants.MAX_CHAR){
                show_word_id = show_word_id+1;
                result =  DataUtils.loadWord(show_word_id);
            }
            EventBus.getDefault().post(new Event().new WordEvent(result));
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post(
                    new Event().new TimeEvent(
                            DateTimeUtil.getCurrentTimeString()
                    )
                    );
        }
    };



    /**
     * Gets word id.
     *获取不重复随机的图片id
     * @param sum the sum
     * @return the word id
     */
    static public int getWordId(int sum) {
        if(sum<=0){
            Logger.d("sum is illegal");
            return 0;
        }
        int retry = 0;
        int random = 1;
        for(;retry<3;retry++)
        {
            random = new RandomUtil().Int(sum);
            if(!wordHashSet.contains(random)){
                wordHashSet.add(random);
                break;
            }
        }
        if(retry>=3)wordHashSet.clear();
        return random;
    }

    /**
     * Gets img id.
     * 获取不重复随机的图片id
     * @param sum the sum
     * @return the img id
     */
    static public int getImgId(int sum) {
        if(sum<=0){
            Logger.d("sum is illegal");
            return 0;
        }
        int retry = 0;
        int random = 1;
        for(;retry<3;retry++)
        {
            random = RandomUtil.Int(sum);
            if(!imgHashSet.contains(random)){
                imgHashSet.add(random);
                break;
            }
        }
        if(retry>=3)imgHashSet.clear();
        return random;
    }

    /**
     * Destroy.
     */
    public void destroy(){
        Logger.d("unregisterReceiver");
        context.unregisterReceiver(receiver);
        stopRefresh();
    }
}
