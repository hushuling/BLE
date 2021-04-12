package com.xiekang.bluetooths.interfaces;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.interfaces
 * @类描述 获取体温的接口
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:39
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface GetTemperature extends Bluetooth_Satus{
  /**
   *
   * @param temp 体温数值
   */
  void getbloodfat(String temp);

  /**
   *
   * @param messager 异常温度提示
   */
  void errCode(String messager);
}
