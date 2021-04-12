package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.xiekang.bluetooths.interfaces.GetTemperature;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.util.List;
import java.util.UUID;


import static com.xiekang.bluetooths.utlis.HexUtil.Bytes2HexString;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 体达体温
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Temp_Bluetooth_Utlis {
  private static Temp_Bluetooth_Utlis bloodpress_bluetooth_utlis;
  private BluetoothGatt bluetoothGatt;
  private BluetoothGattCharacteristic notifyCharacteristic;
  private GetTemperature geturidate;

  public static String GATT_SERVICE_PRIMARY = "0000fff1-0000-1000-8000-00805f9b34fb";
  public static String CHARACTERISTIC_READAB  = "00002902-0000-1000-8000-00805f9b34fb";
  public static Temp_Bluetooth_Utlis getInstance() {
    if (bloodpress_bluetooth_utlis==null){
      bloodpress_bluetooth_utlis=new Temp_Bluetooth_Utlis();
    }
    return bloodpress_bluetooth_utlis;
  }
  /**
   * 连接蓝牙
   *
   * @param
   */
  public void connect(BluetoothDevice bluetoothDevice, GetTemperature bluetooth_satus) {
    RegisterReceiver(bluetooth_satus);
      bluetoothGatt = bluetoothDevice.connectGatt(ContextProvider.get().getContext(), false, gattCallback);
    if (bluetoothGatt==null)UnRegisterReceiver();
      LogUtils.e(bluetoothGatt);
  }

  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
      super.onConnectionStateChange(gatt, status, newState);
      LogUtils.e("连接状态newState："+newState+"status:"+status);
          switch (newState) {
            case BluetoothGatt.STATE_CONNECTED:
              //当连接成果以后，开启这个服务，就可以通信了
              bluetoothGatt.discoverServices();
              if (geturidate!=null)geturidate.succed();
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
      LogUtils.e("onServicesDiscovered："+status+":+status");
      if (status == BluetoothGatt.GATT_SUCCESS) {
        List<BluetoothGattService> services = bluetoothGatt.getServices();
        for (BluetoothGattService bluetoothGattService : services) {
          List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
          for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
            System.out.println("====================>UUID=" + bluetoothGattCharacteristic.getUuid());
            //读和写是同一个UUID的情况
            if (bluetoothGattCharacteristic.getUuid().toString().equals(GATT_SERVICE_PRIMARY)) {
              //读数据的特征值
              notifyCharacteristic = bluetoothGattCharacteristic;//读和写的特征值所赋的值是一样的
              bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
              enableNotification(true, notifyCharacteristic);
            }
          }
        }
      }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);
      if (characteristic.getUuid().toString().equals(notifyCharacteristic.getUuid().toString())) {
        final byte[] value = characteristic.getValue();
        final String date= HexUtil.hexStringToString(Bytes2HexString(value));
        //在这个方法里面获得蓝牙设备发送给APP的数据
        LogUtils.e("收到数据拉==========》",date);
        if (date.contains("ErH") || date.contains("ErL") || date.contains("ErP")) {
          if (date.contains("H")) {
            if (geturidate!=null)geturidate.errCode("测量物体温度过高");
          }
          if (date.contains("L")) {
            if (geturidate!=null)geturidate.errCode("测量物体温度过低");
          }
          if (date.contains("P")) {
            if (geturidate!=null)geturidate.errCode("设备异常");
          }
        } else {
          if (date.contains("Body:")) {
            if (date.contains("C")) {
              final String tem = date.substring(date.indexOf(":") + 1, date.indexOf("C"));
              if (geturidate!=null)geturidate.getbloodfat(tem);
              UnRegisterReceiver();
            } else {
              if (geturidate!=null)geturidate.errCode("请设置正确的单位");
            }

          } else {
            if (geturidate!=null)geturidate.errCode("请测量正常的物体");
          }
        }





      }
    }

  };
  //打开蓝牙接收数据的通道
  private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
    if (bluetoothGatt == null || characteristic == null)
      return false;
    if (!bluetoothGatt.setCharacteristicNotification(characteristic, enable))
      return false;
    //这里的UUID 是不变的 不要改动
    BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_READAB));
    if (clientConfig == null)
      return false;
    if (enable) {
      clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    } else {
      clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    }
    return bluetoothGatt.writeDescriptor(clientConfig);
  }

  private void   RegisterReceiver(GetTemperature getTemperature) {
    this.geturidate=getTemperature;
  }

  public void UnRegisterReceiver() {
    if (geturidate!=null)geturidate.err();
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
      bluetoothGatt=null;
    }
  }
}
