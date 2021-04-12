package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

import com.xiekang.bluetooths.interfaces.GetBloodfat;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import static com.xiekang.bluetooths.utlis.HexUtil.printHexString;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 艾科血脂蓝牙
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class IGateUtis {
  private static IGateUtis iGateUtis;
  private BluetoothGatt bluetoothGatt;
  private GetBloodfat getBloodfat;
  private boolean flag = false;
  //连接状态
  private boolean isconnect = false;
  private String address;
  private BluetoothGattCharacteristic controlCharacteristicl, notifyCharacteristic, batteryCharacteristic;

  public static IGateUtis getInstance() {
    if (iGateUtis == null) {
      iGateUtis = new IGateUtis();
    }
    return iGateUtis;
  }

  private IGateUtis() {
  }

  private void RegisterReceiver(GetBloodfat getBloodfat) {
    if (getBloodfat!=null)this.getBloodfat = getBloodfat;
    isconnect=false;
    flag=false;
    address=null;
    hexdate.delete(0, hexdate.length());
    date.delete(0, date.length());
  }

  public void UnRegisterReceiver() {
    if (getBloodfat != null) getBloodfat.err();
    getBloodfat = null;
    hexdate.delete(0, hexdate.length());
    date.delete(0, date.length());
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
    }
    address=null;
    flag=false;
    isconnect=false;
  }

  /**
   * 连接
   *
   * @param
   */
  public void connect(final BluetoothDevice bluetoothDevice, GetBloodfat bluetooth_satus) {
    RegisterReceiver(bluetooth_satus);
    address=bluetoothDevice.getAddress();
    if (flag) {
      bluetoothGatt.disconnect();
    } else {
      bluetoothGatt = bluetoothDevice.connectGatt(ContextProvider.get().getContext(), false, gattCallback);
    }

  }


  StringBuffer date = new StringBuffer();
  StringBuffer hexdate = new StringBuffer();
  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
      super.onConnectionStateChange(gatt, status, newState);
      //更新UI必须用在主线程
          switch (newState) {
            case BluetoothGatt.STATE_CONNECTED:
              //当连接成果以后，开启这个服务，就可以通信了
              flag = true;
              bluetoothGatt.discoverServices();
                    getBloodfat.succed();
              break;
            case BluetoothGatt.STATE_CONNECTING:
              LogUtils.e("链接中****");
              break;
            case BluetoothGatt.STATE_DISCONNECTED:
              bluetoothGatt.close();
              if (flag) {
                isconnect = false;
                getBloodfat.err();
              } else {
                if (status != 0 && !TextUtils.isEmpty(address)) {
                  LogUtils.e("连接出现异常重新连接******" + newState);
                  connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address),null);
                }
              }
              flag = false;
              break;
            default:
              break;
          }
          LogUtils.e("连接状态newState：" + newState + "status:" + status);
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
            if (bluetoothGattCharacteristic.getUuid().toString().equals("81eb77bd-89b8-4494-8a09-7f83d986ddc7")) {
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
      final byte[] data = characteristic.getValue();
      if (characteristic == null || characteristic.getValue() == null) {
        return;
      }
      try {
        //bytedate.append();
        final String tmpValue = new String(data, "UTF-8");
        final String hexstring = printHexString(data);
        LogUtils.e("byte*****" + data + "******1");
        LogUtils.e("HexString[***" + hexstring + "***2");
        LogUtils.e("RX data:*****" + tmpValue + "******3");
        date.append(tmpValue);
        hexdate.append(hexstring);
        if (hexdate.length() >= 12) {
          if (hexdate.toString().substring(hexdate.length() - 12, hexdate.length()).equals("0D0A0D0A0D0A")) {
            ResolverDate(date);
          }

        }

      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        LogUtils.e("UnsupportedEncodingException: " + e.toString() + "///");
      }

    }

  };

  /**
   * 解析数据
   *
   * @param @mOutputInfo
   */
  private void ResolverDate(StringBuffer c) {
    LogUtils.e(c.toString() + "完成数据");
    String chol = c.substring(c.toString().indexOf("CHOL:"), c.toString().indexOf("HDL:"));
    String HDL = c.substring(c.toString().indexOf("HDL:"), c.toString().indexOf("TRIG:"));
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
    String TRIG = c.substring(c.toString().indexOf("TRIG:"), c.toString().indexOf("CHOL/HDL:"));
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
    String LDL;
    if (c.toString().indexOf("CHD") != -1) {
      String CHD = c.substring(c.toString().indexOf("CHD"), c.toString().length());
      LDL = c.substring(c.toString().indexOf("LDL:"), c.toString().indexOf("CHD"));
      StringBuffer stringBuffers = new StringBuffer();
      if (LDL.contains(">")) {
        String[] ldl = LDL.split(">");
        for (int i = 0; i < ldl.length; i++) {
          stringBuffers.append(ldl[i]);
        }
        LDL = stringBuffers.toString();
      } else if (LDL.contains("<")) {
        String[] ldl = LDL.split("<");
        for (int i = 0; i < ldl.length; i++) {
          stringBuffers.append(ldl[i]);
        }
        LDL = stringBuffers.toString();
      }
    } else {
      LDL = c.substring(c.toString().indexOf("LDL:"), c.toString().length());
      StringBuffer stringBuffers = new StringBuffer();
      if (LDL.contains(">")) {
        String[] ldl = LDL.split(">");
        for (int i = 0; i < ldl.length; i++) {
          stringBuffers.append(ldl[i]);
        }
        LDL = stringBuffers.toString();
      } else if (LDL.contains("<")) {
        String[] ldl = LDL.split("<");
        for (int i = 0; i < ldl.length; i++) {
          stringBuffers.append(ldl[i]);
        }
        LDL = stringBuffers.toString();
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

    if (ldl.contains("<")) {
      ldl = ldl.replace("<", "");
    }
    getBloodfat.getbloodfat(Float.valueOf(chlo), Float.valueOf(trig), Float.valueOf(hdl), Float.valueOf(ldl));
    UnRegisterReceiver();
  }

  //打开蓝牙接收数据的通道
  private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
    if (bluetoothGatt == null || characteristic == null)
      return false;
    if (!bluetoothGatt.setCharacteristicNotification(characteristic, enable))
      return false;
    //这里的UUID 是不变的 不要改动
    BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
    if (clientConfig == null)
      return false;
    if (enable) {
      clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    } else {
      clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
    }
    return bluetoothGatt.writeDescriptor(clientConfig);
  }
}
