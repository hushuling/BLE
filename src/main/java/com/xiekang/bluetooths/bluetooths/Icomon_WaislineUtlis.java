package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetWaislin;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.HexUtil;
import com.xiekang.bluetooths.utlis.LogUtils;

import cn.icomon.icdevicemanager.ICDeviceManager;
import cn.icomon.icdevicemanager.ICDeviceManagerDelegate;
import cn.icomon.icdevicemanager.ICDeviceManagerSettingManager;
import cn.icomon.icdevicemanager.callback.ICScanDeviceDelegate;
import cn.icomon.icdevicemanager.model.data.ICCoordData;
import cn.icomon.icdevicemanager.model.data.ICKitchenScaleData;
import cn.icomon.icdevicemanager.model.data.ICRulerData;
import cn.icomon.icdevicemanager.model.data.ICWeightCenterData;
import cn.icomon.icdevicemanager.model.data.ICWeightData;
import cn.icomon.icdevicemanager.model.device.ICDevice;
import cn.icomon.icdevicemanager.model.device.ICScanDeviceInfo;
import cn.icomon.icdevicemanager.model.device.ICUserInfo;
import cn.icomon.icdevicemanager.model.other.ICConstant;
import cn.icomon.icdevicemanager.model.other.ICDeviceManagerConfig;

import static com.xiekang.bluetooths.bean.Common.B0;
import static com.xiekang.bluetooths.bean.Common.B2;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 沃莱蓝牙腰围尺
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Icomon_WaislineUtlis implements  ICDeviceManagerDelegate, BluetoothDriver<GetWaislin> {
  private GetWaislin geturidate;
  private Bluetooth_Satus mbluetoothsatus;
  private  String wailstype = null;
  private ICDevice device;
  public static Icomon_WaislineUtlis icomon_waislineUtlis;

  private Icomon_WaislineUtlis() {
    initSDK();
  }

  private   String getWailstype() {
    return wailstype;
  }

  public  void setWailstype(String wailstype) {
    this.wailstype = wailstype;
  }

  public static Icomon_WaislineUtlis getInstance() {
    if (icomon_waislineUtlis == null) {
      icomon_waislineUtlis = new Icomon_WaislineUtlis();
    }
    return icomon_waislineUtlis;
  }

  private void initSDK() {
    ICDeviceManagerConfig config = new ICDeviceManagerConfig();
    config.context = ContextProvider.get().getContext();
    // TODO: set user info
    ICUserInfo userInfo = new ICUserInfo();
    userInfo.kitchenUnit = ICConstant.ICKitchenScaleUnit.ICKitchenScaleUnitOz;
    userInfo.rulerUnit = ICConstant.ICRulerUnit.ICRulerUnitInch;
    userInfo.age = 28;
    userInfo.weight = 70;
    userInfo.height = 171;
    userInfo.sex = ICConstant.ICSexType.ICSexTypeMale;
    ICDeviceManager.shared().setDelegate(this);
    ICDeviceManager.shared().updateUserInfo(userInfo);
    ICDeviceManager.shared().initMgrWithConfig(config);
  }

  @Override
  public void Connect(BluetoothDevice bluetoothDevice, GetWaislin date, Bluetooth_Satus bluetooth_satus) {
    this.mbluetoothsatus = bluetooth_satus;
    geturidate = date;
    LogUtils.e("测量类型1111111" + getWailstype());
    if (!TextUtils.isEmpty(bluetoothDevice.getName()) && !TextUtils.isEmpty(bluetoothDevice.getAddress())) {
      if (device == null) {
        device = new ICDevice();
        device.setMacAddr(bluetoothDevice.getAddress());
        ICDeviceManager.shared().addDevice(device, new ICConstant.ICAddDeviceCallBack() {
          @Override
          public void onCallBack(ICDevice device, ICConstant.ICAddDeviceCallBackCode code) {
            LogUtils.e("add device state : " + code + "deviceInfo" + bluetoothDevice.getName());
            mbluetoothsatus.succed();
            geturidate.succed();
          }
        });
      }
    }
  }

  public void UnRegisterReceiver() {
    if (device != null) {
      ICDeviceManager.shared().removeDevice(device, new ICConstant.ICRemoveDeviceCallBack() {
        @Override
        public void onCallBack(ICDevice icDevice, ICConstant.ICRemoveDeviceCallBackCode icRemoveDeviceCallBackCode) {
          LogUtils.e("removeDevice device state : " + icRemoveDeviceCallBackCode);
        }
      });
    }
    if (mbluetoothsatus != null) mbluetoothsatus.err();
    if (geturidate != null) geturidate.err();
    device = null;
  }

  @Override
  public void onInitFinish(boolean bSuccess) {
    LogUtils.e("SDK init result:" + bSuccess);
    ICDevice device = new ICDevice();

  }

  @Override
  public void onBleState(ICConstant.ICBleState state) {
    LogUtils.e("ble state:" + state);
    final int[] index = {0};
    if (state == ICConstant.ICBleState.ICBleStatePoweredOn) {

    }
  }

  @Override
  public void onDeviceConnectionChanged(final ICDevice device, final ICConstant.ICDeviceConnectState state) {
    LogUtils.e(device.getMacAddr() + ": connect state :" + state);

  }

  @Override
  public void onReceiveWeightData(ICDevice device, ICWeightData data) {
    LogUtils.e(device.getMacAddr() + ": weight data :" + data.weight_kg + "      " + data.temperature + "   " + data.imp + "   " + data.isStabilized);
    if (data.isStabilized) {
      int i = 0;
    }
  }

  @Override
  public void onReceiveKitchenScaleData(ICDevice device, ICKitchenScaleData data) {
    LogUtils.e(device.getMacAddr() + ": kitchen data:" + data.value_g + "\t" + data.value_lb + "\t" + data.value_lb_oz + "\t" + data.isStabilized);
  }

  @Override
  public void onReceiveKitchenScaleUnitChanged(ICDevice device, ICConstant.ICKitchenScaleUnit unit) {
    LogUtils.e(device.getMacAddr() + ": kitchen unit changed :" + unit);
  }

  @Override
  public void onReceiveCoordData(ICDevice device, ICCoordData data) {
    LogUtils.e(device.getMacAddr() + ": coord data:" + data.getX() + "\t" + data.getY() + "\t" + data.getTime());

  }

  @Override
  public void onReceiveRulerData(ICDevice device, ICRulerData data) {
    LogUtils.e("测量类型22222" + getWailstype());
    if (!TextUtils.isEmpty(getWailstype())) {
      if (data.getDistance_cm() > 0) {
        if (getWailstype().equals(B0)) {
          geturidate.getwaislin(HexUtil.formatfloat((float) data.getDistance_cm()), 0);
        }
        if (getWailstype().equals(B2)) {
          geturidate.getwaislin(0, HexUtil.formatfloat((float) data.getDistance_cm()));
        }
      }
      UnRegisterReceiver();
    }
    LogUtils.e(device.getMacAddr() + "device.name:" + device.toString() + ": ruler data :" + data.getDistance_cm() + "\t" + data.getPartsType() + "\t" + data.getTime() + "\t" + data.isStabilized());
    if (data.isStabilized()) {
      // demo, auto change device show body parts type
      if (data.getPartsType() == ICConstant.ICRulerBodyPartsType.ICRulerPartsTypeCalf) {
        return;
      }

      ICDeviceManager.shared().getSettingManager().setRulerBodyPartsType(device, ICConstant.ICRulerBodyPartsType.valueOf(data.getPartsType().getValue() + 1), new ICDeviceManagerSettingManager.ICSettingCallback() {
        @Override
        public void onCallBack(ICConstant.ICSettingCallBackCode code) {

        }
      });
    }
  }

  @Override
  public void onReceiveWeightCenterData(ICDevice icDevice, ICWeightCenterData data) {
    LogUtils.e(icDevice.getMacAddr() + ": center data :L=" + data.getLeftPercent() + "   R=" + data.getRightPercent() + "\t" + data.getTime() + "\t" + data.isStabilized());
  }

  @Override
  public void onReceiveWeightUnitChanged(ICDevice icDevice, ICConstant.ICWeightUnit unit) {
    LogUtils.e(icDevice.getMacAddr() + ": weigth unit changed :" + unit);
  }

  @Override
  public void onReceiveRulerUnitChanged(ICDevice icDevice, ICConstant.ICRulerUnit unit) {
    LogUtils.e(icDevice.getMacAddr() + ": ruler unit changed :" + unit);

  }

  @Override
  public void onReceiveRulerMeasureModeChanged(ICDevice icDevice, ICConstant.ICRulerMeasureMode mode) {
    LogUtils.e(icDevice.getMacAddr() + ": ruler measure mode changed :" + mode);

  }

}
