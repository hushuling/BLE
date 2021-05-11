package com.xiekang.bluetooths.bluetooths.breath_home;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.breathhome_ble_sdk.asynctask.AsyncResponse;
import com.breathhome_ble_sdk.asynctask.GetBleCurrentVersionTask;
import com.breathhome_ble_sdk.asynctask.SearchBleDeviceTask;
import com.breathhome_ble_sdk.bean.BleVersionMsgBean;
import com.breathhome_ble_sdk.bean.BluetoothDeviceBean;
import com.breathhome_ble_sdk.bean.HolderBean;
import com.breathhome_ble_sdk.bean.PefDataFromBleBean;
import com.breathhome_ble_sdk.bean.ReturnBean;
import com.breathhome_ble_sdk.broadreceiver.BroadcastResponse;
import com.breathhome_ble_sdk.controller.BluetoothController;
import com.breathhome_ble_sdk.message.MessageManager;
import com.breathhome_ble_sdk.utils.ConstantUtils;
import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.bean.BreachConfigBuilder;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetBreath;
import com.xiekang.bluetooths.interfaces.GetH_Weight;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

/*
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 呼吸家
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Breath_Home_DeviceUtlis implements BluetoothDriver<GetBreath> {
  private GetBreath getDate;
  private BreachConfigBuilder breachConfigBuilder;

  public BreachConfigBuilder getBreachConfigBuilder() {
    return breachConfigBuilder;
  }

  public void setBreachConfigBuilder(BreachConfigBuilder breachConfigBuilder) {
    this.breachConfigBuilder = breachConfigBuilder;
  }


  private MyBleStateBroadcast myBleStateBroadcast;      //自定义蓝牙状态监听
  private MessageManager myMessageManager;   //蓝牙信息管理器
  private BluetoothController mBluetoothController;
  private StringBuffer sb;            //用作存放蓝牙设备的指令
  private HolderBean holder;       //检测人信息，需要从服务器上获取
  private BleVersionMsgBean bleVersionMsg;      //当前版本号
  private BluetoothDeviceBean bluetoothDeviceBean;

  private  static  Breath_Home_DeviceUtlis breath_home_deviceUtlis;
  public static Breath_Home_DeviceUtlis getInstance() {
    if (breath_home_deviceUtlis == null) {
      breath_home_deviceUtlis = new Breath_Home_DeviceUtlis();
    }
    return breath_home_deviceUtlis;
  }

  public  void initDatas() {
    sb = new StringBuffer();
    holder = new HolderBean();
    holder.setDetectedNo(33);
    holder.setSaleChannel(7200000);
    holder.setBirthdate("1988-3-15");
    holder.setPef(659);
    holder.setFev1((float) 4.56);
    holder.setFvc((float) 3.56);
    holder.setGender(1);
    holder.setHeight(breachConfigBuilder.getHeight());
    holder.setWeight(breachConfigBuilder.getWeight());
    holder.setDevieceNo("B116080001");
    getTheNewsVersion();
  }

  private GetBleCurrentVersionTask getBleCurrentVersionTask;

  private void getTheNewsVersion() {
    getBleCurrentVersionTask = (GetBleCurrentVersionTask) new GetBleCurrentVersionTask(ContextProvider.get().getContext()).execute();
    getBleCurrentVersionTask.setAsyncResponse(new AsyncResponse<ReturnBean<BleVersionMsgBean>>() {

      @Override
      public void onDataReceivedFailed() {
        // TODO Auto-generated method stub
          LogUtils.e("Error");
//				bleVersionMsg=
      }

      @Override
      public void onDataReceivedSuccess(ReturnBean<BleVersionMsgBean> returnBean) {
        bleVersionMsg = returnBean.getObject();
        initTools();              //初始化工具
        initBroadcast();            //定义广播接收
      }

      @Override
      public void onDataReceivedAndDefaultData(
          ReturnBean<BleVersionMsgBean> returnBean) {
        bleVersionMsg = returnBean.getObject();
        // TODO Auto-generated method stub
        initTools();              //初始化工具
        initBroadcast();            //定义广播接收

      }
    });
  }

  private void initTools() {
    myMessageManager = new MessageManager().getInstance(ContextProvider.get().getContext());
    mBluetoothController = BluetoothController.getInstance(ContextProvider.get().getApplication());
    mBluetoothController.setServiceHandler(new Handler(){
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
     LogUtils.e("handleMessage："+msg.what+"***"+msg.obj);
        switch(msg.what) {
          case 1:
            mBluetoothController.stopScanBLE();
            break;
//          case 2:
//            Intent intent = new Intent("action.update.device.list");
//            BluetoothDevice device = (BluetoothDevice)msg.obj;
//            intent.putExtra("name", device.getName());
//            intent.putExtra("address", device.getAddress());
//            BLEService.this.sendBroadcast(intent);
//            break;
          case 3:
            Bundle bundle = (Bundle)msg.obj;
            String address = bundle.getString("address");
            String name = bundle.getString("name");
            Bundle bundle1 = new Bundle();
            bundle1.putString("address", address);
            bundle1.putString("name", name);
            Intent intentDevice = new Intent("action.connected.one.device");
            intentDevice.putExtras(bundle1);
            ContextProvider.get().getContext().sendBroadcast(intentDevice);
            break;
          case 4:
            String mes = (String)msg.obj;
            Intent mesDevice = new Intent("action.receive.message");
            mesDevice.putExtra("message", mes);
            ContextProvider.get().getContext().sendBroadcast(mesDevice);
            break;
          case 5:
            Intent stopConnect = new Intent("action.stop.connect");
            ContextProvider.get().getContext().sendBroadcast(stopConnect);
        }
      }
    });
    mBluetoothController.initBLE();
    mBluetoothController.setOnGattNoneListener(new BluetoothController.onGattNoneListener() {

      @Override
      public void onGattNone() {
          LogUtils.e("发送链接！！");
      /*  if (devicelist.size() != 0) {
          for (BluetoothDeviceBean bleDevice : devicelist) {
            if (bleDevice.getName().equals(getIMEI())) {
              mBluetoothController.stopScanBLE();
              mBluetoothController.connect(bleDevice);
              break;
            }
          }
        }*/
      }
    });
    myMessageManager.setmBluetoothController(mBluetoothController);
        LogUtils.e("bleDetectBean", holder.toString());
    myMessageManager.setHolder(holder);           //MessageManager需要设置检测人信息
    if (null == bleVersionMsg) {
    LogUtils.e("Error Code:" + ConstantUtils.ERROR_CODE_NULL_CURRENT_BLEVERSION);
    } else {
          LogUtils.e("bleVersionMsg", bleVersionMsg.toString());
      myMessageManager.setBleVersionmsg(bleVersionMsg);
    }
    myMessageManager.setmListener(new MessageManager.ReceiveCommandCodeListener() {

      @Override
      public void matchSuccess() {
        //成功连接
//        idTvLoadingmsg.setText("成功连接，可以开始检测了");
      LogUtils.e("成功连接，可以开始检测了");
        getDate.succed();
        if (satus!=null)satus.succed();
//    state_btn.setText(R.string.ble_state_connect);
      }

      @Override
      public void needUpdateBleProgram(Boolean isNeedUpdate) {
        if (isNeedUpdate) {//需要进行更新设备的程序
//     Toast.makeText(BreathHomeActivity.this, R.string.hint_updateprogram, Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void sendDataFromBleDevice(PefDataFromBleBean pefdata) {
        PefDataFromBleBean mypefData = pefdata;
      LogUtils.e("数据获取成功！！");
        //获取到的数据上传到服务器中.略
        //sendData();
        if (getDate != null) {
          getDate.sendDataFromBleDevice(pefdata.getFvcValue(),pefdata.getFev1Value(),pefdata.getPefValue());
        }

//    getPresenter().QuerySave(pefdata.getFvcValue(),pefdata.getFev1Value(),pefdata.getPefValue());
//    TipsToast.gank("日期:"+pefdata.getDate()+"\r\n"+"Pef:"+
//      pefdata.getPefValue()+"\r\n"+"FVC:"+pefdata.getFvcValue()+"\r\n"
//      +"FEV1:"+pefdata.getFev1Value());
        UnRegisterReceiver();
      }
    });
  }

  private void initBroadcast() {
    myBleStateBroadcast = new MyBleStateBroadcast();
    IntentFilter intentFilter = new IntentFilter();
//    intentFilter.addAction(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
    intentFilter.addAction(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE);
    intentFilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_DEVICE);
    intentFilter.addAction(ConstantUtils.ACTION_STOP_CONNECT);
    ContextProvider.get().getContext().registerReceiver(myBleStateBroadcast, intentFilter);
    myBleStateBroadcast.setMyBroadcastResponse(new BroadcastResponse() {

                                                 @Override
                                                 public void onSearchBleSuccess(BluetoothDeviceBean bleDevice) {
                                                  /* //搜索到蓝牙设备
                                                   boolean found = false;//记录该条记录是否在list中
                                                   for (BluetoothDeviceBean device : devicelist) {
                                                     if (device.getAddress().equals(bleDevice.getAddress())) {
                                                       found = true;
                                                       break;
                                                     }
                                                   }// for
                                                   if (!found) {
                                                     devicelist.add(bleDevice);
                                                     if (bleDevice.getName().equals(getIMEI())) {
                                                       mBluetoothController.stopScanBLE();
                                                       mBluetoothController.connect(bleDevice);
                                                     }

                                                   } else {//断开后重新连接

                                                   }*/
                                                 }

                                                 @Override
                                                 public void onBleConnect() {
                                                   //蓝牙设备已匹配上
//                                                   idTvLoadingmsg.setText("正在配对");
                                                 LogUtils.e("正在配对");
                                                 }

                                                 @Override
                                                 public void onBleDisconnect() {
                                                   //蓝牙设备断开连接
//                                                   idTvLoadingmsg.setText("设备断开连接");
                                                 LogUtils.e("设备断开连接");
                                                   UnRegisterReceiver();
                                                 }

                                                 @Override
                                                 public void onReceiveBleMsg(String str) {
                                                   //接收到蓝牙设备的指令
                                                   sb.append(str);
                                                   if (-1 != sb.lastIndexOf("\r\n")) {//判断结尾符
                                                         LogUtils.e("Receive Msg" + sb.toString());
                                                     StringBuffer tmp = new StringBuffer(sb.toString());
                                                     myMessageManager.matchCommandCode(tmp);
                                                     sb.setLength(0);      //StringBuffer清零
                                                   }
                                                 }
                                               }

    );
  }


  public void UnRegisterReceiver() {
    if (getDate != null) getDate.err();
    if (satus!=null)satus.err();
    mBluetoothController.stopConnectBLe();

  }
  private Bluetooth_Satus satus;
  /**
   * 连接蓝牙
   * @param bluetoothDevice
   * @param bluetooth_satus
   */
  public  void  Connect(BluetoothDevice bluetoothDevice, GetBreath bluetooth_satus,Bluetooth_Satus satus){
    this.getDate=bluetooth_satus;
    this.satus=satus;
  bluetoothDeviceBean = new BluetoothDeviceBean();
  bluetoothDeviceBean.setAddress(bluetoothDevice.getAddress());
  bluetoothDeviceBean.setName(bluetoothDevice.getName());
  if (mBluetoothController!=null) mBluetoothController.connect(bluetoothDeviceBean);
}
}
