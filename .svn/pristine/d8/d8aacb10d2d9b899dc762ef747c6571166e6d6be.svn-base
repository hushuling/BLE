package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.xiekang.bluetooths.utlis.ContextProvider;

/**
 * @项目名称 MedicalFollowUp
 * @类名 name：com.xiekang.medicalfollowup.utils
 * @类描述 全局蓝牙适配器
 * @创建人 hsl20
 * @创建时间 2020-03-12 14:02
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BluetoothAdapterContext {
  private static BluetoothAdapterContext mbluetooth;
  private final BluetoothAdapter bluetoothAdapter;

  public BluetoothAdapter getBluetoothAdapter() {
    return bluetoothAdapter;
  }

  private BluetoothAdapterContext() {
    BluetoothManager bluetoothManager = (BluetoothManager) ContextProvider.get().getContext().getSystemService(Context.BLUETOOTH_SERVICE);
    //得到蓝牙的适配器
    bluetoothAdapter = bluetoothManager.getAdapter();
  }

  public static BluetoothAdapterContext getInstance() {
    if (mbluetooth==null){
      mbluetooth=new BluetoothAdapterContext();
    }
    return mbluetooth;
  }
}
