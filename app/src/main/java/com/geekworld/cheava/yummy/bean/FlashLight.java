package com.geekworld.cheava.yummy.bean;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.geekworld.cheava.yummy.BaseApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzh on 2016/9/9.
 */
public class FlashLight {
    private final String TAG = "FlashLight";
    /** Camera相机硬件操作类 */
    private Camera camera = null;

    /** Camera2相机硬件操作类 */
    private CameraManager manager = null;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession = null;
    private CaptureRequest request = null;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private String cameraId = null;
    private boolean isSupportFlashCamera2 = false;
    private final CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {
        public void onConfigured(CameraCaptureSession arg0) {
            captureSession = arg0;
            CaptureRequest.Builder builder;
            try {
                builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                builder.addTarget(surface);
                request = builder.build();
                captureSession.capture(request, null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        };

        public void onConfigureFailed(CameraCaptureSession arg0) {
        };
    };

    private boolean isOpen = false;


    public void openFlashLight() {
        if (isOpen) {
            if (isLOLLIPOP()) {
                if (cameraDevice != null) {
                    cameraDevice.close();
                }
            } else {
                turnLightOffCamera(camera);
            }
            isOpen = false;
        } else {
            if (isLOLLIPOP()) {
                try {
                    openCamera2Flash();
                } catch (CameraAccessException e) {
                    Log.e(TAG, e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                turnLightOnCamera(camera);
            }
            isOpen = true;
        }

        // 初始化Camera硬件
        this.manager = (CameraManager) BaseApplication.context().getSystemService(Context.CAMERA_SERVICE);
        if (isLOLLIPOP()) {
            initCamera2();
        } else {
            camera = Camera.open();
        }
    }

    public void releaseFlashLight() {
        camera.release();
        camera = null;
    }

    /**
     * 初始化Camera2
     */
    private void initCamera2() {
        try {
            for (String cameraId : this.manager.getCameraIdList()) {
                CameraCharacteristics characteristics = this.manager.getCameraCharacteristics(cameraId);
                // 过滤掉前置摄像头
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                this.cameraId = cameraId;
                // 判断设备是否支持闪光灯
                this.isSupportFlashCamera2 = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 调用Camera2开启闪光灯
     *
     * @throws CameraAccessException
     */
    private void openCamera2Flash() throws CameraAccessException {
        manager.openCamera(cameraId, new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {
                cameraDevice = camera;
                createCaptureSession();
            }

            @Override
            public void onError(CameraDevice camera, int error) {
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
            }
        }, null);
    }

    /**
     * createCaptureSession
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void createCaptureSession() {
        this.surfaceTexture = new SurfaceTexture(0, false);
        this.surface = new Surface(this.surfaceTexture);
        ArrayList localArrayList = new ArrayList(1);
        localArrayList.add(this.surface);
        try {
            this.cameraDevice.createCaptureSession(localArrayList, this.stateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 通过设置Camera打开闪光灯
     *
     * @param mCamera
     */
    public void turnLightOnCamera(Camera mCamera) {
        Camera.Parameters parameters = camera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // 开启闪光灯
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        }
    }

    /**
     * 通过设置Camera关闭闪光灯
     *
     * @param mCamera
     */
    public void turnLightOffCamera(Camera mCamera) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // 关闭闪光灯
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }
        }
    }

    /**
     * 判断Android系统版本是否 >= LOLLIPOP(API21)
     *
     * @return boolean
     */
    private boolean isLOLLIPOP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }
    }
}
