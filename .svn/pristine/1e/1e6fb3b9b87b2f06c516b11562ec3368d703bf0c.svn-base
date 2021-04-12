package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.contec.spo2.code.bean.SdkConstants;
import com.contec.spo2.code.callback.ConnectCallback;
import com.contec.spo2.code.callback.RealtimeCallback;
import com.contec.spo2.code.connect.ContecSdk;
import com.creative.base.BaseDate;
import com.xiekang.bluetooths.interfaces.GetOxgen;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;


/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 康泰血氧夹
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class OxgenKangTai_Bluetooth_Utlis {
  private static OxgenKangTai_Bluetooth_Utlis bloodpress_bluetooth_utlis;
  private final ContecSdk sdk;
  private GetOxgen getOxgen;

  public static OxgenKangTai_Bluetooth_Utlis getInstance() {
    if (bloodpress_bluetooth_utlis == null) {
      bloodpress_bluetooth_utlis = new OxgenKangTai_Bluetooth_Utlis();
    }
    return bloodpress_bluetooth_utlis;
  }

  private OxgenKangTai_Bluetooth_Utlis() {
    sdk = new ContecSdk(ContextProvider.get().getContext());
    sdk.init(true);
  }

  /**
   * 连接蓝牙
   *
   * @param
   */
  public void connect(BluetoothDevice bluetoothDevice, GetOxgen bluetooth_satus) {
    RegisterReceiver(bluetooth_satus);
    if (sdk != null) sdk.connect(bluetoothDevice, connectCallback);
  }

  /**
   * 连接回调
   */
  private ConnectCallback connectCallback = new ConnectCallback() {
    @Override
    public void onConnectStatus(final int status) {
         LogUtils.e("status:"+status);
      if (status == SdkConstants.CONNECT_CONNECTED) {
        //监听成功
        if (getOxgen != null) getOxgen.succed();
        startFingerOximeter();
      }
      if (status == SdkConstants.CONNECT_DISCONNECTED || status == SdkConstants.CONNECT_DISCONNECT_EXCEPTION
          || status == SdkConstants.CONNECT_DISCONNECT_SERVICE_UNFOUND
          || status == SdkConstants.CONNECT_DISCONNECT_NOTIFY_FAIL) {
        //断开
        UnRegisterReceiver();
      }
    }
  };

  private void startFingerOximeter() {
    sdk.startRealtime(new RealtimeCallback() {
      @Override
      public void onRealtimeWaveData(final int signal, final int prSound, final int waveData, int barData, int fingerOut) {
//            BloodOxygenFragments.SPO_WAVE.add(new BaseDate.Wave(waveData,fingerOut));
            LogUtils.e("signal = " + signal + "prSound = " + prSound + "waveData = " + waveData + "fingerOut=" + fingerOut);
           //探头有错误
            if (fingerOut==0){
              if (getOxgen != null) getOxgen.startDraw(new BaseDate.Wave(waveData,fingerOut));
            }


      }

      @Override
      public void onSpo2Data(final int piError, final int spo2, final int pr, final int pi) {

        if (getOxgen != null) getOxgen.getOxgen(spo2, pr);
        LogUtils.e("piError = " + piError + "   spo2 = " + spo2 + "   pr = " + pr + "   pi = " + pi);
      }

      @Override
      public void onRealtimeEnd() {
        LogUtils.e("实时数据结束");
      }

      @Override
      public void onFail(final int errorCode) {

        LogUtils.e("errorCode = " + errorCode);
      }
    });
  }

  private void RegisterReceiver(GetOxgen getTemperature) {
    this.getOxgen = getTemperature;
  }

  public void UnRegisterReceiver() {
    if (getOxgen!=null)getOxgen.err();
    sdk.disconnect();
  }

}
