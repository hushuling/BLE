package com.xiekang.bluetooths.bluetooths.bloopress;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.xiekang.bluetooths.interfaces.GetBloodfat;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import net.litcare.lplibrary.bf.BFException;
import net.litcare.lplibrary.bf.BFRecordHelper;
import net.litcare.lplibrary.bf.BFType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 爱奥乐血压
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Bloodpress_NewBluetooth_Utlis {
  private static Bloodpress_NewBluetooth_Utlis bloodFatLePuBluetoothUtlis;
  private BluetoothGatt bluetoothGatt;
  private BluetoothGattCharacteristic controlCharacteristicl, notifyCharacteristic, batteryCharacteristic;
  private Bloodpress_intenface getBloodfat;
  public static String GATT_SERVICE_PRIMARY = "00001001-0000-1000-8000-00805f9b34fb";
  public static String CHARACTERISTIC_READABLE = "00001002-0000-1000-8000-00805f9b34fb";
  public static String CLIENT_CHARACTERISTIC_CONFIG ="00002902-0000-1000-8000-00805f9b34fb";
  private boolean getResult;

  private Bloodpress_NewBluetooth_Utlis() {
  }
  public static Bloodpress_NewBluetooth_Utlis getInstance() {
    if (bloodFatLePuBluetoothUtlis == null) {
      bloodFatLePuBluetoothUtlis = new Bloodpress_NewBluetooth_Utlis();
    }
    return bloodFatLePuBluetoothUtlis;
  }
  /**
   * 连接蓝牙
   *
   * @param
   */
  public void connect(BluetoothDevice bluetoothDevice, Bloodpress_intenface bluetooth_satus) {
    RegisterReceiver(bluetooth_satus);
    bluetoothGatt = bluetoothDevice.connectGatt(ContextProvider.get().getContext(), false, gattCallback);
    if (bluetoothGatt == null) UnRegisterReceiver();
    LogUtils.e(bluetoothGatt);
  }

  private Handler mHandler = new Handler() {
    //延时发送各种命令
    @Override
    public void handleMessage(android.os.Message msg) {
      switch (msg.what) {
        case 0:
          LogUtils.e("开始启动****");
          send();
          break;

        default:
          break;
      }

    }

  };



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

            //读和写是同一个UUID的情况
            if (bluetoothGattCharacteristic.getUuid().toString().equals(GATT_SERVICE_PRIMARY)) {
              //写数据的特征值
              controlCharacteristicl = bluetoothGattCharacteristic;//读和写的特征值所赋的值是一样的
              mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  send();
                }
              }, 3000);
            }
            if (bluetoothGattCharacteristic.getUuid().toString().equals(CHARACTERISTIC_READABLE)) {
              //读数据的特征值
              notifyCharacteristic = bluetoothGattCharacteristic;//读和写的特征值所赋的值是一样的
             LogUtils.e("enableNotification"+ enableNotification(true, notifyCharacteristic)); ;
            }
          }
        }
      }
    }


    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
       byte[] value = characteristic.getValue();
      LogUtils.e("onCharacteristicChanged()"+ HexUtil.encodeHexStr(value));
      getValueData3(value);
    }


  };

  /**
   * 得到过程包显示出来
   */
  private void setPreValue(byte[] data) {
    byte[] valueArr = {data[6], data[5]};
    try {
      String strValue = String.valueOf(Integer.valueOf(ZHexUtil.encodeHexStr(valueArr), 16));//血压 6+5 在转换成10进制
      if (getBloodfat!=null)getBloodfat.current(Integer.parseInt(strValue));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  private void getValueData3(byte[] data) {
    if (null == data) return;
    if (data.length < 3) return;
    byte index2 = data[2];
    //[85, 5, -18, 1, 75]
    switch (index2) {
      case 2:
        if (data.length != 8) break;
        setPreValue(data);
        break;
      case 3:
        if (data.length != 14) break;
        try {
          byte index3 = data[3];//Year
          byte index4 = data[4];//Month
          byte index5 = data[5];//Day
          byte index6 = data[6];//Hour
          byte index7 = data[7];//Minute
          byte index8 = data[8];//00和01为当前结果，02以上为历史记忆
          byte[] valueArr = {data[10], data[9]};
          String date = String.format("20%s-%s-%s %s:%s："
              , String.format("%02d", (int) index3), String.format("%02d", (int) index4), String.format("%02d", (int) index5),
              String.format("%02d", (int) index6), String.format("%02d", (int) index7));
          String strValue = ZHexUtil.encodeHexStr(valueArr);//血糖 10+9 在转换成10进制
        String  mHighValue = String.valueOf(Integer.valueOf(strValue, 16));//高压
          String  mmLowValue = String.valueOf(Integer.valueOf(ZHexUtil.encodeHexStr(new byte[]{data[11]}), 16));//低压
          String  mHeratValue = String.valueOf(Integer.valueOf(ZHexUtil.encodeHexStr(new byte[]{data[12]}), 16));//低压

          String result_temp = date + String.format("收缩压:%s 舒张压:%s 心率:%s", mHighValue, mmLowValue, mHeratValue);
            if (!getResult) {
              LogUtils.e("当前结果：" + result_temp);
              getResult = true;
              if (getBloodfat!=null)getBloodfat.getDate(new Bloodpress_Bluetooth_Utlis.Info(mHighValue,mmLowValue,mHeratValue));
              UnRegisterReceiver();
            }
        } catch (Exception e) {
          Log.e("zdw007", e.toString());
        }
        break;
      case -18:
        if (data.length != 5) break;
        Toast.makeText(ContextProvider.get().getContext(),"血压测量出错，测量中请保持坐姿、手势平稳，勿动", Toast.LENGTH_SHORT).show();
        if (getBloodfat!=null)getBloodfat.err();
        UnRegisterReceiver();
        break;
    }
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
      clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    return bluetoothGatt.writeDescriptor(clientConfig);
  }
  /**
   * 请求数据
   */
  public void send() {
    if (controlCharacteristicl != null) {
          byte[] data_00 =  ZHexUtil.hexStringToBytes(ZDataUtil.getSendHex(1));;
          LogUtils.e("写入数据：" + Arrays.toString(ZHexUtil.setStr(ZHexUtil.bytes2HexString(data_00, data_00.length))));
          controlCharacteristicl.setValue(data_00);
          if (bluetoothGatt!=null)  bluetoothGatt.writeCharacteristic(controlCharacteristicl);
        }
  }


  private void   RegisterReceiver(Bloodpress_intenface getBloodfat) {
    this.getBloodfat=getBloodfat;
  }

  public void UnRegisterReceiver() {
    if (getBloodfat!=null)getBloodfat.err();
    getBloodfat=null;
    if (bluetoothGatt != null) {
      bluetoothGatt.disconnect();
      bluetoothGatt.close();
      bluetoothGatt=null;
    }
  }
}
