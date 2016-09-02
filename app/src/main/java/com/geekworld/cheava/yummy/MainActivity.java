package com.geekworld.cheava.yummy;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener  {
    @Bind(R.id.exit)TextView exit;
    @Bind(R.id.btn_time)Button btn_time;
    @Bind(R.id.btn_day)Button btn_day;
    @Bind(R.id.test)
    Button btn_test;
    @Bind(R.id.text_random)
    TextView text_random;
    Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        exit.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_time.setOnClickListener(this);
        btn_day.setOnClickListener(this);
        btn_day.setVisibility(View.INVISIBLE);
        btn_time.setVisibility(View.INVISIBLE);
        service = new Intent(this, LockService.class);
        startService(service);

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.exit:
                stopService(service);
                finish();
                break;
            case R.id.btn_time:
                Calendar currentTime = Calendar.getInstance();
                new TimePickerDialog(MainActivity.this, 0,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                //获得系统提供的AlarmManager服务的对象
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                //Intent设置要启动的组件:
                                Intent intent = new Intent(MainActivity.this, LockScreenActivity.class);
                                intent.putExtra("isSpecialDay",true);
                                //PendingIntent对象设置动作,启动的是Activity还是Service,又或者是广播!
                                PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                                //调用AlarmManager的set( )方法设置单次闹钟的闹钟类型,启动时间以及PendingIntent对象!

                                //设置当前时间
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                // 根据用户选择的时间来设置Calendar对象
                                c.set(Calendar.HOUR, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                // ②设置AlarmManager在Calendar对应的时间启动Activity
                                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),0, pi);
                                Logger.i(c.getTimeInMillis()+"");   //这里的时间是一个unix时间戳
                                // 提示闹钟设置完毕:
                                Toast.makeText(MainActivity.this, "闹钟设置完毕~"+ c.getTimeInMillis(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime
                        .get(Calendar.MINUTE), false).show();
                break;
            case R.id.btn_day:
                Calendar currentDay = Calendar.getInstance();
                new DatePickerDialog(MainActivity.this, 0,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,int year,int month,
                                                  int day) {
                                //设置当前时间
                                Calendar c = Calendar.getInstance();
                                c.setTime(c.getTime());
                                // 根据用户选择的时间来设置Calendar对象
                                c.set(Calendar.YEAR,year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                BaseApplication.set(getResources().getString(R.string.special_day),DateTimeUtil.CalendarToString(c));
                                Logger.i(DateTimeUtil.CalendarToString(c)+"");   //这里的时间是一个unix时间戳
                                // 提示闹钟设置完毕:
                                Toast.makeText(MainActivity.this, "日期设置完毕~"+DateTimeUtil.CalendarToString(c),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, currentDay.get(Calendar.YEAR), currentDay.get(Calendar.MONTH), currentDay
                        .get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.test:
                text_random.append(ContentProvider.getImgId(1000) + "-");
                break;
            default:
                break;
        }
    }
}
