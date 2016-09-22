package com.geekworld.cheava.yummy.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.widget.Toast;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.bean.FlashLight;
import com.geekworld.cheava.yummy.view.FabMenuFactory;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Cheava on 2016/9/15 0015.
 */
public class FastTools {
    private Context context;
    private FabMenuFactory.FastTools tools = null;
    private FlashLight flashLight = new FlashLight();

    static private Camera camera = null;
    static private boolean isOpen = false;

    public FastTools( Context context,FabMenuFactory.FastTools tools) {
        this.tools = tools;
        this.context = context;
    }

    //分发快捷工具的入口
    public void toolsHandler() {
        switch (tools) {
            case CAMERA:
                try {
                    closeFlashLight();
                    releaseLight();
                }catch (Exception e){
                    e.printStackTrace();
                    Logger.e("Can't release flashlight");
                }
                startCommonTools(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
            case PHONE:
                startCommonTools(Intent.ACTION_DIAL);
                break;
            case LIGHT:
                startLightTools();
                break;
            case MESSAGE:
                startMsgTools();
                break;
            default:
                Logger.e("unexpected tools");
                break;
        }
    }

    //不带参数的快捷启动
    private void startCommonTools(String tools) {
        if(tools == null) return;
        Intent intent = new Intent();
        intent.setAction(tools);
        context.startActivity(intent);
    }


    private void startMsgTools() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    private void startLightTools() {
        if (CacheUtil.isCameraAttain()) {
            if(!isOpen){
                openFlashLight();
            }else{
                closeFlashLight();
            }
        } else {
            Toast.makeText(BaseApplication.context(), "没有发现闪光灯", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFlashLight() {
        if (camera == null) {
            try {
                camera = Camera.open();
            }catch (Exception e){
                e.printStackTrace();
                Logger.e("Can't open camera");
                ToastUtil.showShort(context,"无法打开摄像头");
            }
        }
        Camera.Parameters param = camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        //param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(param);
        isOpen = true;
    }

    private void closeFlashLight(){
        if (camera == null) {
            try {
                camera = Camera.open();
            }catch (Exception e){
                e.printStackTrace();
                Logger.e("Can't open camera");
                ToastUtil.showShort(context,"无法打开摄像头");
            }
        }
        Camera.Parameters param = camera.getParameters();
        //param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(param);
        isOpen = false;
    }

    private void releaseLight() {
        camera.release();
        camera = null;
    }
}
