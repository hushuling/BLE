package com.xiekang.bluetooths.interfaces;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.interfaces
 * @类描述 describe 蓝牙连接状态
 * @创建人 hsl20
 * @创建时间 2018/7/26 15:07
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface Bluetooth_Satus {
  /*连接成功*/
  void succed();
  /**
   * 蓝牙连接断开 &&连接失败&&连接失败
   */
  void err();
}
