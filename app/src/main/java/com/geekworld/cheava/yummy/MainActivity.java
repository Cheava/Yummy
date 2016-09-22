
package com.geekworld.cheava.yummy;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.geekworld.cheava.yummy.presenter.DownloadService;
import com.geekworld.cheava.yummy.presenter.LockService;
import com.geekworld.cheava.yummy.utils.ACache;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
* @class MainActivity
* @desc  主界面
* @author wangzh
*/
public class MainActivity extends AppCompatActivity  implements View.OnClickListener  {
    @Bind(R.id.exit)TextView exit;
    Intent lockscreen_service;
    Intent download_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        //BaseApplication.disableSysLock();
        exit.setOnClickListener(this);
        lockscreen_service = new Intent(this, LockService.class);
        lockscreen_service.setPackage(BaseApplication.context().getPackageName());
        startService(lockscreen_service);
        download_service = new Intent(this, DownloadService.class);
        download_service.setPackage(BaseApplication.context().getPackageName());
        startService(download_service);
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
