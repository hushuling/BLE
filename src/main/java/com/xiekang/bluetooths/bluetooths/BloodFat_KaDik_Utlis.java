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
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 卡迪克血脂
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BloodFat_KaDik_Utlis {
  private static BloodFat_KaDik_Utlis bloodFat_kaDik_utlis;
  private BluetoothGatt bluetoothGatt;
  private List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
  //搜索的状态
  private boolean isSearch = false;
  private BluetoothGattCharacteristic controlCharacteristicl, notifyCharacteristic, batteryCharacteristic;
  private static final int MSG_BLUETOOTH_DISCOVERYED = 77;
  private GetBloodfat getBloodfat;
  private StringBuilder mOutputInfo = new StringBuilder();
  //解析
  public String GATT_SERVICE_PRIMARY = "0000ffe4-0000-1000-8000-00805f9b34fb";
  //非解析
  public String CHARACTERISTIC_READABLE = "0000fff4-0000-1000-8000-00805f9b34fb";
  public static BloodFat_KaDik_Utlis getInstance() {
    if (bloodFat_kaDik_utlis == null) {
      bloodFat_kaDik_utlis = new BloodFat_KaDik_Utlis();
    }
    return bloodFat_kaDik_utlis;
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

  private final byte[] hex = "0123456789ABCDEF".getBytes();

  // 从字节数组到十六进制字符串转
  private String Bytes2HexString(byte[] b) {
    byte[] buff = new byte[2 * b.length];
    for (int i = 0; i < b.length; i++) {
      buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
      buff[2 * i + 1] = hex[b[i] & 0x0f];
    }
    return new String(buff);
  }

  //请求数据格式处理
  private byte[] hexStringToBytes(String hexString) {
    if (hexString == null || hexString.equals("")) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  private byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }

  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
      super.onConnectionStateChange(gatt, status, newState);
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
            if (bluetoothGattCharacteristic.getUuid().toString().equals(CHARACTERISTIC_READABLE)) {
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
    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);
      if (characteristic == null || characteristic.getValue() == null) {
        return;
      }
      LogUtils.e(this.getClass().getName(), "收到的数据" + characteristic.getValue());
//      System.out.println("HexUtil.encodeHexStr(characteristic.getValue())" + HexUtil.encodeHexStr(characteristic.getValue()));
//      mOutputInfo.append(HexUtil.hexStringToString(HexUtil.encodeHexStr(characteristic.getValue())));
//      LogUtils.e(this.getClass().getName(), "解析的数据" + HexUtil.hexStringToString(HexUtil.encodeHexStr(characteristic.getValue())));
//      if (HexUtil.encodeHexStr(characteristic.getValue()).equals("00000000") || HexUtil.encodeHexStr(characteristic.getValue()).equals("0d0a0d0a0d0a")) {
//            ResolverDate(mOutputInfo.toString().trim());
//      }
      mOutputInfo.append(HexUtil.encodeHexStr(characteristic.getValue()));
      String date = mOutputInfo.toString();
      if (date.contains("0a4e0a")) {//空格N空格
        int nNum = totalNum(date, "0a0a4e");//空格空格N
        //                Log.e("结束标签个数",  "结束标签个数--="+nNum);
        if (nNum == 2) { //文档中刚好有2个
          String content = HexUtil.hexStringToString(date);
          LogUtils.e("血脂获取成功：", content);
          ResolverDate(content);
          mOutputInfo.delete(0, mOutputInfo.length());
        }

      }


    }

  };
  /**
   * 查找某个字符或字符段在字符串中出现的次数
   *
   * @param str  待被查找的字符串
   * @param need 待查找的字符
   * @return 出现的次数
   */
  public int totalNum(String str, String need) {
    if (str == null || need == null) {
      return 0;
    }
    char[] strArr = str.toCharArray();
    int needLen = need.length();
    int strLen = str.length();
    //如果待查找的字符串长度小于需要查找的字符串长度，则一定不存在
    if (needLen > strLen) {
      return 0;
    }
    int count = 0;
    boolean flag = true;
    for (int i = 0; i < strArr.length; i++) {
      StringBuilder builder = new StringBuilder();
      for (int j = 0; j < needLen; j++) {
        //判断当前在char数组中的位置加上需要查找的字符串的长度是否大于char数组的长度，如果是则跳出循环，方式数组越界
        if ((i + j) >= strLen) {
          flag = false;
          break;
        }
        builder.append(strArr[i + j]);
      }
      if (!flag) {
        break;
      }
      if (builder.toString().equals(need)) {
        count++;
      }
    }
    return count;
  }
  /**
   * 解析数据
   *
   * @param @mOutputInfo
   */
  private void ResolverDate(String outputInfo) {
    LogUtils.e(this.getClass().getCanonicalName(), outputInfo + "完成数据");
    String chol = "";
    String HDL = "";
    String TRIG = "";
    String LDL = "";
    boolean iserr = false;
    try {
      chol = outputInfo.substring(outputInfo.indexOf("CHOL"), outputInfo.indexOf("HDL CHOL"));
      HDL = outputInfo.substring(outputInfo.indexOf("HDL CHOL"), outputInfo.indexOf("TRIG"));
      TRIG = outputInfo.substring(outputInfo.indexOf("TRIG"), outputInfo.indexOf("CALC LDL"));
      LDL = outputInfo.substring(outputInfo.indexOf("CALC LDL"), outputInfo.indexOf("TC/HDL"));

    } catch (Exception e) {
      iserr = true;
    }
    if (iserr) {
      try {
        chol = outputInfo.substring(outputInfo.indexOf("CHOL"), outputInfo.indexOf("HDLCHOL"));
        HDL = outputInfo.substring(outputInfo.indexOf("HDLCHOL"), outputInfo.indexOf("TRIG"));
        TRIG = outputInfo.substring(outputInfo.indexOf("TRIG"), outputInfo.indexOf("CALCLDL"));
        LDL = outputInfo.substring(outputInfo.indexOf("CALCLDL"), outputInfo.indexOf("TC/HDL"));
      } catch (Exception e) {

      }
    }
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
    String chlo = "0";
    if (chol.contains("m")) {
      chlo = (chol.substring(chol.indexOf(":") + 1, chol.indexOf("m")));
    }
    String trig = "0";
    if (TRIG.contains("m")) {
      trig = (TRIG.substring(TRIG.indexOf(":") + 1, TRIG.indexOf("m")));
    }
    String hdl = "0";
    if (HDL.contains("m")) {
      hdl = (HDL.substring(HDL.indexOf(":") + 1, HDL.indexOf("m")));
    }
    String ldl = "0";
    if (LDL.contains("m")) {
      ldl = (LDL.substring(LDL.indexOf(":") + 1, LDL.indexOf("m")));
    }
    if (getBloodfat!=null)getBloodfat.getbloodfat(Float.valueOf(chlo),
        Float.valueOf(trig),
        Float.valueOf(hdl),
        Float.valueOf(ldl));
    UnRegisterReceiver();
  }

  //打开蓝牙接收数据的通道
  private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
    if (bluetoothGatt == null || characteristic == null)
      return false;
    if (!bluetoothGatt.setCharacteristicNotification(characteristic, enable))
      return false;
    //这里的UUID 是不变的 不要改动
    BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_READABLE));
    if (clientConfig == null)
      return false;
    if (enable) {
      clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    } else {
      clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    }
    return bluetoothGatt.writeDescriptor(clientConfig);
  }

  private void   RegisterReceiver(GetBloodfat getBloodfat) {
    this.getBloodfat=getBloodfat;
  }

  public void UnRegisterReceiver() {
    if (getBloodfat!=null)getBloodfat.err();
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
      bluetoothGatt=null;
    }
  }
}
