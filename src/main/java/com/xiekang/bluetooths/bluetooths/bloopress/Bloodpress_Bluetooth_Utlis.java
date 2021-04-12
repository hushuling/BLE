package com.xiekang.bluetooths.bluetooths.bloopress;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 血压 （爱奥乐A66B）
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Bloodpress_Bluetooth_Utlis {
  private static Bloodpress_Bluetooth_Utlis bloodpress_bluetooth_utlis;

  private Bloodpress_intenface mbluetoothsatus;
  private BluetoothLeService mBluetoothLeService;
  private BluetoothGatt mBluetoothGatt;
  private String mDeviceAddress;
  ArrayList<Byte> dataPackage = new ArrayList<Byte>();
  private byte deviceType = BPM;
  private List<String> bloodsugar=new ArrayList<>();
  /**
   * 血压
   */
  private final static int BPM = 1;
  /**
   * 血糖
   */
  private final static int BGM = 2;


  public final static byte INFORMATION_PACKE = 0x00;
  public final static byte START_PACKE = 0x01;
  public final static byte RESULT_PACKE = 0x03;
  public final static byte ACK_PACKE = 0x05;
  public final static byte HANDSHAKE_PACKET = 0x06;
  /**
   * 发送的命令数组
   */
  private byte[] sendDataByte;
  private byte[] cmdData = new byte[100];
  private int save_pointer;
  private int get_pointer;
  private int k;
  private int times;
  private int readbyes; // 收到的字节数；
  private int CommandType = 0;
  private Commands commands;
  private boolean isBingd;
  private Intent intent;



  private Bloodpress_Bluetooth_Utlis() {
    commands = new Commands();
  }

  public static Bloodpress_Bluetooth_Utlis getInstance() {
    if (bloodpress_bluetooth_utlis==null){
      bloodpress_bluetooth_utlis=new Bloodpress_Bluetooth_Utlis();
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
  public void connect(BluetoothDevice bluetoothDevice, Bloodpress_intenface bluetooth_satus) {
      LogUtils.e("连接中****");
      RegisterReceiver(bluetooth_satus);
      mDeviceAddress=bluetoothDevice.getAddress();
      Intent gattServiceIntent = new Intent(ContextProvider.get().getContext(), BluetoothLeService.class);
     isBingd = ContextProvider.get().getContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
  }
  // Code to manage Service lifecycle.
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
        CommandType = HANDSHAKE_PACKET;
        mHandler.postDelayed(mRunnable, 1000);// 连接后延时1秒发“0xOB,0xOA”


      } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
          .equals(action)) {
        UnRegisterReceiver();

        LogUtils.e("连接失败*******");
      } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
          .equals(action)) {

        if (mBluetoothGatt != null) {
          //	displayGattServices(mBluetoothGatt.getServices());
          // mViewBinding.btnBpSend.setEnabled(true);
          mHandler.sendEmptyMessageDelayed(222,500);
        }

      } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
        // 获取设备上传的数据

        byte[] notify = intent
            .getByteArrayExtra(BluetoothLeService.EXTRA_NOTIFY_DATA);
         LogUtils.e("有数据回来了"+
         notify.length);

        if (notify != null&&mbluetoothsatus!=null) {

          getData(notify);

        }
      }
    }
  };
  /**
   * 校验命令包
   */
  private void checkDataPackage(byte[] data) {
    for (int i = 0; i < data.length; i++) {
      dataPackage.add(data[i]);
    }

  }

  private void getData(byte notify[]) {
    // -------------------------------------------------------
    //

    for (int i = 0; i < notify.length;) { // 保存接收的数据
      cmdData[save_pointer++] = notify[i++];
      readbyes++;
      if (save_pointer >= cmdData.length)
        save_pointer = 0;
    }

    boolean bageok = false;
    while (save_pointer != get_pointer) { // 指向包头
      if (cmdData[get_pointer] == 0x55) {
        int t = cmdData[(get_pointer + 1) % cmdData.length];
        if (t > 4 && readbyes >= t) {// 够包的情况下
          bageok = true;// 正规要验证CKS
          break;
        } else {
          break; // 不够时继续收包
        }
      } else {

        get_pointer++;
        if (get_pointer >= cmdData.length)
          get_pointer = 0;
      }
      readbyes--;
    }

    if (bageok) {

      k = cmdData[(get_pointer + 1) % cmdData.length];
      if (k <= 0)
      LogUtils.e("k=0times" + times++);
      else {
        byte[] Data = new byte[k];
        for (int i = 0; i < k;) {
          Data[i++] = cmdData[get_pointer++];
          readbyes--;
          if (get_pointer >= cmdData.length)
            get_pointer = 0;
        }
        bageok = false;
        StringBuilder builder = new StringBuilder(Data.length);
        for (int i = 0; i < Data.length; i++) {
          builder.append(String.format("%02X ", Data[i]));

        }
//        mHandler.sendEmptyMessage(8);
        // loop=true;
        LogUtils.e("Data[2]"+Data[2]+"builder:"+builder.toString());
        switch (Data[2]) {

          case 0:// 收到信息包
//            deviceType = Data[5];// 得到机种号：血压1；血糖2；
            LogUtils.e("Data[5]得到机种号"+Data[5]+"Data*****"+ HexUtil.Bytes2HexString(Data));
                start();
            break;

          case 1:
            break;

          case 2: // 收到过程包
            int number=((short) (Data[6] * 256) + Data[5] >= 0 ? Data[5]
                : Data[5] + 256);
            LogUtils.e("收到过程包-----------------------------"+number);
            if (mbluetoothsatus!=null)mbluetoothsatus.current(number);
            break;

          case 3: // 收到结果包
            if (deviceType == BPM) {
              times++;
              String resultBG = "测量结果：" + getShort(Data, 9) + "/"
                  + (Data[11] & 0xff) + "/" + Data[12] + "("
                  + times + ")";
              LogUtils.e("测量结果-----------------------------"+resultBG);
              if (mbluetoothsatus!=null)mbluetoothsatus.getDate(new Info(getShort(Data, 9)+"" ,Data[12]+"",(Data[11] & 0xff)+""));
              CommandType = ACK_PACKE;
              mHandler.postDelayed(mRunnable, 300);
//

              UnRegisterReceiver();
            }
            // sendDataByte(Commands.CMD_LENGTH_TEN,Commands.CMD_CATEGORY_FIVE);}//发确认包
            else if (deviceType == BGM) {
              String result4 = getShort(Data, 9) + "";
              LogUtils.e("xuetang***********************************"+result4);
              swithXueTang(result4);
              CommandType = RESULT_PACKE;
              mHandler.postDelayed(mRunnable, 300);
            }

            break;
          case (byte) 0xEE: // 收到错误包
            LogUtils.e("收到错误包*********");
           // TipsToast.getInstance().gank(ContextProvider.get().getContext().getResources().getString(R.string.measure_fail));
            UnRegisterReceiver();
            break;

          case 5: // 收到结束包
            LogUtils.e("收到结束包*********");
            if (deviceType == BPM) {
              CommandType = START_PACKE;
            }
            UnRegisterReceiver();
            break;
          case 6:
          default:
            break;
        }
      }
    }
  }

  /**
   * 启动检测
   */
  private void start() {
    if (deviceType == BPM) {
      CommandType = START_PACKE;
      mHandler.postDelayed(mRunnable, 300);
    } else if (deviceType == BGM) {
      CommandType = RESULT_PACKE;				//注意，不同版本这里返回可能不同。
      mHandler.postDelayed(mRunnable, 300);
    }
  }
  //停止测量的命令 设备会关机
 private void stop(){
   sendDataByte(Commands.CMD_LENGTH_TEN,
       Commands.CMD_CATEGORY_TWO);

 }
  /**
   * 向设备发送命令
   *
   * @param ；包长度
   * @param ’包类别
   */
  private void sendDataByte(final byte leng, final byte commandType) {
    if (deviceType == BPM){
      sendDataByte = commands.getSystemdate(Commands.CMD_HEAD, leng,
          commandType);
    }else {
      sendDataByte=ZHexUtil.hexStringToBytes(ZDataUtil.getSendHex(1));
    }
    //LogUtils.e( "535" + mBluetoothGatt);
    SampleGattAttributes.sendMessage(mBluetoothGatt, sendDataByte);
    LogUtils.e( "发送的命令" + HexUtil.encodeHexStr(sendDataByte)+"*****************");

  }
  /**
   * 根据不同的设备　显示测量的结果值
   *
   * @param‘ type
   */

  public static short getShort(byte[] b, int index) {
    return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
  }

  private void swithXueTang(String result) {
    double resultValue = Double.parseDouble(result);
    resultValue = resultValue / 18;
    NumberFormat nf = NumberFormat.getInstance();
    nf.setRoundingMode(RoundingMode.HALF_UP);// 设置四舍五入
    nf.setMinimumFractionDigits(1);// 设置最小保留几位小数
    nf.setMaximumFractionDigits(1);// 设置最大保留几位小数
    bloodsugar.add(nf.format(resultValue));
    // displayTransformationData(nf.format(resultValue) + "mmol");

  }
  public static class Info{
    String dbp;
    String sbp;
    String rhp;

    @Override
    public String toString() {
      return "Info{" +
          "dbp='" + dbp + '\'' +
          ", sbp='" + sbp + '\'' +
          ", rhp='" + rhp + '\'' +
          '}';
    }

    public String getDbp() {
      return dbp;
    }

    public String getSbp() {
      return sbp;
    }

    public String getRhp() {
      return rhp;
    }

    public Info(String sbp, String rhp, String dbp) {
      this.dbp = dbp;
      this.sbp = sbp;
      this.rhp = rhp;
    }
  }
  private Runnable mRunnable = new Runnable() {
    @Override
    public void run() {

      switch (CommandType) {

        case INFORMATION_PACKE:
          mHandler.sendEmptyMessage(0);
          break;
        case START_PACKE:
          mHandler.sendEmptyMessage(1);
          break;
        case 2:
          mHandler.sendEmptyMessage(2);
          break;

        case RESULT_PACKE:
          mHandler.sendEmptyMessage(3);
          break;
        case 4:
          mHandler.sendEmptyMessage(4);
          break;
        case ACK_PACKE:
          mHandler.sendEmptyMessage(5);
          break;
        case HANDSHAKE_PACKET:
          mHandler.sendEmptyMessage(6);
          break;
        default:
          break;
      }

    }
  };

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(android.os.Message msg) {
      switch (msg.what) {
        case 0:
          sendDataByte(Commands.CMD_LENGTH_TEN,
              Commands.CMD_CATEGORY_ZERO);
          // mHandler.sendEmptyMessage(7);
          break;
        case 1:
          sendDataByte(Commands.CMD_LENGTH_TEN, Commands.CMD_CATEGORY_ONE);
          // mHandler.sendEmptyMessage(7);
          break;
        case 2:

          break;
        case 3:
          sendDataByte(Commands.CMD_LENGTH_TEN,
              Commands.CMD_CATEGORY_THReE);
          // mHandler.sendEmptyMessage(7);
          break;
        case 4:

          break;
        case 5:

          sendDataByte(Commands.CMD_LENGTH_TEN,
              Commands.CMD_CATEGORY_FIVE);
          // mHandler.sendEmptyMessage(7);

          break;
        case 6:
          SampleGattAttributes.sendMessage(mBluetoothGatt, new byte[] {
              0x0B, 0x0A });// 握手包
          break;
        case 9:
          //SampleGattAttributes.sendMessage(mBluetoothGatt,("cc80020304040001").getBytes());// 握手包
          break;
        case 222:
          LogUtils.e("连接成功*-****");
          sendDataByte(Commands.CMD_LENGTH_TEN,
              Commands.CMD_CATEGORY_ZERO);
          break;
      }

    }

  };



  private void RegisterReceiver(Bloodpress_intenface mbluetoothsatus) {
      this.mbluetoothsatus = mbluetoothsatus;
    intent = ContextProvider.get().getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
  }

  public void UnRegisterReceiver() {
    if (mbluetoothsatus!=null)mbluetoothsatus.err();
  if (isBingd) ContextProvider.get().getContext().unbindService(mServiceConnection);
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
