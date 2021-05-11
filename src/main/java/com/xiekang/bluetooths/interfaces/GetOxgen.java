package com.xiekang.bluetooths.interfaces;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.interfaces
 * @类描述 获取血氧的接口
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:39
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface GetOxgen<T> extends Bluetooth_Satus {
  /**
   *
   * @param spo2 血氧饱和度值。单位为%。0 为无效值。如果 nSpO 为 98，则血氧饱和
   * 度为 98%
   * @param pr 脉率值。单位是 bpm，0 为无效值
   */
  void getOxgen(final int spo2, final int pr);

  /**
   * 血氧波形数据
   * @param d 参照示例
   */
  void startDraw(T d);


}
