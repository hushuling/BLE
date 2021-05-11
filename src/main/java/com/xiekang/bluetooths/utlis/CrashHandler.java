package com.xiekang.bluetooths.utlis;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.xiekang.bluetooths.utlis.Cotexts.getCrash;

/**
 * 未捕获异常处理类
 *
 * @author xw
 */
public class CrashHandler implements UncaughtExceptionHandler {
  public  final String TAG = "CrashHandler";

  // 系统默认的UncaughtException处理类
  private UncaughtExceptionHandler mDefaultHandler;
  // CrashHandler实例
  private static CrashHandler INSTANCE = new CrashHandler();
  // 程序的Context对象
  private Context mContext;
  // 用来存储设备信息和异常信息
  private Map<String, String> infos = new HashMap<String, String>();

  /**
   * 保证只有一个CrashHandler实例
   */
  private CrashHandler() {
  }

  /**
   * 获取CrashHandler实例 ,单例模式
   */
  public static CrashHandler getInstance() {
    return INSTANCE;
  }

  /**
   * 获得手机信息
   */
  public Map<String, String> phoneInfo() {
    if (infos.size() == 0) {
      collectDeviceInfo(mContext);
    }
    return infos;
  }

  /**
   * 初始化
   *
   */
  public void init() {
    mContext = ContextProvider.get().getContext();
    // 获取系统默认的UncaughtException处理器
    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    // 设置该CrashHandler为程序的默认处理器
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  /**
   * 当UncaughtException发生时会转入该函数来处理
   */
  @Override
  public void uncaughtException(Thread thread, Throwable ex) {
    if (!handleException(ex) && mDefaultHandler != null) {
      // 如果用户没有处理则让系统默认的异常处理器来处理
      mDefaultHandler.uncaughtException(thread, ex);
    } else {
      //MobclickAgent.onKillProcess(mContext);
      System.exit(0);
      android.os.Process.killProcess(android.os.Process.myPid());
    }
  }

  /**
   * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
   *
   * @param ex
   * @return true:如果处理了该异常信息;否则返回false.
   */
  private boolean handleException(Throwable ex) {
    if (ex == null) {
      return false;
    }
    // 收集设备参数信息
    collectDeviceInfo(mContext);
    // 保存日志文件
    saveCrashInfo2File(ex);
    return true;
  }

  /**
   * 获取应用目录路径
   *
   * @return
   */
  public static String getSaveDir() {
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
      return Environment.getExternalStorageDirectory() + Cotexts.getAppDir();
    }
    return Environment.getDataDirectory().getAbsolutePath()
        + Cotexts.getAppDir();
  }

  /**
   * 收集设备参数信息
   *
   * @param ctx
   */
  public void collectDeviceInfo(Context ctx) {
    try {
      PackageManager pm = ctx.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
          PackageManager.GET_ACTIVITIES);
      if (pi != null) {
        String versionName = pi.versionName == null ? "null"
            : pi.versionName;
        String versionCode = pi.versionCode + "";
        infos.put("versionName", versionName);
        infos.put("versionCode", versionCode);
      }
    } catch (NameNotFoundException e) {
      LogUtils.e(TAG, "an error occured when collect package info" + e);
    }
    Field[] fields = Build.class.getDeclaredFields();
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        infos.put(field.getName(), field.get(null).toString());
        // LogUtils.d(TAG, field.getName() + " : " + field.get(null));
      } catch (Exception e) {
        LogUtils.e(TAG, "an error occured when collect crash info" + e);
      }
    }
  }

  /**
   * 保存错误信息到文件中
   *
   * @param ex
   * @return 返回文件名称, 便于将文件传送到服务器
   */
  private void saveCrashInfo2File(Throwable ex) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : infos.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      sb.append(key + "=" + value + "\n");
    }

    Writer writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    ex.printStackTrace(printWriter);
    Throwable cause = ex.getCause();
    while (cause != null) {
      cause.printStackTrace(printWriter);
      cause = cause.getCause();
    }
    printWriter.close();
    String result = writer.toString();
    sb.append(result);
    System.out.println(sb);
    FileUtil.CreaterFile(getCrash());
    FileUtil.createFile(getSaveDir()+getCrash(), Cotexts.getCrash_Text());
    FileUtil.writeDataToFile(getSaveDir()+getCrash()+ Cotexts.getCrash_Text(), sb.toString());
  }

  /**
   * 写入日志
   */
  public static void writeLog(String log, String fileName, boolean bl) {
    FileOutputStream fos = null;
    try {
      String time = DateUtil.getDate();
      log = time + ":" + log + "\r\n";
      if (isSDCardExist()) {
        File dir = new File(getSaveDir());
        if (!dir.exists()) {
          dir.mkdirs();
        }
        fos = new FileOutputStream(getSaveDir()
            + fileName, bl);
        fos.write(log.getBytes());
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (fos != null) fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 是否存在SD卡
   *
   * @return
   */
  public static boolean isSDCardExist() {

    boolean sdCardExist = Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED);

    return sdCardExist;
  }
}
