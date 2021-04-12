package com.xiekang.bluetooths.utlis;



import com.xiekang.bluetooths.interfaces.TimerListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment
 * @类描述 describe 轮询工具类
 * @创建人 hsl20
 * @创建时间 2018/7/26 16:59
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class ReadTaskTimer {
  public static ReadTaskTimer sReadTaskTimer;
  private TimerListener mTimerListener;
  private int readTime = 20;
  private Timer timer;
  long time;
  int count;

  public static synchronized ReadTaskTimer getInstance() {
    if (sReadTaskTimer == null) {
      sReadTaskTimer = new ReadTaskTimer();
    }

    return sReadTaskTimer;
  }

  private ReadTaskTimer() {
    this.init();
  }

  private void init() {
    this.timer = new Timer();
    this.startReadTask();
  }

  private void startReadTask() {
    TimerTask var1 = new TimerTask() {
      public void run() {
        try {
          ++ReadTaskTimer.this.count;
          if (System.currentTimeMillis() - ReadTaskTimer.this.time >= 1000L) {
            ReadTaskTimer.this.count = 0;
            ReadTaskTimer.this.time = System.currentTimeMillis();
          }
          if (ReadTaskTimer.this.mTimerListener != null) {
            ReadTaskTimer.this.mTimerListener.onTimer();
          }
        } catch (Exception var2) {
          var2.printStackTrace();
        }

      }
    };
    this.timer.schedule(var1, 0L, (long) this.readTime);
  }

  public void stopReadTask() {
    try {
      this.timer.purge();
      this.timer.cancel();
      this.timer = null;
      sReadTaskTimer = null;
    } catch (Exception var2) {
      ;
    }

  }

  public void setTimerListener(TimerListener var1) {
    this.mTimerListener = var1;
  }
}
