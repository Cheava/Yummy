package com.geekworld.cheava.yummy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.Gravity;

import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wangzh on 2016/9/7.
 */
public class FabMenuFactory {
    public enum FabStatus {NORMAL, SHARE}

    public enum FastTools {CAMERA, MESSAGE, LIGHT, PHONE}

    private static FloatingActionButton mfab;
    private static SpringFloatingActionMenu menu;
    private static FabStatus mstatus;


    public FabMenuFactory() {
    }

    static public SpringFloatingActionMenu createFabMenu(Context context, FloatingActionButton fab, FabStatus status) {
        mfab = fab;
        mstatus = status;
        switch (status) {
            case NORMAL:
                menu = new SpringFloatingActionMenu.Builder(context)
                        .fab(fab)
                        //添加菜单按钮参数依次是背景颜色,图标,标签,标签的颜色,点击事件
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.center, null, R.color.text_color, null)
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_camera_alt_light_blue_400_48dp, null, R.color.text_color,
                                new ShareClickListenerFactory(context, FastTools.CAMERA))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_lightbulb_outline_light_blue_400_48dp, null, R.color.text_color,
                                new ShareClickListenerFactory(context, FastTools.LIGHT))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_message_light_blue_400_48dp, null, R.color.text_color,
                                new ShareClickListenerFactory(context, FastTools.MESSAGE))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.ic_phone_light_blue_400_48dp, null, R.color.text_color,
                                new ShareClickListenerFactory(context, FastTools.PHONE))

                        //设置动画类型
                        .animationType(SpringFloatingActionMenu.ANIMATION_TYPE_TUMBLR)
                        //设置reveal效果的颜色
                        .revealColor(R.color.colorPrimary)
                        //设置FAB的位置,只支持底部居中和右下角的位置
                        .gravity(Gravity.RIGHT | Gravity.BOTTOM)
                        .onMenuActionListner(menuActionListener)
                        .build();
                break;
            case SHARE:
                String path = BaseApplication.get(context.getResources().getString(R.string.screen_shot_path), null);
                menu = new SpringFloatingActionMenu.Builder(context)
                        .fab(fab)
                        //添加菜单按钮参数依次是背景颜色,图标,标签,标签的颜色,点击事件
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.center, null, R.color.text_color, null)
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.wechat2, null, R.color.text_color,
                                new ShareClickListenerFactory(context, SHARE_MEDIA.WEIXIN, path))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.wechatcircle2, null, R.color.text_color,
                                new ShareClickListenerFactory(context, SHARE_MEDIA.WEIXIN_CIRCLE, path))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.qq2, null, R.color.text_color,
                                new ShareClickListenerFactory(context, SHARE_MEDIA.QQ, path))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.qqzone2, null, R.color.text_color,
                                new ShareClickListenerFactory(context, SHARE_MEDIA.QZONE, path))
                        .addMenuItem(R.color.sweet_dialog_bg_color, R.mipmap.weibo2, null, R.color.text_color,
                                new ShareClickListenerFactory(context, SHARE_MEDIA.SINA, path))
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

    static private OnMenuActionListener menuActionListener = new OnMenuActionListener() {
        @Override
        public void onMenuOpen() {
            //设置FAB的icon当菜单打开的时候
            mfab.setImageResource(R.mipmap.close2);
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

