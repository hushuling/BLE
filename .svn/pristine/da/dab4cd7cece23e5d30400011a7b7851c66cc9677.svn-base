package com.xiekang.bluetooths.bluetooths.oxgen;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.creative.FingerOximeter.FingerOximeter;
import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.creative.base.BaseDate;
import com.xiekang.bluetooths.interfaces.GetOxgen;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.util.List;


/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 科瑞康血氧
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Oxgen_Bluetooth_Utlis {
  private static Oxgen_Bluetooth_Utlis bloodpress_bluetooth_utlis;
  private GetOxgen getOxgen;
  private ReaderBLE readerBLE;
  private SenderBLE senderBLE;

  public static Oxgen_Bluetooth_Utlis getInstance() {
    if (bloodpress_bluetooth_utlis == null) {
      bloodpress_bluetooth_utlis = new Oxgen_Bluetooth_Utlis();
    }
    return bloodpress_bluetooth_utlis;
  }

  /**
   * 连接蓝牙
   *
   * @param
   */
  public void connect(BluetoothDevice bluetoothDevice, GetOxgen bluetooth_satus) {
    RegisterReceiver(bluetooth_satus);
    if (mManager != null) mManager.connect(bluetoothDevice.getAddress());
  }

  private BLEManager mManager;

  private void RegisterReceiver(GetOxgen getTemperature) {
    this.getOxgen = getTemperature;
    mManager = new BLEManager();
    ContextProvider.get().getContext().registerReceiver(mGattUpdateReceiver, BLEManager.makeGattUpdateIntentFilter());
  }

  public void UnRegisterReceiver() {
    if (getOxgen!=null)getOxgen.err();
    if (mManager != null) {
      if (senderBLE != null) senderBLE.close();
      if (readerBLE != null) readerBLE.close();
      mManager.closeService();
      mManager.disconnect();
      mManager = null;
      ContextProvider.get().getContext().unregisterReceiver(mGattUpdateReceiver);
    }

    mFingerOximeter = null;

  }

  public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      LogUtils.e(this.getClass().getName(), "action->" + action);
      if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
        LogUtils.e(this.getClass().getName(), "链接成功*****");
        if (getOxgen != null) getOxgen.succed();
      } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
        LogUtils.e(this.getClass().getName(), "链接断开*****");
        UnRegisterReceiver();

//    }
      } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
        LogUtils.e("find device, start ACTION_GATT_SERVICES_DISCOVERED");
      } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
        //Toast.makeText(MainActivity.this,intent.getStringExtra(BluetoothLeService.EXTRA_DATA),Toast.LENGTH_SHORT).show();
        LogUtils.e("find device, start ACTION_DATA_AVAILABLE");
      } else if (BluetoothLeService.ACTION_SPO2_DATA_AVAILABLE.equals(action)) {
        //byte[] data =intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
        //Log.d(TAG, "MainActivity received:"+Arrays.toString(data));
        LogUtils.e("find device, start ACTION_SPO2_DATA_AVAILABLE");
      } else if (BluetoothLeService.ACTION_CHARACTER_NOTIFICATION.equals(action)) {
        startFingerOximeter();

      } else if (BLEManager.ACTION_FIND_DEVICE.equals(action)) {
        LogUtils.e("find device, start service");

      } else if (BLEManager.ACTION_SEARCH_TIME_OUT.equals(action)) {
        LogUtils.e("search time out!");

      } else if (BLEManager.ACTION_START_SCAN.equals(action)) {
        LogUtils.e("discoverying");

      }
    }
  };

  private FingerOximeter mFingerOximeter;

  private void startFingerOximeter() {
    if (BLEManager.mBleHelper != null) {
      readerBLE = new ReaderBLE(BLEManager.mBleHelper);
      senderBLE = new SenderBLE(BLEManager.mBleHelper);
      mFingerOximeter = new FingerOximeter(readerBLE, senderBLE, new FingerOximeterCallBack());
      mFingerOximeter.Start();
      mFingerOximeter.SetWaveAction(true);

    }
  }
  //----------- message -------------
  /**
   * 血氧参数
   */
  public static final byte MSG_DATA_SPO2_PARA = 0x01;
  /**
   * 血氧波形数据
   */
  public static final byte MSG_DATA_SPO2_WAVE = 0x02;
  /**
   * 血氧搏动标记
   */
  public static final byte MSG_DATA_PULSE = 0x03;
  /**
   * 取消搏动标记
   */
  public static final byte RECEIVEMSG_PULSE_OFF = 0x04;
  /**
   * 导联脱落
   */
  public static final byte MSG_PROBE_OFF = 0x06;
  /**
   * 蓝牙状态信息
   */
  public static final byte MSG_BLUETOOTH_STATE = 0x05;
  /**
   * 消息: 电池电量为0 , message: battery level is 0
   */
  private static final int BATTERY_ZERO = 0x302;
  private Handler myHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case MSG_BLUETOOTH_STATE: //蓝牙状态信息
          //TipsToast.gank((String) msg.obj);
          //Log.d(TAG, (String) msg.obj);

          break;
        case MSG_DATA_SPO2_WAVE:  //波形数据
          //List<Wave> waves = (List<Wave>) msg.obj;

          break;
        case MSG_DATA_SPO2_PARA: //波形参数
          //nStatus探头状态 ->true为正常 false为脱落
          //probe status ->true noraml, false off
          if (!msg.getData().getBoolean("nStatus")) {

            myHandler.removeMessages(BATTERY_ZERO);
            myHandler.sendEmptyMessage(MSG_PROBE_OFF);
            UnRegisterReceiver();
            //探头脱落，过一段时间，系统会自动断开蓝牙
            // auto disconnect bluetooth after probe off a period
            // mBluetoothLeService.disconnect();
//            isconnect=false;
            break;
          } else {
//            isconnect=true;
            int nSpo2 = msg.getData().getInt("nSpO2");
            int nPR = msg.getData().getInt("nPR");
            float fPI = msg.getData().getFloat("fPI");
            float b = msg.getData().getFloat("nPower");
            if (fPI > 0) {
              LogUtils.e("hanlder接收有效数据" + nSpo2 + " " + nPR + " " + fPI);
              getOxgen.getOxgen(nSpo2, nPR);
            } else {

            }

//     int battery = 0;
//     if (b < 2.5f) {
//      battery = 0;
//     } else if (b < 2.8f) {
//      battery = 1;
//     } else if (b < 3.0f)
//      battery = 2;
//     else {
//      battery = 3;
//     }
//
//     setBattery(battery);
//            setTVSPO2(nSpo2 + "");
//            setTVPR(nPR + "");
//            setTVPI(fPI + "");

            LogUtils.e("hanlder接收数据" + nSpo2 + " " + nPR + " " + fPI);
          }

          break;
        case MSG_DATA_PULSE:
          //showPulse(true);

          break;
        case RECEIVEMSG_PULSE_OFF:
          //showPulse(false);

          break;
        case MSG_PROBE_OFF:
//				if(mManager!=null){
//					mManager.disconnect();
//				}
//          resultsueccss=false;
//          if (!isFinishing()) {
//            if (mManager != null && address != null) {
//              mManager.connect(address);
//            } else {
//              checkBluetoothPermission();
//            }
//          }
//     TipsToast.gank("设备已脱落！");
          LogUtils.e("设备脱落");

          UnRegisterReceiver();
          break;
        default:
          break;
      }
    }
  };

  /**
   * 收到的血氧仪数据
   * received FingerOximeter of data
   */
  class FingerOximeterCallBack implements IFingerOximeterCallBack {

    @Override
    public void OnGetSpO2Param(int nSpO2, int nPR, float fPI, boolean nStatus, int nMode, float nPower, int i3) {

      Message msg = myHandler.obtainMessage(MSG_DATA_SPO2_PARA);
      Bundle data = new Bundle();
      data.putInt("nSpO2", nSpO2);
      data.putInt("nPR", nPR);
      data.putFloat("fPI", fPI);
      data.putFloat("nPower", nPower);
      data.putBoolean("nStatus", nStatus);
      data.putInt("nMode", nMode);
      data.putFloat("nPower", nPower);
      msg.setData(data);
      myHandler.sendMessage(msg);
      LogUtils.e("数据" + nSpO2 + " " + nPR + " " + fPI);

    }

    //血氧波形数据采样频率：50Hz，每包发送 5 个波形数据，即每 1 秒发送 10 包波形数据
    //参数 waves 对应一包数据
    //spo2 sampling rate is 50hz, 5 wave data in a packet,
    //send 10 packet 1/s. param "waves" is 1 data packet
    @Override
    public void OnGetSpO2Wave(List<BaseDate.Wave> waves) {
      //Log.d(TAG, "wave.size:"+waves.size()); // size = 5
      for (int i = 0; i < waves.size(); i++) {
        if (getOxgen != null) getOxgen.startDraw(waves.get(i));
      }
//      myHandler.obtainMessage(MSG_DATA_SPO2_WAVE, waves).sendToTarget();
    }

    @Override
    public void OnGetDeviceVer(int nHWMajor, int nHWMinor, int nSWMajor, int nSWMinor) {
      // myHandler.obtainMessage(MSG_BLUETOOTH_STATE, "device info,获取到设备信息:" + nHWMajor).sendToTarget();
      LogUtils.e("device info,获取到设备信息:" + nHWMajor);
    }

    @Override
    public void OnConnectLose() {
      LogUtils.e("connect lost,连接丟失");
      //myHandler.obtainMessage(MSG_BLUETOOTH_STATE, "connect lost,连接丟失").sendToTarget();
    }
  }

}
