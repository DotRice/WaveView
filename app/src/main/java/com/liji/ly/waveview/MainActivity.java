package com.liji.ly.waveview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.liji.excellentwaveview.WaveView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Timer timer;
    private Timer timerCircle;
    WaveView waveview;
    WaveView waveviewCicle;

    private MyTimerTask mTask;
    private MyTimerTaskCircle mTaskCircle;
    int i = 0;
    int j = 0;
    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                waveview.setProgress((int) (msg.obj));
                waveview.start();
            } else if (msg.what == 2) {
                waveviewCicle.setProgress((int) (msg.obj));
                waveviewCicle.start();
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waveview = (WaveView) findViewById(R.id.waveview);
        waveviewCicle = (WaveView) findViewById(R.id.waveview_circle);
        mTask = new MyTimerTask(updateHandler);
        mTaskCircle = new MyTimerTaskCircle(updateHandler);
        timer = new Timer();
        timerCircle = new Timer();
        timer.schedule(mTask, 0, 100);
        timerCircle.schedule(mTaskCircle, 0, 100);

    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            Message message = handler.obtainMessage();
            message.what = 1;
            message.obj = (i++) % 101;
            handler.sendMessage(message);
        }

    }

    class MyTimerTaskCircle extends TimerTask {
        Handler handler;

        public MyTimerTaskCircle(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            Message message = handler.obtainMessage();
            message.what = 2;
            message.obj = (j++) % 101;
            handler.sendMessage(message);
        }

    }


}
