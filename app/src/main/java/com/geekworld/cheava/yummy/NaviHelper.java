package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.ShareBoardlistener;

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

    }

    /* 1:懒汉式，静态工程方法，创建实例 */
    public static NaviHelper getInstance(Activity activity1) {
        if (instance == null || activity != activity1)
        {
            instance = new NaviHelper(activity1);
            activity = activity1;

            //if(fab==null){
                fab = createFAB(activity);
            //}
            //if(menu==null){
                menu = ceateFabMenu(activity,fab);
            //}
        }
        return instance;
    }


    static View.OnClickListener imgclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(BaseApplication.context(),"爱你哦~ヾ(≧∇≦*)ゝ",Toast.LENGTH_SHORT).show();
            menu.hideMenu();
            UMImage umImage = new UMImage(activity, R.drawable.ic_launcher);
            new ShareAction(activity)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .setCallback(umShareListener)
                    .withText("Hello 微信")
                    .withMedia(umImage)
                    .share();

        }
    };

    static private FloatingActionButton createFAB(Context context){
        //必须手动创建FAB, 并设置属性
        FloatingActionButton fab = new FloatingActionButton(context);
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setImageResource(R.drawable.center);
        fab.setColorPressedResId(R.color.text_color);
        fab.setColorNormalResId(R.color.text_color);
        fab.setColorRippleResId(R.color.text_color);
        fab.setShadow(true);
        return fab;
    }

    static private SpringFloatingActionMenu ceateFabMenu(Context context,FloatingActionButton fab){
        final FloatingActionButton mfab = fab;
        final SpringFloatingActionMenu menu = new SpringFloatingActionMenu.Builder(context)
                .fab(fab)
                //添加菜单按钮参数依次是背景颜色,图标,标签,标签的颜色,点击事件
                .addMenuItem(R.color.sweet_dialog_bg_color, R.drawable.center, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.sweet_dialog_bg_color, R.drawable.wechat2, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.sweet_dialog_bg_color, R.drawable.wechatcircle2, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.sweet_dialog_bg_color, R.drawable.qq2, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.sweet_dialog_bg_color, R.drawable.qqzone2, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.sweet_dialog_bg_color, R.drawable.weibo2, null, R.color.text_color,imgclick)
                //设置动画类型
                .animationType(SpringFloatingActionMenu.ANIMATION_TYPE_TUMBLR)
                //设置reveal效果的颜色
                .revealColor(R.color.colorPrimary)
                //设置FAB的位置,只支持底部居中和右下角的位置
                .gravity(Gravity.RIGHT | Gravity.BOTTOM)
                .onMenuActionListner(new OnMenuActionListener() {
                    @Override
                    public void onMenuOpen() {
                        //设置FAB的icon当菜单打开的时候
                        mfab.setImageResource(R.drawable.close2);
                    }

                    @Override
                    public void onMenuClose() {
                        //设置回FAB的图标当菜单关闭的时候
                        mfab.setImageResource(R.drawable.standby);
                    }
                })
                .build();
        return menu;
    }


    public void share(String path){
        this.path = path;

        if(menu.isMenuOpen()){
            menu.hideMenu();
        }
        menu.showMenu();

    }

    static private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(activity, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(activity, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(activity, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


}
