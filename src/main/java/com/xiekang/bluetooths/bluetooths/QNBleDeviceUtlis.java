package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.qingniu.qnble.utils.QNLogUtils;
import com.qingniu.scale.constant.DecoderConst;
import com.qn.device.constant.CheckStatus;
import com.qn.device.constant.QNIndicator;
import com.qn.device.constant.QNInfoConst;
import com.qn.device.constant.UserGoal;
import com.qn.device.constant.UserShape;
import com.qn.device.listener.QNBleConnectionChangeListener;
import com.qn.device.listener.QNLogListener;
import com.qn.device.listener.QNResultCallback;
import com.qn.device.listener.QNScaleDataListener;
import com.qn.device.out.QNBleApi;
import com.qn.device.out.QNBleDevice;
import com.qn.device.out.QNScaleData;
import com.qn.device.out.QNScaleStoreData;
import com.qn.device.out.QNUser;
import com.xiekang.bluetooths.bean.QNConfigBuilder;
import com.xiekang.bluetooths.BluetoothDriver;
import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;
import com.xiekang.bluetooths.interfaces.GetBodyfat;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 轻牛体脂
 * @创建人 hsl20
 * @创建时间 2018/6/25 9:22
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class QNBleDeviceUtlis implements BluetoothDriver<GetBodyfat> {
  private static QNBleDeviceUtlis qnBleDeviceUtlis;
  private  GetBodyfat getBodyfat;
  private Bluetooth_Satus satus;
  private QNBleApi qnBleApi;
  private QNUser createQNUser;
  private String height;
  private QNBleDevice qnBleDevice;
  private boolean mIsConnected;
  private QNConfigBuilder qnConfigBuilder;
  public static QNBleDeviceUtlis getInstance() {
    if (qnBleDeviceUtlis==null){
      qnBleDeviceUtlis=new QNBleDeviceUtlis();
    }
    return qnBleDeviceUtlis;
  }

  private QNBleDeviceUtlis() {
    initQinniu();

  }
  private void initQinniu() {
    String encryptPath = "file:///android_asset/xkwlkj202103.qn";
    QNLogUtils.setLogEnable(LogUtils.isDebug ());//设置日志打印开关，默认关闭
    QNLogUtils.setWriteEnable(LogUtils.isDebug ());//设置日志写入文件开关，默认关闭
    qnBleApi = QNBleApi.getInstance(ContextProvider.get().getContext());
    qnBleApi.initSdk("xkwlkj202103", encryptPath, new QNResultCallback() {
      @Override
      public void onResult(int code, String msg) {
        LogUtils.e("initSdk", "code:"+code+"**msg:" + msg);
      }
    });
    //此API是用来监听日志的，如果需要上传日志到服务器则可以使用，否则不需要设置
    qnBleApi.setLogListener(new QNLogListener() {
      @Override
      public void onLog(String log) {
        LogUtils.e("onLog:" + log);
      }
    });
  }

  public QNConfigBuilder getQnConfigBuilder() {
    return qnConfigBuilder;
  }

  public void QNConfigBuilder(QNConfigBuilder qnConfigBuilder){
    this.qnConfigBuilder=qnConfigBuilder;
  }
  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
  @Override
  public void Connect(final BluetoothDevice remoteDevice, final GetBodyfat var6, Bluetooth_Satus bluetoothSatus) {
    if (qnConfigBuilder==null)return;
    this.satus=bluetoothSatus;
    height= String.valueOf(qnConfigBuilder.getHeight());
    mIsConnected=true;
    this.getBodyfat=var6;
    UserShape userShape;
    userShape = UserShape.SHAPE_NONE;
    UserGoal userGoal;
    userGoal = UserGoal.GOAL_NONE;
    try {
      createQNUser = qnBleApi.buildUser("123456789",
          qnConfigBuilder.getHeight(), qnConfigBuilder.getGender() == 1 ? QNInfoConst.GENDER_MAN : QNInfoConst.GENDER_WOMAN, dateFormat.parse(qnConfigBuilder.getBirthday()), 0,
          userShape, userGoal, 0, new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
              Log.d("createQNUser", "创建用户信息返回:" + msg);
            }
          });
    } catch (ParseException e) {
      e.printStackTrace();
    }

    qnBleDevice = qnBleApi.buildDevice(remoteDevice, -1, qnConfigBuilder.getScanRecord(), new QNResultCallback() {
      @Override
      public void onResult(int code, String msg) {
        if (code != CheckStatus.OK.getCode()) {
          QNLogUtils.log("LeScanCallback", msg);
        }
      }
    });
    initBleConnectStatus();
    initUserData(); //设置数据监听器,返回数据,需在连接当前设备前设置
    qnBleApi.connectDevice(qnBleDevice, createQNUser, new QNResultCallback() {
      @Override
      public void onResult(int code, String msg) {
        Log.d("ConnectActivity", "连接设备返回:" + msg);
      }
    });
  }
  public void UnRegisterReceiver() {
    doDisconnect();
    qnConfigBuilder=null;
    if (getBodyfat!=null)getBodyfat.err();
    getBodyfat=null;
  }


  /**
   * 断开连接
   */
  private void doDisconnect() {
    mIsConnected=false;
    qnBleApi.disconnectDevice(qnBleDevice, new QNResultCallback() {
      @Override
      public void onResult(int i, String s) {
        LogUtils.e("disconnectDevice",i+"****"+s);
      }
    });
    qnBleApi.setBleConnectionChangeListener(null);
    qnBleApi.setDataListener(null);
  }
  private void initBleConnectStatus() {
    qnBleApi.setBleConnectionChangeListener(new QNBleConnectionChangeListener() {
      //正在连接
      @Override
      public void onConnecting(QNBleDevice device) {
        LogUtils.e("正在连接");
      }

      //已连接
      @Override
      public void onConnected(QNBleDevice device) {
        LogUtils.e("已连接");
        if (getBodyfat!=null)getBodyfat.succed();
        if (satus!=null)satus.succed();
      }

      @Override
      public void onServiceSearchComplete(QNBleDevice device) {

      }

      //正在断开连接，调用断开连接时，会马上回调
      @Override
      public void onDisconnecting(QNBleDevice device) {
        LogUtils.e("正在断开连接，调用断开连接时，会马上回调");
      }

      // 断开连接，断开连接后回调
      @Override
      public void onDisconnected(QNBleDevice device) {
        LogUtils.e("断开连接，断开连接后回调"+mIsConnected);
        if (mIsConnected){
          LogUtils.e("异常断开");
          if(getBodyfat!=null)getBodyfat.ConectLost();
          qnBleApi.connectDevice(qnBleDevice, createQNUser, new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
              Log.d("ConnectActivity", "连接设备返回:" + msg);
            }
          });
        }
      }

      //出现了连接错误，错误码参考附表
      @Override
      public void onConnectError(QNBleDevice device, int errorCode) {
        LogUtils.d("ConnectActivity", "onConnectError:" + errorCode);
      }

    });
  }
  private void initUserData() {
    qnBleApi.setDataListener(new QNScaleDataListener() {
      @Override
      public void onGetUnsteadyWeight(QNBleDevice device, double weight) {
        LogUtils.e(  "体重是:" + weight);
      }

      @Override
      public void onGetScaleData(QNBleDevice device, QNScaleData all) {
        LogUtils.e(   "完成测量收到测量数据"+all.toString());
        HashMap<String,Double>listdate=new HashMap<>();
        //保存到健康建议 并显示ui
        listdate.put(all.getItem(QNIndicator.TYPE_WEIGHT).getName()   , all.getItem(QNIndicator.TYPE_WEIGHT).getValue());
        listdate.put(all.getItem(QNIndicator.TYPE_BMI).getName()        , all.getItem(QNIndicator.TYPE_BMI).getValue());
        listdate.put( all.getItem(QNIndicator.TYPE_WATER).getName()     ,  all.getItem(QNIndicator.TYPE_WATER).getValue());
        listdate.put( all.getItem(QNIndicator.TYPE_BODYFAT).getName()   ,  all.getItem(QNIndicator.TYPE_BODYFAT).getValue());
        listdate.put( all.getItem(QNIndicator.TYPE_BMR).getName()      ,  all.getItem(QNIndicator.TYPE_BMR).getValue());
        listdate.put( all.getItem(QNIndicator.TYPE_MUSCLE_MASS).getName(),  all.getItem(QNIndicator.TYPE_MUSCLE_MASS).getValue());
        listdate.put(  all.getItem(QNIndicator.TYPE_BONE).getName()     ,   all.getItem(QNIndicator.TYPE_BONE).getValue());
        listdate.put(  all.getItem(QNIndicator.TYPE_VISFAT).getName()   ,   all.getItem(QNIndicator.TYPE_VISFAT).getValue());
        listdate.put(  all.getItem(QNIndicator.TYPE_MUSCLE).getName()   ,   all.getItem(QNIndicator.TYPE_MUSCLE).getValue());
        listdate.put(  all.getItem(QNIndicator.TYPE_PROTEIN).getName()  ,   all.getItem(QNIndicator.TYPE_PROTEIN).getValue());
        listdate.put(  all.getItem(QNIndicator.TYPE_SUBFAT).getName()   ,   all.getItem(QNIndicator.TYPE_SUBFAT).getValue());
        listdate.put(   all.getItem(QNIndicator.TYPE_LBM).getName()     ,    all.getItem(QNIndicator.TYPE_LBM).getValue());



        if (getBodyfat!=null)getBodyfat.getBodyfat(listdate,height);
        getBodyfat=null;

      }

      @Override
      public void onGetStoredScale(QNBleDevice device, List<QNScaleStoreData> storedDataList) {
        LogUtils.e(   "收到存储数据");
      }

      @Override
      public void onGetElectric(QNBleDevice device, int electric) {
        String text = "收到电池电量百分比" + electric;
        LogUtils.e(   text);
        if (electric == DecoderConst.NONE_BATTERY_VALUE) {//获取电池信息失败
          return;
        }
      }

      //测量过程中的连接状态
      @Override
      public void onScaleStateChange(QNBleDevice device, int status) {
        LogUtils.e(   "秤的连接状态是:" + status);
      }

      @Override
      public void onScaleEventChange(QNBleDevice device, int scaleEvent) {
        Log.d("ConnectActivity", "秤返回的事件是:" + scaleEvent);
      }
    });
  }

}
