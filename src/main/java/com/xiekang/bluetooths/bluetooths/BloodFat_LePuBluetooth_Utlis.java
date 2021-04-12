package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import com.xiekang.bluetooths.interfaces.GetBloodfat;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import net.litcare.lplibrary.bf.BFException;
import net.litcare.lplibrary.bf.BFRecordHelper;
import net.litcare.lplibrary.bf.BFType;

import java.util.List;
import java.util.UUID;


/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 乐普血脂
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BloodFat_LePuBluetooth_Utlis {
  private static BloodFat_LePuBluetooth_Utlis bloodFatLePuBluetoothUtlis;
  private BluetoothGatt bluetoothGatt;
  private BluetoothGattCharacteristic controlCharacteristicl, notifyCharacteristic, batteryCharacteristic;
  private GetBloodfat getBloodfat;
  private StringBuffer resultBuffer = new StringBuffer();
  private BloodFat_LePuBluetooth_Utlis() {
  }
  public static BloodFat_LePuBluetooth_Utlis getInstance() {
    if (bloodFatLePuBluetoothUtlis == null) {
      bloodFatLePuBluetoothUtlis = new BloodFat_LePuBluetooth_Utlis();
    }
    return bloodFatLePuBluetoothUtlis;
  }
  /**
   * 连接蓝牙
   *
   * @param
   */
  public void connect(BluetoothDevice bluetoothDevice, GetBloodfat bluetooth_satus) {
    RegisterReceiver(bluetooth_satus);
    bluetoothGatt = bluetoothDevice.connectGatt(ContextProvider.get().getContext(), false, gattCallback);
    if (bluetoothGatt == null) UnRegisterReceiver();
    LogUtils.e(bluetoothGatt);
  }




  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {   super.onConnectionStateChange(gatt, status, newState);
      LogUtils.e("连接状态newState：" + newState + "status:" + status);
      switch (newState) {
        case BluetoothGatt.STATE_CONNECTED:
          //当连接成果以后，开启这个服务，就可以通信了
          bluetoothGatt.discoverServices();
          if (getBloodfat != null) getBloodfat.succed();


          break;
        case BluetoothGatt.STATE_CONNECTING:
          break;
        case BluetoothGatt.STATE_DISCONNECTED:
          UnRegisterReceiver();
          break;
        default:
          break;
      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      super.onServicesDiscovered(gatt, status);
      if (status == BluetoothGatt.GATT_SUCCESS) {
        List<BluetoothGattService> services = bluetoothGatt.getServices();
        for (BluetoothGattService bluetoothGattService : services) {
          List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
          for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
            System.out.println("====================>UUID=" + bluetoothGattCharacteristic.getUuid());
            //读和写是同一个UUID的情况
            if (bluetoothGattCharacteristic.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
              //写数据的特征值
              controlCharacteristicl = bluetoothGattCharacteristic;//读和写的特征值所赋的值是一样的
              //读数据的特征值
              notifyCharacteristic = bluetoothGattCharacteristic;//读和写的特征值所赋的值是一样的
              bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
              enableNotification(true, notifyCharacteristic);
              send();
            }
          }
        }
      }
    }
    private static final int BUFFER_LENGTH = 44;
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);
      resultBuffer.append(characteristic.getStringValue(0));
      if (resultBuffer.length() == BUFFER_LENGTH) {
        send(KEY_RECEIVE_SUCCESS);
        onResult(resultBuffer.toString());
        resultBuffer.delete(0,resultBuffer.length());

      }
    }

  };

  private void onResult(String bluetoothResult) {
    try {
      BFRecordHelper bfRecordHelper = BFRecordHelper.parseFromBTResult(bluetoothResult);
      ResolverDate(   bfRecordHelper.getValueString(BFType.CHOL), bfRecordHelper.getValueString(BFType.TRIG),
          bfRecordHelper.getValueString(BFType.HDL),  bfRecordHelper.getValueString(BFType.LDL));
    } catch (BFException e) {
      e.printStackTrace();
    }
  }

  /**
   * 解析数据
   *
   * @param @mOutputInfo
   */
  private void ResolverDate(String chol,
                            String TRIG,
                            String HDL,
                            String LDL) {

    //总胆固醇
    {
      StringBuffer stringBuffer = new StringBuffer();
      if (chol.contains(">")) {
        String[] hdl = chol.split(">");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        chol = stringBuffer.toString();
      } else if (chol.contains("<")) {
        String[] hdl = chol.split("<");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        chol = stringBuffer.toString();
      }
    }
    //甘油三酯
    {
      StringBuffer stringBuffer = new StringBuffer();
      if (TRIG.contains(">")) {
        String[] hdl = TRIG.split(">");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        TRIG = stringBuffer.toString();
      } else if (TRIG.contains("<")) {
        String[] hdl = TRIG.split("<");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        TRIG = stringBuffer.toString();
      }
    }
    //高密度胆固醇
    {
      StringBuffer stringBuffer = new StringBuffer();
      if (HDL.contains(">")) {
        String[] hdl = HDL.split(">");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        HDL = stringBuffer.toString();
      } else if (HDL.contains("<")) {
        String[] hdl = HDL.split("<");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        HDL = stringBuffer.toString();
      }
    }
    //低密度胆固醇
    {
      StringBuffer stringBuffer = new StringBuffer();
      if (LDL.contains(">")) {
        String[] hdl = LDL.split(">");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        LDL = stringBuffer.toString();
      } else if (LDL.contains("<")) {
        String[] hdl = LDL.split("<");
        for (int i = 0; i < hdl.length; i++) {
          stringBuffer.append(hdl[i]);
        }
        LDL = stringBuffer.toString();
      }
    }
    if (chol.contains("--")) {
      chol = "0";
    }
    if (TRIG.contains("--")) {
      TRIG = "0";
    }
    if (HDL.contains("--")) {
      HDL ="0";
    }
    if (LDL.contains("--")) {
      LDL = "0";
    }
    if (getBloodfat!=null)getBloodfat.getbloodfat(Float.valueOf(chol),
        Float.valueOf(TRIG),
        Float.valueOf(HDL),
        Float.valueOf(LDL));
    UnRegisterReceiver();
  }
  //打开蓝牙接收数据的通道
  private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
    if (bluetoothGatt == null || characteristic == null)
      return false;
    if (!bluetoothGatt.setCharacteristicNotification(characteristic, enable))
      return false;
    //这里的UUID 是不变的 不要改动
    BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
    if (clientConfig == null)
      return false;
    if (enable) {
      clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    } else {
      clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    }
    return bluetoothGatt.writeDescriptor(clientConfig);
  }
  private static final String KEY_CONNECT = "connect";
  private static final String KEY_RECEIVE_SUCCESS = "true";

  /**
   * 请求数据
   */
  public void send() {
    if (controlCharacteristicl != null) {
      LogUtils.e("发送请求数据的命令：" + KEY_CONNECT);
      controlCharacteristicl.setValue(KEY_CONNECT);
      if (bluetoothGatt!=null)  bluetoothGatt.writeCharacteristic(controlCharacteristicl);
    }
  }

  private void send(String KEY_CONNECT) {
    if (controlCharacteristicl != null) {
      LogUtils.e("完成数据的命令：" + KEY_CONNECT);
      controlCharacteristicl.setValue(KEY_CONNECT);
      if (bluetoothGatt!=null) bluetoothGatt.writeCharacteristic(controlCharacteristicl);
    }
  }
  private void   RegisterReceiver(GetBloodfat getBloodfat) {
    this.getBloodfat=getBloodfat;
    resultBuffer.delete(0,resultBuffer.length());
  }

  public void UnRegisterReceiver() {
    resultBuffer.delete(0,resultBuffer.length());
    if (getBloodfat!=null)getBloodfat.err();
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
      bluetoothGatt=null;
    }
  }
}
