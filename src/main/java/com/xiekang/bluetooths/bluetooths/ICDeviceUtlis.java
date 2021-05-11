package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;

import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetIDcar;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import mpay.sdk.lib.BTLib;
import mpay.sdk.lib.CommonLib;
import mpay.sdk.lib.interfaces.BluetoothListener;
import mpay.sdk.lib.interfaces.CommandListener;
import mpay.sdk.lib.model.DevItem;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 IC 读卡器
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class ICDeviceUtlis implements BluetoothListener, CommandListener, BluetoothDriver<GetIDcar> {
  private static ICDeviceUtlis icDeviceUtlis;
  private final BTLib btLib;
  // Cotexts Lib
  public CommonLib commonLib = null;
  private GetIDcar getBodyfat;
  private Bluetooth_Satus satus;
  public static ICDeviceUtlis getInstance() {
    if (icDeviceUtlis == null) {
      icDeviceUtlis = new ICDeviceUtlis();
    }
    return icDeviceUtlis;
  }

  private ICDeviceUtlis() {
    btLib = new BTLib(ContextProvider.get().getContext(), true);
    commonLib = btLib;
    btLib.setCommandListener(this);
    btLib.setBTListener(this);
    btLib.btStart();

  }

  /**
   * 直接连接扫描的设备
   *
   * @param remoteDevice bleDevice - 扫描回调接口中的蓝牙设备
   * @param var6         callback - 称重过程的回调接口
   */
  public void Connect(final BluetoothDevice remoteDevice, final GetIDcar var6, Bluetooth_Satus bluetoothSatus) {
    boolean isSuccful = btLib.connectBTDeviceByAddress(remoteDevice.getAddress());
    this.satus=bluetoothSatus;
    if (isSuccful) {
      var6.succed();
    } else {
      var6.err();
    }
  }

  public void UnRegisterReceiver() {
    if (getBodyfat != null) getBodyfat.err();
    if (satus!=null)satus.err();
    btLib.disconnectBTDevice();
  }

  @Override
  public void onBluetoothState(final boolean enable) {
    LogUtils.e("Bluetooth Mode  -  bluetooth is " + (enable ? "enable" : "disable"));
  }

  @Override
  public void onBluetoothDeviceScaning() {
    LogUtils.e("onBluetoothDeviceScaning()");

  }

  @Override
  public void onBluetoothDeviceFound(final DevItem item) {
    LogUtils.e("onBluetoothDeviceFound()" +item.dev_name + " " + item.dev_address);

  }

  @Override
  public void onBluetoothDeviceScanOver() {
    LogUtils.e("onBluetoothDeviceScanOver()");
  }

  @Override
  public void onBluetoothDeviceBounding() {
    LogUtils.e("onBluetoothDeviceBounding()");
  }

  @Override
  public void onBluetoothDeviceBoundSuccess() {
    LogUtils.e("onBluetoothDeviceBoundSuccess()");
  }

  @Override
  public void onBluetoothDeviceBoundFailed() {
    LogUtils.e("onBluetoothDeviceBoundFailed()");
  }

  @Override
  public void onBluetoothDeviceConnecting() {
    LogUtils.e("onBluetoothDeviceConnecting()");
  }

  @Override
  public void onBluetoothDeviceConnected() {

    LogUtils.e("onBluetoothDeviceConnected()");
    btLib.cmdPICCActivate(3000);

  }

  @Override
  public void onBluetoothDeviceConnectFailed() {
    LogUtils.e("onBluetoothDeviceConnectFailed()");
  }

  @Override
  public void onBluetoothDeviceDisconnected() {

    LogUtils.e("onBluetoothDeviceDisconnected()");

  }


  @Override
  public void onReaderResponse(int returnCode, String returnMessage, String functionName)
  {
    // TODO Auto-generated method stub
    LogUtils.e(">> " + "Reader Response : " + "\n   Function : " + functionName + "\n   Return Code : " + returnCode + "\n   Return Message : " + returnMessage);
  }

  @Override
  public void onSDKResponse(int returnCode, String returnMessage, String functionName)
  {
    // TODO Auto-generated method stub
    LogUtils.e(">> " + "SDK Response : " + "\n   Function : " + functionName + "\n   Return Code : " + returnCode + "\n   Return Message : " + returnMessage);

  }

  @Override
  public void onGetVersion(boolean status, String version)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onICCStatus(boolean status, String iccStatus)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onICCPowerOn(boolean status, String atr)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onICCPowerOff(boolean status)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onICCAccess(boolean status, String rAPUD)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPICCActivate(boolean status, String cardSN)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      if (cardSN.length() > 1)
      {
        LogUtils.e(">> Card SN : " + cardSN);
      }

    }
  }

  @Override
  public void onPICCDeactivate(boolean status)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      LogUtils.e(">>	" + "Success");
    }
  }

  @Override
  public void onPICCRate(boolean status, String ats)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPICCAccess(boolean status, String rAPUD)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onMifareAuth(boolean status)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      LogUtils.e(">>	" + "Success");
    }
  }

  @Override
  public void onMifareReadBlock(boolean status, String data)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      if (data.length() > 1)
      {
        LogUtils.e(">> Data : " + data);
      }

    }
  }

  @Override
  public void onMifareWriteBlock(boolean status)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      LogUtils.e(">>	" + "Success");
    }
  }

  @Override
  public void onMifareIncrement(boolean status)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      LogUtils.e(">>	" + "Success");
    }
  }

  @Override
  public void onMifareDecrement(boolean status)
  {
    // TODO Auto-generated method stub
    if (status)
    {
      LogUtils.e(">>	" + "Success");
    }
  }

  @Override
  public void onSetUseVersion(boolean status)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSetBluetoothDeviceName(boolean status)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onGetReaderSN(boolean status, String sn)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSetReaderSN(boolean status)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDetectBattery(boolean status, String energy)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSetSleepTimer(boolean status)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onGetCardInfo(boolean status, String info, String pan, String cardholderName, String expDate)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSetICCPort(boolean status) {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onSelectMemoryCardType(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardPowerOn(boolean status, String atr)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardGetType(boolean status, String typeCode, String type)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardReadData(boolean status, String rAPDU)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardWriteData(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardPowerOff(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardReadErrorCounter(boolean status, int errorCounter)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardVerifyPSC(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardGetPSC(boolean status, String psc)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardModifyPSC(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardReadDataWithProtectBit(boolean status, String data)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardWriteDataWithProtectBit(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardReadProtectionData(boolean status, String data)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  @Override
  public void onMemoryCardWriteProtectionData(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub

  }

  public void onGiveUpAction(boolean status)
  {
    // TODO 鑷嫊鐢㈢敓鐨勬柟娉� Stub
    if (status) {
      LogUtils.e(">>	" + "Cancel Success");
    }
  }

}
