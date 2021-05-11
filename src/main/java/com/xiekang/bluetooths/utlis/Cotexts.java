package com.xiekang.bluetooths.utlis;

/**
 * Created by Administrator on 2017/3/9 0009.
 */

public class Cotexts {

  /*当前版本*/
  private static final String VERSION = "v1.0.1";

  /**
   * 激活加密MD5自定义key
   */
  private static final String REGIATER_KEY = "q1azw2sxe3dcr4fv!@#$%%^";

  /**
   * 激活码
   **/
  private static final String SignValue = "SignValue";
  //设备码
  private static final String SignKey = "SignKey";

  /**
   * 应用目录名称
   */
  private static final String APP_DIR = "/Bluetooths";
  private static final String SP_NAME = "Bluetooths";

  public static String getVERSION() {
    return VERSION;
  }

  public static String getRegiaterKey() {
    return REGIATER_KEY;
  }

  public static String getAppDir() {
    return APP_DIR;
  }

  public static String getSpName() {
    return SP_NAME;
  }

  public static String getSignValue() {
    return SignValue;
  }

  public static String getSignKey() {
    return SignKey;
  }

  public static String getCrash_Text() {
    return Crash_Text;
  }

  public static String getCrash() {
    return Crash;
  }

  private static final String Crash_Text = "/" + DateUtil.getDate() + ".txt";
  private static final String Crash = "/Crash";

}
