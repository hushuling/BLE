package com.xiekang.bluetooths.utlis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SharePrefrenceUtil {

  private static SharedPreferences sharedPreferences = null;
  private static Editor editor = null;

  /**
   * 保存参数
   * @param key
   * @param data
   */
  @SuppressLint("WrongConstant")
  public static void saveParam( String key, Object data) {
    String type;
    if (data == null) {
      type = "";
    } else {
      type = data.getClass().getSimpleName();
    }

    sharedPreferences =  ContextProvider.get().getContext().getSharedPreferences(Cotexts.getSpName(),
        Context.MODE_APPEND);
    editor = sharedPreferences.edit();

    if ("Integer".equals(type)) {
      editor.putInt(key, (Integer) data);
    } else if ("String".equals(type)) {
      editor.putString(key, (String) data);
    } else if ("Float".equals(type)) {
      editor.putFloat(key, (Float) data);
    } else if ("Boolean".equals(type)) {
      editor.putBoolean(key, (Boolean) data);
    } else if ("Long".equals(type)) {
      editor.putLong(key, (Long) data);
    } else if (data == null) {
      editor.putString(key, "");
    }
    editor.commit();
  }

  //保存自定义对象
  @SuppressLint("WrongConstant")
  public static boolean SaveMode( String key, Object date) {
    SharedPreferences share =  ContextProvider.get().getContext().getSharedPreferences(Cotexts.getSpName(),
        Context.MODE_APPEND);
    if (date == null) {
      Editor editor = share.edit().remove(key);
      return editor.commit();
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(date);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
// 将对象放到OutputStream中
// 将对象转换成byte数组，并将其进行base64编码
    String objectStr = new String(Base64.encode(baos.toByteArray(),
        Base64.DEFAULT));
    try {
      baos.close();
      oos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Editor editor = share.edit();
// 将编码后的字符串写到base64.xml文件中
    editor.putString(key, objectStr);
    return editor.commit();
  }
  @SuppressLint("WrongConstant")
  public static Object getObjectFromShare( String key) {
    SharedPreferences sharePre =  ContextProvider.get().getContext().getSharedPreferences(Cotexts.getSpName(),
        Context.MODE_APPEND);
    try {
      String wordBase64 = sharePre.getString(key, "");
      // 将base64格式字符串还原成byte数组
      if (wordBase64 == null || wordBase64.equals("")) { // 不可少，否则在下面会报java.io.StreamCorruptedException
        return null;
      }
      byte[] objBytes = Base64.decode(wordBase64.getBytes(),
          Base64.DEFAULT);
      ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      // 将byte数组转换成product对象
      Object obj = ois.readObject();
      bais.close();
      ois.close();
      return obj;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取参数
   * defValue为为默认值，如果当前获取不到数据就返回它
   *
   * @param key
   * @param defValue
   * @return
   */
  @SuppressLint("WrongConstant")
  public static Object getData( String key, Object defValue) {
    String type = defValue.getClass().getSimpleName();
    sharedPreferences =  ContextProvider.get().getContext().getSharedPreferences(Cotexts.getSpName(),
        Context.MODE_APPEND);
    if ("Integer".equals(type)) {
      return sharedPreferences.getInt(key, (Integer) defValue);
    } else if ("Boolean".equals(type)) {
      return sharedPreferences.getBoolean(key, (Boolean) defValue);
    } else if ("String".equals(type)) {
      return sharedPreferences.getString(key, (String) defValue);
    } else if ("Float".equals(type)) {
      return sharedPreferences.getFloat(key, (Float) defValue);
    } else if ("Long".equals(type)) {
      return sharedPreferences.getLong(key, (Long) defValue);
    }

    return null;
  }

  /**
   * 删除某个参数
   *
   * @param key
   */
  @SuppressLint("WrongConstant")
  public static void selectParam( String key) {

    sharedPreferences =  ContextProvider.get().getContext().getSharedPreferences(Cotexts.getSpName(),
        Context.MODE_APPEND);
    editor = sharedPreferences.edit();
    editor.remove(key);
    editor.commit();
  }

  public static void clear() {
    SharedPreferences preferences =  ContextProvider.get().getContext().getSharedPreferences(Cotexts.getSpName(), Context.MODE_PRIVATE);
    Editor editor = preferences.edit();
    editor.clear();

    editor.apply();
  }
}
