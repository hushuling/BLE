package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetH_Weight;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.util.List;
import java.util.UUID;

import static com.xiekang.bluetooths.utlis.DateUtil.getDecimal;
import static com.xiekang.bluetooths.utlis.HexUtil.Bytes2HexString;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 鼎恒身高体重
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class DingHen_W_Heigth_Bluetooth_Utlis implements BluetoothDriver<GetH_Weight> {
  private static DingHen_W_Heigth_Bluetooth_Utlis bloodpress_bluetooth_utlis;
  private BluetoothGatt bluetoothGatt;
  private BluetoothGattCharacteristic notifyCharacteristic;
  private GetH_Weight getH_weight;
  private Bluetooth_Satus satus;
  StringBuffer stringBuffer = new StringBuffer();
  public static String GATT_SERVICE_PRIMARY = "0000ffe1-0000-1000-8000-00805f9b34fb";
  public static String CHARACTERISTIC_READAB = "00002902-0000-1000-8000-00805f9b34fb";
  private BluetoothDevice bluetoothDevice;
  public static DingHen_W_Heigth_Bluetooth_Utlis getInstance() {
    if (bloodpress_bluetooth_utlis == null) {
      bloodpress_bluetooth_utlis = new DingHen_W_Heigth_Bluetooth_Utlis();
    }
    return bloodpress_bluetooth_utlis;
  }

  /**
   * 连接蓝牙
   *
   * @param
   */


  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
      super.onConnectionStateChange(gatt, status, newState);
      LogUtils.e("连接状态newState：" + newState + "status:" + status);
      switch (newState) {
        case BluetoothGatt.STATE_CONNECTED:
          //当连接成果以后，开启这个服务，就可以通信了
          bluetoothGatt.discoverServices();
          if (getH_weight != null) getH_weight.succed();
          if (satus!=null)satus.succed();
          break;
        case BluetoothGatt.STATE_CONNECTING:
          break;
        case BluetoothGatt.STATE_DISCONNECTED:
          if (status!=0){
            if (bluetoothDevice!=null&&getH_weight!=null){
              Connect(bluetoothDevice,getH_weight,satus);
            }
          }else {
            UnRegisterReceiver();
          }

          break;
        default:
          break;
      }

    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      super.onServicesDiscovered(gatt, status);
      LogUtils.e("onServicesDiscovered：" + status + ":+status");
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
        final String date = HexUtil.hexStringToString(Bytes2HexString(value));
        String hexString = Bytes2HexString(value);
        //在这个方法里面获得蓝牙设备发送给APP的数据
        LogUtils.e("收到数据拉==========》", date + "**" + hexString);
        //020D0A54696D653A 包头 Time:    0D0A030D0A 包尾
        stringBuffer.append(hexString);
        String vlaue = stringBuffer.toString();
        int head = vlaue.indexOf("573A");
        int last = vlaue.indexOf("0D0A");
        if (head >= 0 && last >= 0) {
          String hexsting = HexUtil.hexStringToString(vlaue.substring(head, last));
          LogUtils.e("解析数据" + hexsting + "vlaue.substring(head,last)" + vlaue.substring(head, last));
          String weight = null;
          String height = null;
          if (hexsting.contains("W:") && hexsting.contains("H:")) {
            weight = hexsting.substring(hexsting.indexOf("W:") + ("W:").length(), hexsting.indexOf("H:"));
            height = hexsting.substring(hexsting.indexOf("H:") + ("H:").length(), hexsting.length());
            if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(height)) {
              if (getH_weight!=null)getH_weight.getH_Weight(Float.parseFloat(weight),Float.parseFloat(height));
            }
          }
          UnRegisterReceiver();
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

  private void RegisterReceiver(GetH_Weight getTemperature) {
    this.getH_weight = getTemperature;
    stringBuffer.delete(0, stringBuffer.length());
  }

  @Override
  public void Connect(BluetoothDevice bluetoothDevice, GetH_Weight bluetooth_satus, Bluetooth_Satus bluetoothSatus) {
    this.bluetoothDevice=bluetoothDevice;
    this.satus=bluetoothSatus;
    RegisterReceiver(bluetooth_satus);
    bluetoothGatt = bluetoothDevice.connectGatt(ContextProvider.get().getContext(), false, gattCallback);
    if (bluetoothGatt == null) UnRegisterReceiver();
    LogUtils.e(bluetoothGatt);
  }
  @Override
  public void UnRegisterReceiver() {
    stringBuffer.delete(0, stringBuffer.length());
    if (getH_weight != null) getH_weight.err();
    if (satus!=null)satus.err();
    this.bluetoothDevice=null;
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
      bluetoothGatt = null;
    }
  }
}
