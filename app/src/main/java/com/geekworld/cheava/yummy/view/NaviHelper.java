package com.geekworld.cheava.yummy.view;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.geekworld.cheava.yummy.R;
import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * The type Navi helper.
 */
/*
* @class NaviHelper
* @desc  浮动按键助手
* @author wangzh
*/
public class NaviHelper extends SpringFloatingActionMenu {
    private static Activity activity;
    private static NaviHelper instance = null;
    private static FloatingActionButton fab = null;
    private static SpringFloatingActionMenu menu = null;
    /* 私有构造方法，防止被实例化 */
    private NaviHelper(Activity activity) {
        super(activity);
        EventBus.getDefault().register(this);
    }

    /**
     * Gets instance.
     *
     * @param activity1 the activity 1
     * @return the instance
     */
/* 1:懒汉式，静态工程方法，创建实例 */
    public static NaviHelper getInstance(Activity activity1) {
        if (instance == null || activity != activity1)
        {
            instance = new NaviHelper(activity1);
            activity = activity1;
            fab = createFAB(activity);
        }
        return instance;
    }

    static private FloatingActionButton createFAB(Context context){
        //必须手动创建FAB, 并设置属性
        FloatingActionButton fab = new FloatingActionButton(context);
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setImageResource(R.mipmap.center);
        fab.setColorPressedResId(R.color.text_color);
        fab.setColorNormalResId(R.color.text_color);
        fab.setColorRippleResId(R.color.text_color);
        fab.setShadow(true);
        return fab;
    }

    /**
     * Share.
     *
     * @param path the path
     */
    public void share(String path){
        if(path == null){
            Toast.makeText(activity,"找不到需要分享的图片",Toast.LENGTH_SHORT).show();
            return;
        }
        //fab = createFAB(activity);
        if (menu != null) {
            menu.removeAllViews();
        }
        menu = new FabMenuFactory().createFabMenu(activity, fab, FabMenuFactory.FabStatus.SHARE);
        if(menu.isMenuOpen()){
            menu.hideMenu();
        }
        menu.showMenu();
    }

    /**
     * Standby.
     *
     * @param signal the signal
     */
    @Subscribe
    public void standby(String signal) {
        //fab = createFAB(activity);
        //if("SHARE_FINISH".equals(signal))
        {
            if (menu != null) {
                menu.removeAllViews();
            }
            menu = new FabMenuFactory().createFabMenu(activity, fab, FabMenuFactory.FabStatus.NORMAL);
            if (menu.isMenuOpen()) {
                menu.hideMenu();
            }
        }
    }

    /**
     * Destroy.
     */
    public void destroy() {
        activity = null;
        instance = null;
        fab = null;
        menu = null;
        EventBus.getDefault().unregister(this);
    }
}
