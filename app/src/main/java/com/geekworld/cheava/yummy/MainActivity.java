package com.geekworld.cheava.yummy;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener  {
    @Bind(R.id.exit)TextView exit;
    Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        exit.setOnClickListener(this);
        service = new Intent(this, LockService.class);
        startService(service);
        AlarmManagerUtil.sendAlarmBroadcast(BaseApplication.context(), 1, AlarmManager.RTC_WAKEUP, DateTimeUtil.getDifMillis("2016-08-14 15:17:20", DateTimeUtil.getCurrentDateTimeString()), SpacialDay.class);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.exit:
                stopService(service);
                finish();
                break;
            default:
                break;
        }
    }
}
