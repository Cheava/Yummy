package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

/**
 * Created by Cheava on 2016/8/16 0016.
 */
public class NaviHelper extends SpringFloatingActionMenu  {
    private static Activity activity;
    private static NaviHelper instance = null;
    private static FloatingActionButton fab = null;
    private static SpringFloatingActionMenu menu = null;
    /* 私有构造方法，防止被实例化 */
    private NaviHelper(Activity activity) {
        super(activity);

    }

    /* 1:懒汉式，静态工程方法，创建实例 */
    public static NaviHelper getInstance(Activity activity) {
        if (instance == null) {
            instance = new NaviHelper(activity);
            instance.activity = activity;
        }
        return instance;
    }

    View.OnClickListener imgclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(BaseApplication.context(),"ヾ(≧∇≦*)ゝ",Toast.LENGTH_SHORT).show();

        }
    };

    private FloatingActionButton createFAB(Context context){
        //必须手动创建FAB, 并设置属性
        FloatingActionButton fab = new FloatingActionButton(context);
        fab.setImageAlpha(100);
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setImageResource(R.mipmap.ic_launcher);
        fab.setColorPressedResId(R.color.colorPrimary);
        fab.setColorNormalResId(R.color.material_blue_500);
        fab.setColorRippleResId(R.color.text_color);
        fab.setShadow(true);
        return fab;
    }

    private SpringFloatingActionMenu ceateFabMenu(Context context,FloatingActionButton fab){
        final FloatingActionButton mfab = fab;
        SpringFloatingActionMenu menu = new SpringFloatingActionMenu.Builder(context)
                .fab(fab)
                //添加菜单按钮参数依次是背景颜色,图标,标签,标签的颜色,点击事件
                .addMenuItem(R.color.blue_btn_bg_color, R.mipmap.ic_launcher, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.blue_btn_bg_color, R.drawable.wechat, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.error_stroke_color, R.drawable.wechatcircle, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.gray_btn_bg_pressed_color, R.drawable.qq, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.button_text_color, R.drawable.qqzone, null, R.color.text_color,imgclick)
                .addMenuItem(R.color.blue_btn_bg_color, R.drawable.weibo, null, R.color.text_color,imgclick)
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
                        mfab.setImageResource(R.drawable.close);
                    }

                    @Override
                    public void onMenuClose() {
                        //设置回FAB的图标当菜单关闭的时候
                        mfab.setImageResource(R.mipmap.ic_launcher);
                    }
                })
                .build();
        return menu;
    }


    public void share(String path){
        if(fab==null){
            fab = createFAB(activity);
        }
        if(menu==null){
            menu = ceateFabMenu(activity,fab);
        }
        if(!menu.isMenuOpen()){
            menu.showMenu();
        }
    }
}
