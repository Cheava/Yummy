package com.geekworld.cheava.yummy.view;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;

import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.share.ClickListenerFactory;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

import org.greenrobot.eventbus.EventBus;

/**
 * The type Fab menu factory.
 */
/*
* @class FabMenuFactory
* @desc  浮动按键工厂类
* @author wangzh
*/
public class FabMenuFactory {
    /**
     * The enum Fab status.
     */
    public enum FabStatus {
        /**
         * Normal fab status.
         */
        NORMAL, /**
         * Share fab status.
         */
        SHARE}

    /**
     * The enum Fast tools.
     */
    public enum FastTools {
        /**
         * Camera fast tools.
         */
        CAMERA, /**
         * Message fast tools.
         */
        MESSAGE, /**
         * Light fast tools.
         */
        LIGHT, /**
         * Phone fast tools.
         */
        PHONE}

    public enum SharePlatform {
        WEIXIN,WEIXIN_CIRCLE,QQ,QZONE,SINA
    }

    private FloatingActionButton mfab;
    private SpringFloatingActionMenu menu;
    private FabStatus mstatus;


    /**
     * Instantiates a new Fab menu factory.
     */
    public FabMenuFactory() {
    }

    /**
     * Create fab menu spring floating action menu.
     *
     * @param context the context
     * @param fab     the fab
     * @param status  the status
     * @return the spring floating action menu
     */
    public SpringFloatingActionMenu createFabMenu(Context context, FloatingActionButton fab, FabStatus status) {
        mfab = fab;
        mstatus = status;
        switch (status) {
            //正常状态 显示快捷工具
            case NORMAL:
                menu = new SpringFloatingActionMenu.Builder(context)
                        .fab(fab)
                        //添加菜单按钮参数依次是背景颜色,图标,标签,标签的颜色,点击事件
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.center, null, R.color.text_color, null)
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_camera_alt_light_blue_400_48dp, null, R.color.text_color,
                                new ClickListenerFactory(context, FastTools.CAMERA))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_lightbulb_outline_light_blue_400_48dp, null, R.color.text_color,
                                new ClickListenerFactory(context, FastTools.LIGHT))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_message_light_blue_400_48dp, null, R.color.text_color,
                                new ClickListenerFactory(context, FastTools.MESSAGE))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_phone_light_blue_400_48dp, null, R.color.text_color,
                                new ClickListenerFactory(context, FastTools.PHONE))

                        //设置动画类型
                        .animationType(SpringFloatingActionMenu.ANIMATION_TYPE_TUMBLR)
                        //设置reveal效果的颜色
                        .revealColor(R.color.colorPrimary)
                        //设置FAB的位置,只支持底部居中和右下角的位置
                        .gravity(Gravity.RIGHT | Gravity.BOTTOM)
                        .onMenuActionListner(menuActionListener)
                        .build();
                break;
            //分享状态，显示主流分享平台
            case SHARE:
                String path = CacheUtil.getScreenshotPath();
                Uri uri = Uri.parse(CacheUtil.getScreenshotUriString());
                menu = new SpringFloatingActionMenu.Builder(context)
                        .fab(fab)
                        //添加菜单按钮参数依次是背景颜色,图标,标签,标签的颜色,点击事件
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.center, null, R.color.text_color, null)
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.wechat, null, R.color.text_color,
                                new ClickListenerFactory(context, SharePlatform.WEIXIN, uri))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.wechatcircle, null, R.color.text_color,
                                new ClickListenerFactory(context, SharePlatform.WEIXIN_CIRCLE, uri))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.qq, null, R.color.text_color,
                                new ClickListenerFactory(context, SharePlatform.QQ, uri))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.qqzone, null, R.color.text_color,
                                new ClickListenerFactory(context, SharePlatform.QZONE, uri))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.weibo, null, R.color.text_color,
                                new ClickListenerFactory(context, SharePlatform.SINA, uri))
                        //设置动画类型
                        .animationType(SpringFloatingActionMenu.ANIMATION_TYPE_TUMBLR)
                        //设置reveal效果的颜色
                        .revealColor(R.color.colorPrimary)
                        //设置FAB的位置,只支持底部居中和右下角的位置
                        .gravity(Gravity.RIGHT | Gravity.BOTTOM)
                        .onMenuActionListner(menuActionListener)
                        .build();
                break;
            default:
                Logger.e("unexpected Fab status");
                break;
        }

        return menu;
    }


    private OnMenuActionListener menuActionListener = new OnMenuActionListener() {
        @Override
        public void onMenuOpen() {
            //设置FAB的icon当菜单打开的时候
            mfab.setImageResource(R.mipmap.close);
        }

        @Override
        public void onMenuClose() {
            //设置回FAB的图标当菜单关闭的时候
            mfab.setImageResource(R.mipmap.standby);
            if (mstatus == FabStatus.SHARE) {
                EventBus.getDefault().post("SHARE_FINISH");
            }
        }
    };
}

