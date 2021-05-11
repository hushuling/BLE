package com.xiekang.bluetooths.interfaces;

/**
 * 获取身高体重的接口
 */
public interface GetH_Weight extends Bluetooth_Satus {
  /**
   * @param weight
   * @param height
   */
  void getH_Weight(float weight, float height);
}
