package com.xiekang.bluetooths.interfaces;

import java.util.HashMap;

/**
 * 获取体脂秤数据的接口
 */
public interface GetBodyfat extends Bluetooth_Satus {
  void getBodyfat(HashMap<String,Double> all, String height);
  // 连接异常 重试...
  void ConectLost();
}
