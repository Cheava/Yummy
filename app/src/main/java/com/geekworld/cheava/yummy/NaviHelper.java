package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.test.suitebuilder.annotation.Suppress;

import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Cheava on 2016/8/16 0016.
 */
public class NaviHelper extends SpringFloatingActionMenu {
    private static Activity activity;
    private static String path = null;
    private static NaviHelper instance = null;
    private static FloatingActionButton fab = null;
    private static SpringFloatingActionMenu menu = null;
    /* 私有构造方法，防止被实例化 */
    private NaviHelper(Activity activity) {
        super(activity);
        EventBus.getDefault().register(this);
    }

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

    public void share(String path){
        this.path = path;
        //fab = createFAB(activity);
        if (menu != null) {
            menu.removeAllViews();
        }
        menu = FabMenuFactory.createFabMenu(activity, fab, FabMenuFactory.FabStatus.SHARE);
        if(menu.isMenuOpen()){
            menu.hideMenu();
        }
        menu.showMenu();
    }

    @Subscribe
    public void standby(String signal) {
        //fab = createFAB(activity);
        //if("SHARE_FINISH".equals(signal))
        {
            if (menu != null) {
                menu.removeAllViews();
            }
            menu = FabMenuFactory.createFabMenu(activity, fab, FabMenuFactory.FabStatus.NORMAL);
            if (menu.isMenuOpen()) {
                menu.hideMenu();
            }
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}
