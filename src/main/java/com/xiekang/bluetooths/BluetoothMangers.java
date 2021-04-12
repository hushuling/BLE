package com.xiekang.bluetooths;

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
import com.xiekang.bluetooths.bean.NewUIData;
import com.xiekang.bluetooths.bluetooths.BC401_UricUtlis;
import com.xiekang.bluetooths.bluetooths.BloodFat_KaDik_Utlis;
import com.xiekang.bluetooths.bluetooths.BloodFat_LePuBluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.BloodsugarUtlis;
import com.xiekang.bluetooths.bluetooths.BluetoothAdapterContext;
import com.xiekang.bluetooths.bluetooths.IGateUtis;
import com.xiekang.bluetooths.bluetooths.OxgenKangTai_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.QNBleDeviceUtlis;
import com.xiekang.bluetooths.bluetooths.Sugar_OxygenUtlis;
import com.xiekang.bluetooths.bluetooths.Temp_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.bloopress.Bloodpress_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.bloopress.Bloodpress_intenface;
import com.xiekang.bluetooths.bluetooths.bloopress.Boodsugar_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.bloopress.Templte_Bluetooth_Utlis;
import com.xiekang.bluetooths.bluetooths.oxgen.Oxgen_Bluetooth_Utlis;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.Blutooth_Search;
import com.xiekang.bluetooths.interfaces.GetBloodfat;
import com.xiekang.bluetooths.interfaces.GetOxgen;
import com.xiekang.bluetooths.interfaces.GetTemperature;
import com.xiekang.bluetooths.interfaces.GetUriDate;
import com.xiekang.bluetooths.interfaces.Getbloodsuar;
import com.xiekang.bluetooths.utlis.Common;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.xiekang.bluetooths.utlis.Common.AlKAN;
import static com.xiekang.bluetooths.utlis.Common.BC01;
import static com.xiekang.bluetooths.utlis.Common.Bioland_BGM;
import static com.xiekang.bluetooths.utlis.Common.Bioland_BPM;
import static com.xiekang.bluetooths.utlis.Common.Bioland_IT;
import static com.xiekang.bluetooths.utlis.Common.Bluetooth_BP;
import static com.xiekang.bluetooths.utlis.Common.CardioChek;
import static com.xiekang.bluetooths.utlis.Common.LPM311;
import static com.xiekang.bluetooths.utlis.Common.OGM;
import static com.xiekang.bluetooths.utlis.Common.POD;
import static com.xiekang.bluetooths.utlis.Common.QN_Scale;
import static com.xiekang.bluetooths.utlis.Common.SpO2;
import static com.xiekang.bluetooths.utlis.Common.Statu;
import static com.xiekang.bluetooths.utlis.Common.connecttimeout;
import static com.xiekang.bluetooths.utlis.Common.iGate;
import static com.xiekang.bluetooths.utlis.Common.searchtimeout;

/**
 * @项目名称 bluetooths
 * @类名 name：com.xiekang.bluetooths
 * @类描述 describe
 * @创建人 hsl20
 * @创建时间 2021-01-19 16:54
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BluetoothMangers {
  private static final BluetoothMangers ourInstance = new BluetoothMangers();
  private final MyHanlder mhanler;
  private BluetoothAdapter mbluetoothAdapter;
  //设备固定的蓝牙名称或前缀
  private String mblutoothName;
  //搜索到对应的设备
  private boolean isSearch;
  //启动搜索的状态
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

  /**
   * 开始搜索
   *
   * @param mblutoothName   设备固定名称
   * @param blutooth_search 搜索进度接口
   */
  public void StartSearch(String mblutoothName, Blutooth_Search blutooth_search) {
    this.mblutoothName = mblutoothName;
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
    //得到蓝牙的适配器
    mbluetoothAdapter = BluetoothAdapterContext.getInstance().getBluetoothAdapter();
  }

  /**
   * 监听蓝牙连接状态
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
          LogUtils.e("tag", "STATE_OFF 手机蓝牙关闭");
          mbluetoothAdapter.enable();
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          LogUtils.e("tag", "STATE_TURNING_OFF 手机蓝牙正在关闭");
          break;
        case BluetoothAdapter.STATE_ON:
          LogUtils.e("tag", "STATE_ON 手机蓝牙开启");
          ContextProvider.get().getContext().unregisterReceiver(stateChangeReceiver);
          if (isStart) leScan();
          break;
        case BluetoothAdapter.STATE_TURNING_ON:
          LogUtils.e("tag", "STATE_TURNING_ON 手机蓝牙正在开启");
          break;
      }
    }
  };

  /**
   * 搜索蓝牙
   */
  private void leScan() {
    if (isStart) return;
    isStart = true;
    //正在扫描就，就退出点击时间
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
   * 停止搜索
   */
  public void StopScan() {
    if (isStart) {
      mbluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
    }
    isStart = false;
  }

  /**
   * 搜索蓝牙4.0的回调接口
   * 记得更新UI在runOnUiThread（）；方法里面
   */
  private ScanCallback mLeScanCallback = new ScanCallback() {

    public void onScanResult(int callbackType, ScanResult result) {
      //这里根据相应的蓝牙设备，做改动，我这里设备是名称是以“BLE”开头
      //将搜索蓝牙设备加入显示列表
      LogUtils.e("搜索到附近的蓝牙设备-*--------" + "***" +result.getDevice().getName() + "****" + result.getDevice().getAddress() + "***" + result.getScanRecord() );
      if (!TextUtils.isEmpty(result.getDevice().getName())) {
        //rssi 距离限制
        if (result.getDevice().getName().contains(mblutoothName) && !deviceList.contains(result.getDevice()) && !scanRecordlist.contains(result.getScanRecord())) {
          deviceList.add(result.getDevice());
          scanRecordlist.add(result.getScanRecord().getBytes());
          isSearch = true;
          LogUtils.e("添加到搜索列表中****");
        }
        if (blutooth_search != null&&deviceList.size()>0&&scanRecordlist.size()>0)
          blutooth_search.Searched(deviceList, scanRecordlist);

      }
    }
    public void onBatchScanResults(List<ScanResult> results) {
      LogUtils.e("onBatchScanResults-*--------" + "***" +results.size() );
    }

    public void onScanFailed(int errorCode) {
      LogUtils.e("onScanFailed****"+errorCode);
    }
  };
  //连接的状态
  boolean succed = false;

  /**
   * 连接设备
   *
   * @param remoteDevice    搜索到对映设备的蓝牙对象
   * @param bluetooth_satus 获取数据和状态接口
   */
  public void Connect(BluetoothDevice remoteDevice, final Bluetooth_Satus bluetooth_satus) {
    succed = false;
    mhanler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (!succed) {
          Stop();
          bluetooth_satus.err();
        }
      }
    }, connecttimeout);

    if (!TextUtils.isEmpty(mblutoothName)) {
      switch (mblutoothName) {
        case OGM:
          BloodsugarUtlis.getInstance().connect(remoteDevice, new Getbloodsuar() {
            @Override
            public void getbloodsugar(float bloodsugar) {
              ((Getbloodsuar) bluetooth_satus).getbloodsugar(bloodsugar);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
          break;
        case Bioland_BPM: {
          Bloodpress_Bluetooth_Utlis.getInstance().connect(remoteDevice, new Bloodpress_intenface<Bloodpress_Bluetooth_Utlis.Info>() {

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }

            @Override
            public void current(int date) {
              ((Bloodpress_intenface) bluetooth_satus).current(date);
            }

            @Override
            public void getDate(Bloodpress_Bluetooth_Utlis.Info uiData) {
              ((Bloodpress_intenface) bluetooth_satus).getDate(uiData);
            }
          });
        }
        break;
        case Bioland_BGM: {
          Boodsugar_Bluetooth_Utlis.getInstance().connect(remoteDevice, new Getbloodsuar() {
            @Override
            public void getbloodsugar(float bloodsugar) {
              ((Getbloodsuar) bluetooth_satus).getbloodsugar(bloodsugar);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
        case Bluetooth_BP: {
          Temp_Bluetooth_Utlis.getInstance().connect(remoteDevice, new GetTemperature() {
            @Override
            public void getbloodfat(String chlo) {
              ((GetTemperature) bluetooth_satus).getbloodfat(chlo);
            }

            @Override
            public void errCode(String messager) {
              ((GetTemperature) bluetooth_satus).errCode(messager);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
        case Bioland_IT: {

          Templte_Bluetooth_Utlis.getInstance().connect(remoteDevice, new GetTemperature() {
            @Override
            public void getbloodfat(String chlo) {
              ((GetTemperature) bluetooth_satus).getbloodfat(chlo);

            }

            @Override
            public void errCode(String messager) {
              ((GetTemperature) bluetooth_satus).errCode(messager);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }

          });
        }
        break;
        case SpO2: {

          OxgenKangTai_Bluetooth_Utlis.getInstance().connect(remoteDevice, new GetOxgen<BaseDate.Wave>() {
            @Override
            public void getOxgen(final int spo2, final int pr) {
              ((GetOxgen) bluetooth_satus).getOxgen(spo2, pr);
            }

            @Override
            public void startDraw(BaseDate.Wave wave) {
              ((GetOxgen) bluetooth_satus).startDraw(wave);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
        case POD: {
          Oxgen_Bluetooth_Utlis.getInstance().connect(remoteDevice, new GetOxgen<BaseDate.Wave>() {
            @Override
            public void getOxgen(final int spo2, final int pr) {
              ((GetOxgen) bluetooth_satus).getOxgen(spo2, pr);
            }

            @Override
            public void startDraw(BaseDate.Wave wave) {
              ((GetOxgen) bluetooth_satus).startDraw(wave);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
        case BC01: {
          BC401_UricUtlis.getInstance().judgeconnect(remoteDevice, new GetUriDate<NewUIData>() {
            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }

            @Override
            public void getBodyfat(final NewUIData uiData) {
              ((GetUriDate) bluetooth_satus).getBodyfat(uiData);

            }
          });
        }
        break;
        case CardioChek: {

          BloodFat_KaDik_Utlis.getInstance().connect(remoteDevice, new GetBloodfat() {
            @Override
            public void getbloodfat(float chlo, float trig, float hdl, float ldl) {
              ((GetBloodfat) bluetooth_satus).getbloodfat(chlo, trig, hdl, ldl);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
        case LPM311: {
          BloodFat_LePuBluetooth_Utlis.getInstance().connect(remoteDevice, new GetBloodfat() {
            @Override
            public void getbloodfat(float chlo, float trig, float hdl, float ldl) {
              ((GetBloodfat) bluetooth_satus).getbloodfat(chlo, trig, hdl, ldl);

            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
        case iGate: {
          IGateUtis.getInstance().connect(remoteDevice, new GetBloodfat() {
            @Override
            public void getbloodfat(float chlo, float trig, float hdl, float ldl) {
              ((GetBloodfat) bluetooth_satus).getbloodfat(chlo, trig, hdl, ldl);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;

        case AlKAN: {
          Sugar_OxygenUtlis.getInstance().connect(remoteDevice, new Getbloodsuar() {
            @Override
            public void getbloodsugar(float chlo) {
              ((Getbloodsuar) bluetooth_satus).getbloodsugar(chlo);
            }

            @Override
            public void succed() {
              Statu(bluetooth_satus, Statu);
            }

            @Override
            public void err() {
              Statu(bluetooth_satus, 0);
            }
          });
        }
        break;
      }

    }
  }

  private void Statu(Bluetooth_Satus bluetooth_satus, int errcode) {
    if (errcode == Statu) {
      succed = true;
      bluetooth_satus.succed();
    } else {
      bluetooth_satus.err();
    }
  }

  /**
   * 重置设备
   */
  public void Restart() {
    isStart = false;
    regiestBroast();
    if (mbluetoothAdapter.isEnabled()) {
      mbluetoothAdapter.disable();
    } else {
      mbluetoothAdapter.enable();
    }

  }

  /**
   * 停止检测
   */
  public void Stop() {
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
        case Bluetooth_BP:
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

      }
    }
  }

  /**
   * @param isShow         是否开启日志
   * @param searchtimeout  搜索蓝牙设备的超时时间
   * @param connecttimeout 连接设备的超时时间
   */
  public void init(boolean isShow, long searchtimeout, long connecttimeout) {
    LogUtils.debug = isShow;
    Common.searchtimeout = searchtimeout;
    Common.connecttimeout = connecttimeout;
  }
}
