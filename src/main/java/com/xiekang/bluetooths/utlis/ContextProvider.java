package com.xiekang.bluetooths.utlis;

import android.app.Application;
import android.content.Context;

/**
 * @项目名称 eHealthAPP
 * @类名 name：com.example.baseinstallation
 * @类描述 describe
 * @创建人 hsl201306
 * @创建时间 2020-02-20 12:10
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class ContextProvider {
  private static volatile ContextProvider instance;
  private Context mContext;

  private ContextProvider(Context context) {
    mContext = context;
  }

  /**
   * 获取实例
   */
  public static ContextProvider get() {
    if (instance == null) {
      synchronized (ContextProvider.class) {
        if (instance == null) {
          Context context = ApplicationContextProvider.mcontext;
          if (context == null) {
            throw new IllegalStateException("context == null");
          }
          instance = new ContextProvider(context);
        }
      }
    }
    return instance;
  }

  /**
   * 获取上下文
   */
  public Context getContext() {
    return mContext;
  }

  public Application getApplication() {
    return (Application) mContext.getApplicationContext();
  }


}
