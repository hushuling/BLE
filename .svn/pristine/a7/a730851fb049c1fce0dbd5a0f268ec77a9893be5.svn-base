package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;

import static com.xiekang.bluetooths.bluetooths.BltManager.BLUE_TOOTH_CLEAR;
import static com.xiekang.bluetooths.bluetooths.BltManager.BLUE_TOOTH_SEARTH;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 4.0下传统蓝牙连接工具类
 * @创建人 hsl20
 * @创建时间 2018/7/26 14:53
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BluetoothUtils {
  private static BluetoothUtils bluetoothUtils;
  private Bluetooth_Satus mbluetoothsatus;
  boolean isUnRegister = false;

  public static synchronized BluetoothUtils getInstance() {
    if (bluetoothUtils == null) {
      bluetoothUtils = new BluetoothUtils();
    }

    return bluetoothUtils;
  }

  public void BlueToothRegister(String bluetoothDevicename, Context context) {
    blueToothRegister(bluetoothDevicename, context);
    //启用蓝牙
    // BltManager.getInstance().clickBlt(getActivity(), BltContant.BLUE_TOOTH_OPEN);
    //第一次进来搜索设备
    BltManager.getInstance().clickBlt(BLUE_TOOTH_SEARTH);
  }

  public void setbluetoothsatus(Bluetooth_Satus mbluetoothsatus) {
    this.mbluetoothsatus = mbluetoothsatus;
  }

  /**
   * 注册蓝牙回调广播
   */
  private void blueToothRegister(final String bluetoothDevicename, Context context) {
    isUnRegister = true;
    BltManager.getInstance().registerBltReceiver(context, new BltManager.OnRegisterBltReceiver() {
      /**搜索到新设备
       * @param device
       */
      @Override
      public void onBluetoothDevice(BluetoothDevice device) {
        if (!TextUtils.isEmpty(device.getName())) {
          if (device.getName().contains(bluetoothDevicename)) {
            doConnect(device);
            BltManager.getInstance().stopSearthBltDevice();
          }
        }

      }

      /**连接中
       * @param device
       */
      @Override
      public void onBltIng(BluetoothDevice device) {

      }

      /**连接完成
       * @param device
       */
      @Override
      public void onBltEnd(BluetoothDevice device) {
        doConnect(device);
      }

      /**取消链接
       * @param device
       */
      @Override
      public void onBltNone(BluetoothDevice device) {
      }
    });
  }

  private void doConnect(final BluetoothDevice device) {
    //链接的操作应该在子线程
    new Thread(new Runnable() {
      @Override
      public void run() {
        BltManager.getInstance().createBond(device, handler);
      }
    }).start();
  }

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      String date;
      switch (msg.what) {
        case 4://已连接某个设备
          mbluetoothsatus.succed();
          break;
      }
    }
  };

  public BluetoothSocket getmBluetoothSocket() {
    return BltManager.getInstance().getmBluetoothSocket();
  }

  public void UnregisterReceiver() {
    if (isUnRegister) {
      //断开蓝牙连接
      BltManager.getInstance().clickBlt( BLUE_TOOTH_CLEAR);
      //取消数据监听
      BltManager.getInstance().unregisterReceiver();
    }
    isUnRegister = false;
  }
}
