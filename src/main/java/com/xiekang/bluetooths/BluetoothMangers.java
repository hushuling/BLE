package com.xiekang.bluetooths;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.creative.base.BaseDate;
import com.xiekang.bluetooths.bean.BloodpressDate;
import com.xiekang.bluetooths.bean.Common;
import com.xiekang.bluetooths.bean.NewUIData;
import com.xiekang.bluetooths.bluetooths.BC401_UricUtlis;
import com.xiekang.bluetooths.bluetooths.BloodFat_KaDik_Utlis;
import com.xiekang.bluetooths.bluetooths.BloodFat_LePuBluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.BloodsugarUtlis;
import com.xiekang.bluetooths.bluetooths.BluetoothAdapterContext;
import com.xiekang.bluetooths.bluetooths.DingHen_W_Heigth_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.ICDeviceUtlis;
import com.xiekang.bluetooths.bluetooths.ID300DeviceUtlis;
import com.xiekang.bluetooths.bluetooths.IGateUtis;
import com.xiekang.bluetooths.bluetooths.Icomon_WaislineUtlis;
import com.xiekang.bluetooths.bluetooths.OxgenKangTai_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.QNBleDeviceUtlis;
import com.xiekang.bluetooths.bluetooths.Sugar_OxygenUtlis;
import com.xiekang.bluetooths.bluetooths.Temp_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.bloopress.Bloodpress_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.bloopress.Boodsugar_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.bloopress.Templte_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.breath_home.Breath_Home_DeviceUtlis;
import com.xiekang.bluetooths.bluetooths.oxgen.Oxgen_Bluetooth_Utlis;
import com.xiekang.bluetooths.interfaces.Bloodpress_intenface;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.Blutooth_Search;
import com.xiekang.bluetooths.interfaces.GetBloodfat;
import com.xiekang.bluetooths.interfaces.GetBodyfat;
import com.xiekang.bluetooths.interfaces.GetBreath;
import com.xiekang.bluetooths.interfaces.GetH_Weight;
import com.xiekang.bluetooths.interfaces.GetIDcar;
import com.xiekang.bluetooths.interfaces.GetOxgen;
import com.xiekang.bluetooths.interfaces.GetTemperature;
import com.xiekang.bluetooths.interfaces.GetUriDate;
import com.xiekang.bluetooths.interfaces.GetWaislin;
import com.xiekang.bluetooths.interfaces.Getbloodsuar;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.xiekang.bluetooths.bean.Common.AlKAN;
import static com.xiekang.bluetooths.bean.Common.BC01;
import static com.xiekang.bluetooths.bean.Common.Bioland_BGM;
import static com.xiekang.bluetooths.bean.Common.Bioland_BPM;
import static com.xiekang.bluetooths.bean.Common.Bioland_IT;
import static com.xiekang.bluetooths.bean.Common.Breath;
import static com.xiekang.bluetooths.bean.Common.Icomon;
import static com.xiekang.bluetooths.bean.Common.TD133;
import static com.xiekang.bluetooths.bean.Common.CardioChek;
import static com.xiekang.bluetooths.bean.Common.DH;
import static com.xiekang.bluetooths.bean.Common.iDR210;
import static com.xiekang.bluetooths.bean.Common.LPM311;
import static com.xiekang.bluetooths.bean.Common.Mr5;
import static com.xiekang.bluetooths.bean.Common.OGM;
import static com.xiekang.bluetooths.bean.Common.POD;
import static com.xiekang.bluetooths.bean.Common.QN_Scale;
import static com.xiekang.bluetooths.bean.Common.SpO2;
import static com.xiekang.bluetooths.bean.Common.connecttimeout;
import static com.xiekang.bluetooths.bean.Common.iGate;
import static com.xiekang.bluetooths.bean.Common.searchtimeout;
import static com.xiekang.bluetooths.utlis.DeviceIdUtil.IsRegister;

/**
 * @???????????? bluetooths
 * @?????? name???com.xiekang.bluetooths
 * @????????? describe
 * @????????? hsl20
 * @???????????? 2021-01-19 16:54
 * @?????????
 * @???????????? time
 * @???????????? describe
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BluetoothMangers{
  private static final BluetoothMangers ourInstance = new BluetoothMangers();
  private final MyHanlder mhanler;
  private BluetoothAdapter mbluetoothAdapter;
  //????????????????????????????????????
  private String mblutoothName;
  //????????????????????????
  private boolean isSearch;
  //?????????????????????
  private boolean isStart;
  private Blutooth_Search blutooth_search;
  private List<BluetoothDevice> deviceList = new ArrayList<>();
  private List<byte[]> scanRecordlist = new ArrayList<>();

  private static class MyHanlder extends Handler {
    private WeakReference<BluetoothMangers> baseApplicationWeakReference;

    public MyHanlder(BluetoothMangers baseApplication) {
      baseApplicationWeakReference = new WeakReference<>(baseApplication);
    }
  }

  public static BluetoothMangers getInstance() {
    return ourInstance;
  }
  private Bluetooth_Satus date;
  public void RegisterReceiver( Bluetooth_Satus bluetooth_satus) {
   this.date=bluetooth_satus;
  }
  /**
   * ????????????
   *
   * @param mblutoothName   ??????????????????
   * @param blutooth_search ??????????????????
   */
  public void StartSearch(String mblutoothName, Blutooth_Search blutooth_search) {
    if (!IsRegister()) return;
    this.mblutoothName = mblutoothName;
    //???????????????????????????????????????
    this.blutooth_search = blutooth_search;
    deviceList.clear();
    scanRecordlist.clear();
    isSearch = false;
    if (!mbluetoothAdapter.enable()) {
      regiestBroast();
      mbluetoothAdapter.enable();
    } else {
      if (!mbluetoothAdapter.isEnabled()) {
        throw new IllegalStateException("The BluetoothAdapter's enable err");
      } else {
        leScan();
      }
    }
  }

  private BluetoothMangers() {
    mhanler = new MyHanlder(this);
    //????????????????????????
    mbluetoothAdapter = BluetoothAdapterContext.getInstance().getBluetoothAdapter();
  }

  /**
   * ????????????????????????
   */
  private void regiestBroast() {
    IntentFilter connectedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    ContextProvider.get().getContext().registerReceiver(stateChangeReceiver, connectedFilter);
  }

  private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      int action = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
          BluetoothAdapter.ERROR);
      LogUtils.e("tag" + "action======" + action);
      switch (action) {
        case BluetoothAdapter.STATE_OFF:
          LogUtils.e("tag", "STATE_OFF ??????????????????");
          mbluetoothAdapter.enable();
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          LogUtils.e("tag", "STATE_TURNING_OFF ????????????????????????");
          break;
        case BluetoothAdapter.STATE_ON:
          LogUtils.e("tag", "STATE_ON ??????????????????");
          ContextProvider.get().getContext().unregisterReceiver(stateChangeReceiver);
          if (isStart) leScan();
          break;
        case BluetoothAdapter.STATE_TURNING_ON:
          LogUtils.e("tag", "STATE_TURNING_ON ????????????????????????");
          break;
      }
    }
  };

  /**
   * ????????????
   */
  private void leScan() {
    if (isStart) return;
    isStart = true;
    //???????????????????????????????????????
    boolean discovering = mbluetoothAdapter.isDiscovering();
    LogUtils.e("===============>", "discovering=" + discovering + "isSearch***");
    if (discovering) {
      return;
    }
    mbluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
    mhanler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (!isSearch) {
          if (blutooth_search != null) blutooth_search.Timeout();
        }
        StopScan();
      }
    }, searchtimeout);
  }

  /**
   * ????????????
   */
  public void StopScan() {
    if (!IsRegister()) return;
    if (isStart) {
      mbluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
    }
    isStart = false;
  }

  /**
   * ????????????4.0???????????????
   * ????????????UI???runOnUiThread?????????????????????
   */
  private ScanCallback mLeScanCallback = new ScanCallback() {

    public void onScanResult(int callbackType, ScanResult result) {
      String blutoothname=mblutoothName;
      if (mblutoothName.equals(Breath)) blutoothname=Breath_Home_DeviceUtlis.getInstance().getBreachConfigBuilder().getIMEI();
      //?????????????????????????????????????????????????????????????????????????????????BLE?????????
      //???????????????????????????????????????
      LogUtils.e("?????????????????????" + "**" + result.getDevice().getName() + "**" + result.getDevice().getAddress());
      if (!TextUtils.isEmpty(result.getDevice().getName())) {
        //rssi ????????????
        if (result.getDevice().getName().contains(blutoothname) && !deviceList.contains(result.getDevice()) && !scanRecordlist.contains(result.getScanRecord())) {
          deviceList.add(result.getDevice());
          scanRecordlist.add(result.getScanRecord().getBytes());
          isSearch = true;
          LogUtils.e("????????????????????????****");
        }
        if (blutooth_search != null && deviceList.size() > 0 && scanRecordlist.size() > 0)
          blutooth_search.Searched(deviceList, scanRecordlist);

      }
    }

    public void onBatchScanResults(List<ScanResult> results) {
      LogUtils.e("onBatchScanResults-*--------" + "***" + results.size());
    }

    public void onScanFailed(int errorCode) {
      LogUtils.e("onScanFailed****" + errorCode);
    }
  };
  //???????????????
  boolean succed = false;

  /**
   * ????????????
   *
   * @param remoteDevice    ????????????????????????????????????
   */
  public void Connect(BluetoothDevice remoteDevice,Bluetooth_Satus bluetooth_satus) {
    if (!IsRegister()) return;
    LogUtils.e(remoteDevice.getName()+"**"+remoteDevice.getAddress()+"????????????**");
    succed = false;
    mhanler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (!succed) {
          UnRegisterReceiver();
          bluetooth_satus.err();
        }
      }
    }, connecttimeout);

    if (!TextUtils.isEmpty(mblutoothName)) {
      switch (mblutoothName) {
        case QN_Scale:
          QNBleDeviceUtlis.getInstance().Connect(remoteDevice, (GetBodyfat) date,bluetooth_satus);
          break;
        case OGM:
          BloodsugarUtlis.getInstance().Connect(remoteDevice, (Getbloodsuar) date,bluetooth_satus);
          break;
        case Bioland_BPM:
          Bloodpress_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (Bloodpress_intenface<BloodpressDate>) date,bluetooth_satus);
          break;
        case Bioland_BGM:
          Boodsugar_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (Getbloodsuar) date,bluetooth_satus);
          break;
        case TD133:
          Temp_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (GetTemperature) date,bluetooth_satus);
          break;
        case Bioland_IT:
          Templte_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (GetTemperature) date,bluetooth_satus);
          break;
        case SpO2:
          OxgenKangTai_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (GetOxgen<BaseDate.Wave>) date,bluetooth_satus);
          break;
        case POD:
          Oxgen_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (GetOxgen<BaseDate.Wave>) date,bluetooth_satus);
          break;
        case BC01:
          BC401_UricUtlis.getInstance().Connect(remoteDevice, (GetUriDate<NewUIData>) date,bluetooth_satus);
          break;
        case CardioChek:
          BloodFat_KaDik_Utlis.getInstance().Connect(remoteDevice, (GetBloodfat) date,bluetooth_satus);
          break;
        case LPM311:
          BloodFat_LePuBluetooth_Utlis.getInstance().Connect(remoteDevice, (GetBloodfat) date,bluetooth_satus);
          break;
        case iGate:
          IGateUtis.getInstance().Connect(remoteDevice, (GetBloodfat) date,bluetooth_satus);
          break;
        case AlKAN:
          Sugar_OxygenUtlis.getInstance().Connect(remoteDevice, (Getbloodsuar) date,bluetooth_satus);
          break;
        case Mr5:
          ICDeviceUtlis.getInstance().Connect(remoteDevice, (GetIDcar) date,bluetooth_satus);
          break;
        case DH:
          DingHen_W_Heigth_Bluetooth_Utlis.getInstance().Connect(remoteDevice, (GetH_Weight) date,bluetooth_satus);
          break;
        case iDR210:
          ID300DeviceUtlis.getInstance().Connect(remoteDevice, (GetIDcar) date,bluetooth_satus);
          break;
        case Breath:
          Breath_Home_DeviceUtlis.getInstance().Connect(remoteDevice, (GetBreath) date,bluetooth_satus);
          break;
        case Icomon:
          Icomon_WaislineUtlis.getInstance().Connect(remoteDevice, (GetWaislin) date,bluetooth_satus);
          break;
      }

    }
  }


  /**
   * ????????????
   */
  public void Restart() {
    if (!IsRegister()) return;
    isStart = false;
    regiestBroast();
    if (mbluetoothAdapter.isEnabled()) {
      mbluetoothAdapter.disable();
    } else {
      mbluetoothAdapter.enable();
    }

  }
  /**
   * ????????????
   */
  public void UnRegisterReceiver() {
    if (!IsRegister()) return;
    StopScan();
    if (!TextUtils.isEmpty(mblutoothName)) {
      switch (mblutoothName) {
        case OGM:
          BloodsugarUtlis.getInstance().UnRegisterReceiver();
          break;
        case Bioland_BPM:
          Bloodpress_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case Bioland_BGM:
          Boodsugar_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case TD133:
          Temp_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case Bioland_IT:
          Temp_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case QN_Scale:
          QNBleDeviceUtlis.getInstance().UnRegisterReceiver();
          break;
        case POD:
          Oxgen_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case BC01:
          BC401_UricUtlis.getInstance().UnRegisterReceiver();
          break;
        case CardioChek:
          BloodFat_KaDik_Utlis.getInstance().UnRegisterReceiver();
          break;
        case LPM311:
          BloodFat_LePuBluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case SpO2:
          OxgenKangTai_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case iGate:
          IGateUtis.getInstance().UnRegisterReceiver();
          break;
        case AlKAN:
          Sugar_OxygenUtlis.getInstance().UnRegisterReceiver();
          break;
        case Mr5:
          ICDeviceUtlis.getInstance().UnRegisterReceiver();
          break;
        case DH:
          DingHen_W_Heigth_Bluetooth_Utlis.getInstance().UnRegisterReceiver();
          break;
        case iDR210:
          ID300DeviceUtlis.getInstance().UnRegisterReceiver();
          break;
        case Breath:
          Breath_Home_DeviceUtlis.getInstance().UnRegisterReceiver();
          break;

      }
    }
  }

  /**
   * @param isShow         ??????????????????
   * @param searchtimeout  ?????????????????????????????????
   * @param connecttimeout ???????????????????????????
   */
  public void init(Activity activity, boolean isShow, long searchtimeout, long connecttimeout) {
    Common.searchtimeout = searchtimeout;
    Common.connecttimeout = connecttimeout;
    if (!IsRegister()) new RegisterActivity(activity).show();

  }
  /**
   * @param isShow         ??????????????????
   * @param searchtimeout  ?????????????????????????????????
   * @param connecttimeout ???????????????????????????
   */
  public void init( boolean isShow, long searchtimeout, long connecttimeout) {
    Common.searchtimeout = searchtimeout;
    Common.connecttimeout = connecttimeout;

  }


}
