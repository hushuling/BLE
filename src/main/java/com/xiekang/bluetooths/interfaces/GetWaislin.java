package com.xiekang.bluetooths.interfaces;

/**
 * @项目名称 StudentHealthPack
 * @类名 name：com.xiekang.bluetooths.interfaces
 * @类描述 腰围尺接口
 * @创建人 hsl20
 * @创建时间 2021-05-11 11:41
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface GetWaislin  extends Bluetooth_Satus{
  void getwaislin(float yaowei, float tunwei);
}
