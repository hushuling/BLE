package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.contec.bc.code.base.ContecDevice;
import com.contec.bc.code.bean.ContecBluetoothType;
import com.contec.bc.code.callback.CommunicateCallback;
import com.contec.bc.code.connect.ContecSdk;
import com.xiekang.bluetooths.bean.NewUIData;
import com.xiekang.bluetooths.interfaces.GetUriDate;
import com.xiekang.bluetooths.interfaces.TimerListener;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;
import com.xiekang.bluetooths.utlis.ReadTaskTimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;
import static com.xiekang.bluetooths.bluetooths.BltManager.BLUE_TOOTH_CLEAR;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 康泰尿液4.0&3.0
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BC401_UricUtlis {
  private static BC401_UricUtlis bc401_uricUtlis;
  private String TAG = "BC401_UricUtlis";
  private GetUriDate geturidate;
  private boolean isConnect=false;
  private static ContecSdk sdk;
  public static BC401_UricUtlis getInstance() {
    if (bc401_uricUtlis==null){
      bc401_uricUtlis=new BC401_UricUtlis();
    }
    return bc401_uricUtlis;
  }
  private BC401_UricUtlis() {
  }
  public void  judgeconnect(BluetoothDevice device, GetUriDate bluetooth_satus ){
    RegisterReceiver(bluetooth_satus);
    if (device.getType() == DEVICE_TYPE_LE) {
      sdk = new ContecSdk();
      //初始化蓝牙模块
      sdk.init(ContecBluetoothType.TYPE_FF, false);
      ContecDevice contectDevice = new ContecDevice();
      contectDevice.setDevice(device);
      //设置本次通信要获取的数据类型
      sdk.setObtainDataType(ContecSdk.ObtainDataType.SINGLE);
      //启动连接获取数据
      isConnect=true;
      sdk.startCommunicate(ContextProvider.get().getContext(), contectDevice, communicateCallback);
    } else {
      connect(device);
    }
  }

  CommunicateCallback communicateCallback = new CommunicateCallback() {

    @Override
    public void onCommunicateIndexSuccess(String s) {

    }

    @Override
    public void onCommunicateSuccess(final String json) {
      //与设备同步数据成功后返回json格式的数据，根据自身需求解析
      LogUtils.e(TAG, "onCommunicateSuccess"+json);
      //解析Json数据的示例程序
      try {
        JSONArray jsonArray = new JSONObject(json).getJSONArray("Data");

        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);

          String date = jsonObject.getString("Date");
          String URO = jsonObject.getString("URO");
          String BLD = jsonObject.getString("BLD");
          String BIL = jsonObject.getString("BIL");
          String KET = jsonObject.getString("KET");
          String GLU = jsonObject.getString("GLU");
          String PRO = jsonObject.getString("PRO");
          String PH = jsonObject.getString("PH");
          String NIT = jsonObject.getString("NIT");
          String LEU = jsonObject.getString("LEU");
          String SG = jsonObject.getString("SG");
          String VC = jsonObject.getString("VC");
          String MAL = jsonObject.getString("MAL");
          String CR = jsonObject.getString("CR");
          String UCA = jsonObject.getString("UCA");
          NewUIData uiData = new NewUIData();
          uiData.setUro(URO);
          uiData.setPh(PH);
          uiData.setBld(BLD);
          uiData.setNit(NIT);
          uiData.setBil(BIL);
          uiData.setLeu(LEU);
          uiData.setKet(KET);
          uiData.setSg(SG);
          uiData.setGlu(GLU);
          uiData.setVc(VC);
          uiData.setPro(PRO);
          uiData.setMAL(MAL);
          uiData.setCRE(CR);
          uiData.setUCA(UCA);
          if (isConnect){
            geturidate.getBodyfat(uiData);
          }
          isConnect=false;
          LogUtils.e(TAG, "Date = " + date);
          LogUtils.e(TAG, "URO = " + URO);
          LogUtils.e(TAG, "BLD = " + BLD);
          LogUtils.e(TAG, "KET = " + KET);
          LogUtils.e(TAG, "GLU = " + GLU);
          LogUtils.e(TAG, "PRO = " + PRO);
          LogUtils.e(TAG, "PH = " + PH);
          LogUtils.e(TAG, "NIT = " + NIT);
          LogUtils.e(TAG, "LEU = " + LEU);
          LogUtils.e(TAG, "SG = " + SG);
          LogUtils.e(TAG, "VC = " + VC);
          LogUtils.e(TAG, "MAL = " + MAL);
          LogUtils.e(TAG, "CR = " + CR);
          LogUtils.e(TAG, "UCA = " + UCA);
          UnRegisterReceiver();
         // sdk.stopCommunicate();
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }

    }

    @Override
    public void onCommunicateFailed(int errorCode) {
      //查看通信出错原因
      LogUtils.e(TAG, "通信出错代码" + errorCode);
      UnRegisterReceiver();
    }

    @Override
    public void onCommunicateProgress(int status) {
      if (geturidate!=null&&status==1)geturidate.succed();
      LogUtils.e(TAG, "status  = " + status);
    }
  };

  /**
   * 连接蓝牙3.0
   *
   * @param
   */
  public void connect(final BluetoothDevice bluetoothDevice) {

    //链接的操作应该在子线程
    new Thread(new Runnable() {
      @Override
      public void run() {
        BltManager.getInstance().createBond(bluetoothDevice, handler);
      }
    }).start();

  }

  public BluetoothSocket getmBluetoothSocket() {
    return BltManager.getInstance().getmBluetoothSocket();
  }

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      String date;
      switch (msg.what) {
        case 4://已连接某个设备
          if (geturidate!=null)geturidate.succed();
            try {
              final int count = 0;
              final byte[] vlaue = new byte[64];
              byte[] oneTrans = new byte[]{-109, -114, 4, 0, 9, 4, 17};
              ReadTaskTimer.getInstance().setTimerListener(new TimerListener() {
                @Override
                public void onTimer() {
                  if (count % 20 == 0) {
                    LogUtils.e(this.getClass().getName() + "finally**");
                    InputStream inputStream = null;
                    int len = 0;
                    try {
                      inputStream =  BC401_UricUtlis.getInstance().getmBluetoothSocket().getInputStream();
                      len = inputStream.read(vlaue);
                      byte[] dates = new byte[len];
                      if (len > 0) {
                        for (int i = 0; i < len; i++) {
                          Verifier.getInstance().addByte(Byte.valueOf(vlaue[i]));
                          dates[i] = vlaue[i];
                        }
                        LogUtils.e(" ReceiveSocketService", HexUtil.encodeHexStr(dates) + "***********len*******" + len);
                        if (Verifier.getInstance().getByte().size() > 20) {
                          NewUIData analyse = Verifier.getInstance().analyse();
                          if (analyse != null) {
                            Verifier.getInstance().clear();
                            if (geturidate!=null)geturidate.getBodyfat(analyse);
                            ReadTaskTimer.getInstance().stopReadTask();
                            UnRegisterReceiver();
                          }

                        }
                      }
                    } catch (Exception e) {
                      e.printStackTrace();
                      LogUtils.e(this.getClass().getName() + "onTimer", e.getMessage());
                    } finally {
                    }
                  }
                }
              });
              BC401_UricUtlis.getInstance().getmBluetoothSocket().getOutputStream().write(oneTrans);
            } catch (IOException e) {
              e.printStackTrace();
            }

          break;
        case 5:
          //链接失败 重新搜索链接
         UnRegisterReceiver();
          break;

      }
    }
  };

  private void RegisterReceiver(GetUriDate geturidate) {
    this.geturidate=geturidate;

  }

  public void UnRegisterReceiver() {
    if (geturidate!=null)geturidate.err();
    isConnect=false;
    if (sdk==null){
      //断开蓝牙连接
      BltManager.getInstance().clickBlt( BLUE_TOOTH_CLEAR);
    }else {
      sdk.stopCommunicate();
    }

  }
}
