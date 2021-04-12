package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import com.xiekang.bluetooths.interfaces.Getbloodsuar;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.xiekang.bluetooths.utlis.DateUtil.getDecimal;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 艾康血糖
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BloodsugarUtlis {
  private static BloodsugarUtlis bloodpress_bluetooth_utlis;
  private String order = "2644312031200632373838340D";
  private BluetoothGatt bluetoothGatt;
  //连接状态
  private boolean isconnect = false;
  private BluetoothGattCharacteristic controlCharacteristicl, notifyCharacteristic, batteryCharacteristic;
  private Getbloodsuar geturidate;
  public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
  public static String Data_UUID = "00004a5b-0000-1000-8000-00805f9b34fb";

  public static BloodsugarUtlis getInstance() {
    if (bloodpress_bluetooth_utlis == null) {
      bloodpress_bluetooth_utlis = new BloodsugarUtlis();
    }
    return bloodpress_bluetooth_utlis;
  }

  /**
   * 连接蓝牙
   *
   * @param
   */
  public void connect(BluetoothDevice bluetoothDevice, Getbloodsuar bluetooth_satus) {
   RegisterReceiver(bluetooth_satus);
    bluetoothGatt = bluetoothDevice.connectGatt(ContextProvider.get().getContext(), false, gattCallback);
    LogUtils.e("血糖连接"+bluetoothGatt.toString());
    if (bluetoothGatt==null)UnRegisterReceiver();
  }

  /**
   * 请求数据
   */
  private void send() {
    if (controlCharacteristicl != null) {

      controlCharacteristicl.setValue(HexUtil.hexStringToBytes(order));
      controlCharacteristicl.setWriteType(controlCharacteristicl.getWriteType());
      boolean a = bluetoothGatt.writeCharacteristic(controlCharacteristicl);
      LogUtils.e("发送数据：" + a);

    }
  }

  StringBuffer buffer = new StringBuffer();
  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
      super.onConnectionStateChange(gatt, status, newState);
      switch (newState) {
        case BluetoothGatt.STATE_CONNECTED:
          //当连接成果以后，开启这个服务，就可以通信了
          if (geturidate != null) geturidate.succed();
              bluetoothGatt.discoverServices();
          break;
        case BluetoothGatt.STATE_CONNECTING:
          LogUtils.e("连接中");
          break;
        case BluetoothGatt.STATE_DISCONNECTED:
          UnRegisterReceiver();
          break;
        default:
          break;
      }
      LogUtils.e("连接状态" + newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      super.onServicesDiscovered(gatt, status);
      if (status == BluetoothGatt.GATT_SUCCESS) {
        List<BluetoothGattService> services = bluetoothGatt.getServices();
        for (BluetoothGattService bluetoothGattService : services) {
          List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
          for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
            //LogUtils.e("====================>UUID=" + bluetoothGattCharacteristic.getUuid());
            //读和写是同一个UUID的情况
            if (bluetoothGattCharacteristic.getUuid().toString().equals(Data_UUID)) {
              //写数据的特征值
              controlCharacteristicl = bluetoothGattCharacteristic;//读和写的特征值所赋的值是一样的
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
      LogUtils.e("onCharacteristicChanged收到数据拉==========》", characteristic.getValue().toString());
      if (characteristic.getUuid().toString().equals(notifyCharacteristic.getUuid().toString())) {
        try {
          final byte[] value = characteristic.getValue();
          String date = HexUtil.Bytes2HexString(value);
          //在这个方法里面获得蓝牙设备发送给APP的数据
          LogUtils.e("收到数据拉==========》", date);
          if (date.contains("26445A20") || isture) {
            isture = true;
            buffer.append(date);
            String DATE = buffer.toString();
            String s1 = "1E";

            if (appearNumber(DATE, s1) >= 2) {
              String dates = buffer.toString();
              String[] split = dates.split("1E");
              if (split.length >= 3) {
                String date4 = split[1];
                String[] split1 = date4.split("20");
                if (split1.length >= 3) {
                  LogUtils.e("血糖值" + split1[1]);
                  if (suagr == 0)
                    suagr = (float) (((double) Integer.parseInt(HexUtil.hexStringToString(split1[1])) / 18) + 0.05);
                }
              }

            }

            if (DATE.contains("0D")) {
              if (suagr > 0) {
                send();
                LogUtils.e("血糖值" + suagr);
                buffer.delete(0, buffer.length());
                geturidate.getbloodsugar(Float.parseFloat(getDecimal(suagr)));
                UnRegisterReceiver();
              }
            }
          }
        } catch (Exception e) {
          LogUtils.e("Exception" + e.toString());
        }

      }
    }

  };

  /**
   * 获取指定字符串出现的次数
   *
   * @param srcText  源字符串
   * @param findText 要查找的字符串
   * @return
   */
  private static int appearNumber(String srcText, String findText) {
    int count = 0;
    Pattern p = Pattern.compile(findText);
    Matcher m = p.matcher(srcText);
    while (m.find()) {
      count++;
    }
    LogUtils.e("appearNumber:" + count);
    return count;
  }

  /**
   *      * 将数字字符串转化为二进制byte数组
   *      * @param hex
   *      * @return
   *     
   */
  private static byte[] hexStringToByte(String hex) {
    int len = (hex.length() / 2);
    byte[] result = new byte[len];
    char[] achar = hex.toCharArray();
    for (int i = 0; i < len; i++) {
      int pos = i * 2;
      result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
    }
    return result;
  }

  /**
   *      * 将二进制数组转化为16进制字符串
   *      * @param src
   *      * @return
   *      
   */
  private static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder();
    if (src == null || src.length <= 0) {
      return null;
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      //stringBuilder.append(i + ":");//序号 2个数字为1组
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

  private static byte toByte(char c) {
    byte b = (byte) "0123456789ABCDEF".indexOf(c);
    return b;
  }

  //打开蓝牙接收数据的通道
  private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
    if (bluetoothGatt == null || characteristic == null)
      return false;
    if (!bluetoothGatt.setCharacteristicNotification(characteristic, enable))
      return false;
    //这里的UUID 是不变的 不要改动
    BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
    if (clientConfig == null)
      return false;
    if (enable) {
      clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    } else {
      clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    }
    return bluetoothGatt.writeDescriptor(clientConfig);
  }

  //接收到数据开始的标志
  boolean isture = false;
  float suagr = 0f;

  private void RegisterReceiver(Getbloodsuar getbloodsuar) {
    this.geturidate = getbloodsuar;
    isconnect = false;
    isture = false;
    suagr = 0f;
    buffer.delete(0, buffer.length());
  }

  public void UnRegisterReceiver() {
    if (geturidate != null) geturidate.err();
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
      bluetoothGatt=null;
    }
    this.geturidate = null;
    isture = false;
    suagr = 0f;
    isconnect = false;
    buffer.delete(0, buffer.length());
  }

}
