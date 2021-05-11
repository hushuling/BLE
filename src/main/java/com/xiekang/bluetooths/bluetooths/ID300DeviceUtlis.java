package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.xiekang.bluetooths.bean.IdCardInfo;
import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetIDcar;
import com.xiekang.bluetooths.utlis.LogUtils;
import com.zkteco.id3xx.IDCardReader;
import com.zkteco.id3xx.meta.IDCardInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 中控身份证蓝牙读卡器
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class ID300DeviceUtlis implements BluetoothDriver<GetIDcar> {
  private static ID300DeviceUtlis icDeviceUtlis;
  private GetIDcar getIDcar;
  private IDCardReader idCardReader = null;
  private WorkThread workThread = null;
  private boolean mbStop = true;
  private Bluetooth_Satus satus;
  public static ID300DeviceUtlis getInstance() {
    if (icDeviceUtlis == null) {
      icDeviceUtlis = new ID300DeviceUtlis();
    }
    return icDeviceUtlis;
  }
  /**
   * 直接连接扫描的设备
   *
   * @param device bleDevice - 扫描回调接口中的蓝牙设备
   * @param var6         callback - 称重过程的回调接口
   */
  public void Connect(final BluetoothDevice device, final GetIDcar var6, Bluetooth_Satus bluetoothSatus) {
    this.getIDcar=var6;
    this.satus=bluetoothSatus;
    idCardReader = new IDCardReader();
    BluetoothSocket mBluetoothSocket = null;
    try {
      mBluetoothSocket = device.createRfcommSocketToServiceRecord(IDCardReader.myuuid);
      mBluetoothSocket.connect();
      InputStream mInputStream = mBluetoothSocket.getInputStream();
      OutputStream mOutputStream = mBluetoothSocket.getOutputStream();
      idCardReader.init(mInputStream,mOutputStream);
      //开启读卡
      if (idCardReader != null){
        try{
          Thread.sleep(500);
        }catch (Exception e){
          e.printStackTrace();
        }
        OnBnRead();
      }
    } catch (IOException e) {
      e.printStackTrace();
      LogUtils.e("连接异常："+e.toString()+"**重新连接");
      OnBnDisconn();
      Connect(device,var6,bluetoothSatus);
    }

  }

  public void UnRegisterReceiver() {
    if (getIDcar != null) getIDcar.err();
    if (satus!=null)satus.err();
    getIDcar = null;
    OnBnDisconn();
  }

  private class WorkThread extends Thread {
    @Override
    public void run() {
      super.run();
      while (!mbStop) {
        ReadCardInfo();

        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private boolean ReadCardInfo() {
    if (idCardReader == null) {
      return false;
    }
    if (!idCardReader.sdtFindCard()) {
      return false;
    } else {
      if (!idCardReader.sdtSelectCard()) {
        return false;
      }
    }
    final IDCardInfo idCardInfo = new IDCardInfo();
    if (idCardReader.sdtReadCard(1, idCardInfo)) {
      LogUtils.e("身份证数据获取成功" + idCardInfo.getId());
      IdCardInfo datas = new IdCardInfo();
      datas.name = idCardInfo.getName();

      datas.cardNO = idCardInfo.getId().toUpperCase();
      datas.dep = idCardInfo.getDepart(); //公安局
      datas.sexs = idCardInfo.getSex();
      datas.address = idCardInfo.getAddress();
      datas.nation = idCardInfo.getNation();
      datas.birthday = idCardInfo.getBirth();
      datas.lifestart = idCardInfo.getValidityTime();
      datas.lifeend = idCardInfo.getValidityTime();
      if (getIDcar != null) getIDcar.getIdCardInfo(datas);
      //断开连接
      UnRegisterReceiver();

      return true;
    }

    LogUtils.e("Bill", "读卡失败！");

    return false;
  }

  public void OnBnRead() {
    if (null == idCardReader) {
      LogUtils.e("请先连接设备");
      return;
    }
    if (!mbStop) {
      return;
    }
    LogUtils.e("Bill", "连接成功");
    if (getIDcar!=null)getIDcar.succed();
    if (satus!=null)satus.succed();
    mbStop = false;
    workThread = new WorkThread();
    workThread.start();// 线程启动
  }

  private void OnBnDisconn() {
    if (null == idCardReader) {
      return;
    }
    if (!mbStop) {
      mbStop = true;
    }
    idCardReader.closeDevice();
    idCardReader = null;
    workThread.interrupt();
    LogUtils.e("Bill", "断开设备成功");
  }

}
