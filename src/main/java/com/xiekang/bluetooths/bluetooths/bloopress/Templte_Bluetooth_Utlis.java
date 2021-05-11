package com.xiekang.bluetooths.bluetooths.bloopress;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetTemperature;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.math.BigDecimal;
import java.util.Arrays;


import static android.content.Context.BIND_AUTO_CREATE;
import static com.xiekang.bluetooths.utlis.HexUtil.bytetoString;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 爱奥乐体温E127B
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Templte_Bluetooth_Utlis implements BluetoothDriver<GetTemperature> {
  private static Templte_Bluetooth_Utlis bloodpress_bluetooth_utlis;
  private GetTemperature mbluetoothsatus;
  private BluetoothLeService mBluetoothLeService;
  private BluetoothGatt mBluetoothGatt;
  private String mDeviceAddress;
  public static String GATT_SERVICE_PRIMARYNEW1 = "0000ffb0-0000-1000-8000-00805f9b34fb";
  public static String CHARACTERISTIC_WRITEABLENEW1 = "0000ffb1-0000-1000-8000-00805f9b34fb";
  public static String CHARACTERISTIC_READABLENEW1 = "0000ffb2-0000-1000-8000-00805f9b34fb";
  /**
   * 发送的命令数组
   */
  private byte[] sendDataByte;
  private boolean isBingd;
  private Intent intent;
  private Bluetooth_Satus satus;

  private Templte_Bluetooth_Utlis() {

  }

  public static Templte_Bluetooth_Utlis getInstance() {
    if (bloodpress_bluetooth_utlis==null){
      bloodpress_bluetooth_utlis=new Templte_Bluetooth_Utlis();
    }
    return bloodpress_bluetooth_utlis;
  }


  private IntentFilter makeGattUpdateIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
    intentFilter
        .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
    intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
    return intentFilter;
  }
  /**
   * 连接蓝牙
   *
   * @param
   */
  public void Connect(BluetoothDevice bluetoothDevice, GetTemperature bluetooth_satus, Bluetooth_Satus satus) {
      LogUtils.e("连接中****");
      this.satus=satus;
      RegisterReceiver(bluetooth_satus);
      mDeviceAddress=bluetoothDevice.getAddress();
       Intent gattServiceIntent = new Intent(ContextProvider.get().getContext(), BluetoothLeService.class);
      isBingd = ContextProvider.get().getContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
  }
  private final ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName componentName,
                                   IBinder service) {
      mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
          .getService();

      if (mBluetoothLeService.connect(mDeviceAddress)) {
        mBluetoothGatt = mBluetoothLeService.getGatt();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      mBluetoothLeService = null;
    }
  };

  //
  private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
        LogUtils.e("连接成功*******");
        if (mbluetoothsatus!=null)mbluetoothsatus.succed();
        if (satus!=null)satus.succed();
        SampleGattAttributes.sendMessage(mBluetoothGatt, ZHexUtil.hexStringToBytes(getSendHex(0)));// 握手包


      } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
          .equals(action)) {
        UnRegisterReceiver();
        LogUtils.e("连接失败*******");
      } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
          .equals(action)) {

        if (mBluetoothGatt != null) {
          //	displayGattServices(mBluetoothGatt.getServices());
          // mViewBinding.btnBpSend.setEnabled(true);
          SampleGattAttributes.sendMessage(mBluetoothGatt, ZHexUtil.hexStringToBytes(getSendHex(0)));// 握手包
        }

      } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
        // 获取设备上传的数据

        byte[] notify = intent
            .getByteArrayExtra(BluetoothLeService.EXTRA_NOTIFY_DATA);
         LogUtils.e("有数据回来了"+
         notify.length);

        if (notify != null&&mbluetoothsatus!=null) {

          getDatas(notify);

        }
      }
    }
  };

  private void getDatas(byte[] data) {
    LogUtils.e("\n体温计返回(16进制)：" + Arrays.toString(ZHexUtil.setStr(ZHexUtil.bytes2HexString(data, data.length))));

    LogUtils.e("zdw", "--" + Arrays.toString(data));
    getVersion(data);
    getBleInfo(data);
    showContent(data);
  }
  private String mType = "";
  private String mSnNumber;
  boolean first = true;//只发一次信息包
  private int agreement_type;//1:1.0协议;2:2.0或3.0协议
  boolean flag = true;//只接收一次结果包
  private String getTypeStr(byte type) {
    String typeStr = "";
//    if (type == 0) {
//      typeStr = getString(R.string.apple);
//    } else if (type == 1) {
//      typeStr = getString(R.string.bioland);
//    } else if (type == 2) {
//      typeStr = getString(R.string.haier);
//    } else if (type == 3) {
//      typeStr = getString(R.string.bioland);
//    } else if (type == 4) {
//      typeStr = getString(R.string.xiaomi);
//    } else if (type == 5) {
//      typeStr = getString(R.string.daotong);
//    } else if (type == 6) {
//      typeStr = "KANWEI";
//    }
    return typeStr;
  }

  /**
   * 信息包
   * <p>
   * "00" package
   * Get the bluetooth device protocol version
   */
  private void getVersion(byte[] data) {
    if (null == data) return;
    int length = data.length;
    byte index0 = data[0];
    if (index0 != 85) return;
    byte index2 = data[2];
    if (index2 == 0) {
      first = false;
      //品牌
      mType = getTypeStr(data[4]);
    }
  }
  private void write(byte[] data_05) {
    boolean issufuel=SampleGattAttributes.sendMessage(mBluetoothGatt, data_05);
    if (issufuel){
      if (data_05[2] == 0){
        byte[] data = ZHexUtil.hexStringToBytes(getSendHex());
        LogUtils.e("\n写入数据(16进制)：" + Arrays.toString(ZHexUtil.setStr(ZHexUtil.bytes2HexString(data_05, data_05.length))));

        SampleGattAttributes.sendMessage(mBluetoothGatt, data);
      }else {
        byte[] data_00 = ZHexUtil.hexStringToBytes(getSendHex(0));
        LogUtils.e("\n写入数据(16进制)：" + Arrays.toString(ZHexUtil.setStr(ZHexUtil.bytes2HexString(data_00, data_00.length))));
        SampleGattAttributes.sendMessage(mBluetoothGatt, data_00);
      }
    }
  }
  /**
   * "1.0"协议的"05"应答包
   *
   * @return
   */
  public static String getSendHex() {
    //5AH	0BH	05H	0EH	0BH	08H	0CH	12H	A9H	00H	00H
    String time = ZTimeTool.getCurrentDateTime("yy-MM-dd-HH-mm-ss");
    String[] arrTime = time.split("-");

    int index0 = 90;//5A->90起始码
    int index1 = 11;//0B->11包长
    int index2 = 5;//05 包类别
    int index3 = Integer.parseInt(arrTime[0]);//Year
    int index4 = Integer.parseInt(arrTime[1]);//Month
    int index5 = Integer.parseInt(arrTime[2]);//Day
    int index6 = Integer.parseInt(arrTime[3]);//Hour
    int index7 = Integer.parseInt(arrTime[4]);//Minute
    int index8 = index0 + index1 + index2 + index3 + index4 + index5 + index6 + index7;//8,9,10为Check_sum
    int index9 = 0;//
    int index10 = 0;//

    byte[] data = new byte[]{(byte) index0, (byte) index1, (byte) index2, (byte) index3,
        (byte) index4, (byte) index5, (byte) index6, (byte) index7, (byte) index8, (byte) index9, (byte) index10};

    String hexSend = ZHexUtil.encodeHexStr(data);
    return hexSend.toUpperCase();
  }
  /**
   * @param data "3.0" protocol "00" package length:18
   * @param data "2.0" protocol "00" package length:15
   */
  private void getBleInfo(byte[] data) {
    if (null == data) return;
    if (data.length != 18 && data.length != 15) return;
    byte index0 = data[0];
    if (index0 != 85) return;
    byte index2 = data[2];
    if (index2 != 0) return;
    //sn
    byte[] snArray = new byte[9];
    System.arraycopy(data, 8, snArray, 0, 9);
    mSnNumber = bytetoString(snArray);
    byte index4 = data[4];
    //品牌
    mType = getTypeStr(index4);

    String str = String.format("\n蓝牙名称：%s\nSN号:%s\n客户码:%s",
        mDeviceAddress, mSnNumber, mType);
    LogUtils.e(str);
  }
  /**
   * get measure result
   * "2.0" above protocol "03" package length:12
   * "1.0" protocol "03" package length:14
   *
   * @param data received package data
   */
  private void showContent(byte[] data) {
    if (null == data) return;
    byte index0 = data[0];
    if (index0 != 85) return;
    byte index2 = data[2];
    if (index2 == 3) {//Device upload results
      byte index3 = data[3];//Year
      byte index4 = data[4];//Month
      byte index5 = data[5];//Day
      byte index6 = data[6];//Hour
      byte index7 = data[7];//Minute
      byte[] valueArr = {data[10], data[9]};
      //10+9 Convert to decimal,And then  divide by 100,get the temperature
      String strValue = ZHexUtil.encodeHexStr(valueArr);
      int value = Integer.valueOf(strValue, 16);
      Log.e("zdw", "value --" + value);
      //跟体温计结果显示保持一致：丢弃小数点后第二位
      String valueStr =formatTo1ROUNDDOWN((double) value / 100);
//            if (!TextUtils.isEmpty(mSnNumber)) {
//                String str = String.format("蓝牙名称：%s\nSN号:%s ; 客户码:%s\n日期:20%s-%s-%s %s:%s\n温度值:%s℃",
//                        mDevice.getName(),mSnNumber, mType
//                        , index3, index4, index5, index6, index7, valueStr);
      String str = String.format("\n日期:%s   温度值:%s℃", ZTimeTool.getCurrentDateTime("yyyy-MM-dd HH:mm:ss"), valueStr);
      LogUtils.e(str+"valueStr"+valueStr);
      if (mbluetoothsatus!=null)mbluetoothsatus.getTempter(valueStr);
      UnRegisterReceiver();
//            } else {
//                String str = String.format("蓝牙名称：%s\n客户码:%s ; \n日期:20%s-%s-%s %s:%s,\n温度值:%s℃",mDevice.getName(), mType
//                        , index3, index4, index5, index6, index7, valueStr);
//                tvContent.setText(str);
//            }
    } else if (index2 == 5) {
      LogUtils.e("接收完成");
    }
  }
  private String formatTo1(double f) {
    BigDecimal bg = new BigDecimal(f);
    return bg.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
  }
  private String formatTo1ROUNDDOWN(double f) {
    BigDecimal bg = new BigDecimal(f);
    return bg.setScale(1, BigDecimal.ROUND_DOWN).toString();
  }


  /**
   * 向设备发送命令
   *
   * @param ；包长度
   * @param ’包类别
   */
  private void sendDataByte(final byte leng, final byte commandType) {
      sendDataByte=ZHexUtil.hexStringToBytes(ZDataUtil.getSendHex(1));
    //LogUtils.e( "535" + mBluetoothGatt);
    SampleGattAttributes.sendMessage(mBluetoothGatt, sendDataByte);
    LogUtils.e( "发送的命令" + HexUtil.encodeHexStr(sendDataByte)+"*****************");

  }

  /**
   * 2.0以上协议的发包
   * Write data rule
   *
   * @param sendPack Write packet category
   * @return Write packet String data
   */
  public static String getSendHex(int sendPack) {
    String time = ZTimeTool.getCurrentDateTime("yy-MM-dd-HH-mm-ss");
    String[] arrTime = time.split("-");
    if (arrTime.length == 6) {
      int index0 = 90;
      int index1 = 10;
      int index2 = sendPack;//packet category
      int index3 = Integer.parseInt(arrTime[0]);//Year
      int index4 = Integer.parseInt(arrTime[1]);//Month
      int index5 = Integer.parseInt(arrTime[2]);//Day
      int index6 = Integer.parseInt(arrTime[3]);//Hour
      int index7 = Integer.parseInt(arrTime[4]);//Minute
      int index8 = Integer.parseInt(arrTime[5]);//Second
      int index9 = index0 + index1 + index2 + index3 + index4 + index5 + index6 + index7 + index8 + 2;
      if (index9 > 255) {
        index9 = index9 % 255;
      }
      byte[] data = new byte[]{(byte) index0, (byte) index1, (byte) index2, (byte) index3,
          (byte) index4, (byte) index5, (byte) index6, (byte) index7, (byte) index8};

      String hexSend = ZHexUtil.encodeHexStr(data);
      String hexIndex9 = ZHexUtil.encodeHexStr(new byte[]{(byte) index9});
      int len = hexIndex9.length();
      String i9 = hexIndex9.substring(len - 2, len);
      return String.format("%s%s", hexSend, i9).toUpperCase();
    }
    return "";
  }

  private void RegisterReceiver(GetTemperature mbluetoothsatus) {
      this.mbluetoothsatus = mbluetoothsatus;
    intent = ContextProvider.get().getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
  }

  public void UnRegisterReceiver() {
    if (mbluetoothsatus!=null)mbluetoothsatus.err();
    if (satus!=null)satus.err();
  if (isBingd) ContextProvider.get().getContext().unbindService(mServiceConnection);
    if (mBluetoothLeService!=null)mBluetoothLeService.close();
   if (intent!=null)ContextProvider.get().getContext().unregisterReceiver(mGattUpdateReceiver);
    isBingd=false;
    intent=null;
    this.mbluetoothsatus = null;
    if (mBluetoothGatt != null) {
      mBluetoothGatt.disconnect();
      mBluetoothGatt.close();
      mBluetoothGatt=null;
    }
  }
}
