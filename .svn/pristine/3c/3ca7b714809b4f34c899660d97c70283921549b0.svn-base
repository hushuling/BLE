package com.xiekang.bluetooths.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * @项目名称 bluetooths
 * @类名 name：com.xiekang.bluetooths.interfaces
 * @类描述 蓝牙搜索成功 搜索完成
 * @创建人 hsl20
 * @创建时间 2021-01-20 15:17
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface Blutooth_Search {
  /**
   * 搜索到对应的设备
   * @param remoteDevice 搜索到的蓝牙对象
   * @param scanRecord 远程设备提供的配对号
   */
  void Searched(List<BluetoothDevice> remoteDevice,List< byte[]> scanRecord);

  /**
   * 搜索完成
   */
  void Timeout();
}
