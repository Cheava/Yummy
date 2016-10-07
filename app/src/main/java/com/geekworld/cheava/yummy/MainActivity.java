
package com.geekworld.cheava.yummy;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geekworld.cheava.yummy.presenter.DownloadService;
import com.geekworld.cheava.yummy.presenter.LockService;
import com.geekworld.cheava.yummy.utils.ACache;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

/*
* @class MainActivity
* @desc  主界面
* @author wangzh
*/
public class MainActivity extends AppCompatActivity  implements View.OnClickListener  {
    @Bind(R.id.exit)TextView exit;
    @Bind(R.id.version)TextView version;
    Intent lockscreen_service;
    Intent download_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        //BaseApplication.disableSysLock();
        version.setText(BaseApplication.getVersion());
        exit.setOnClickListener(this);
        lockscreen_service = new Intent(this, LockService.class);
        lockscreen_service.setPackage(BaseApplication.context().getPackageName());
        startService(lockscreen_service);
        download_service = new Intent(this, DownloadService.class);
        download_service.setPackage(BaseApplication.context().getPackageName());
        startService(download_service);

        //BmobUpdateAgent.initAppVersion();
        //BmobUpdateAgent.update(this);
        BmobUpdateAgent.silentUpdate(this);

        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                if (updateStatus == UpdateStatus.Yes) {
                    UpdateResponse ur = updateInfo;
                }else if(updateStatus== UpdateStatus.IGNORED){//新增忽略版本更新
                    Toast.makeText(MainActivity.this, "该版本已经被忽略更新", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(BaseApplication.isNoSwitch()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.exit:
                stopService(lockscreen_service);
                stopService(download_service);
                finish();
                break;
            default:
                break;
        }
    }
}
