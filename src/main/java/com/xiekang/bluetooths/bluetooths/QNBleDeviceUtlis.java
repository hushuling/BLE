package com.xiekang.bluetooths.bluetooths;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.qingniu.qnble.utils.QNLogUtils;
import com.qingniu.scale.constant.DecoderConst;
import com.qn.device.constant.CheckStatus;
import com.qn.device.constant.QNBleConst;
import com.qn.device.constant.QNIndicator;
import com.qn.device.constant.QNInfoConst;
import com.qn.device.constant.QNScaleStatus;
import com.qn.device.constant.UserGoal;
import com.qn.device.constant.UserShape;
import com.qn.device.listener.QNBleProtocolDelegate;
import com.qn.device.listener.QNLogListener;
import com.qn.device.listener.QNResultCallback;
import com.qn.device.listener.QNScaleDataListener;
import com.qn.device.out.QNBleApi;
import com.qn.device.out.QNBleDevice;
import com.qn.device.out.QNBleProtocolHandler;
import com.qn.device.out.QNScaleData;
import com.qn.device.out.QNScaleStoreData;
import com.qn.device.out.QNUser;
import com.xiekang.bluetooths.interfaces.GetBodyfat;
import com.xiekang.bluetooths.utlis.ContextProvider;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
public class QNBleDeviceUtlis {
  private static QNBleDeviceUtlis qnBleDeviceUtlis;
  private  GetBodyfat getBodyfat;
  private QNBleApi qnBleApi;
  private QNUser createQNUser;
  private BluetoothGatt mBluetoothGatt;
  private BluetoothGattCharacteristic qnReadBgc, qnWriteBgc, qnBleReadBgc, qnBleWriteBgc;
  private QNBleProtocolHandler mProtocolhandler;
  private boolean isFirstService;
  private String height;
  private QNBleDevice qnBleDevice;

  public static QNBleDeviceUtlis getInstance() {
    if (qnBleDeviceUtlis==null){
      qnBleDeviceUtlis=new QNBleDeviceUtlis();
    }
    return qnBleDeviceUtlis;
  }

  private QNBleDeviceUtlis() {


  }
  public void initQinniu() {
    String encryptPath = "file:///android_asset/xkwlkj202103.qn";
    QNLogUtils.setLogEnable(LogUtils.debug);//设置日志打印开关，默认关闭
    QNLogUtils.setWriteEnable(LogUtils.debug);//设置日志写入文件开关，默认关闭
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

  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");

  /**
   * 直接连接扫描的设备
   * @param remoteDevice bleDevice - 扫描回调接口中的蓝牙设备
   * @param scanRecord 远程设备提供的配对号
   * @param var2 userId - 用户标识，用户唯一，传非空的字符串，可以使用 用户名，手机号，邮箱等其它标识
   * @param var3 height - 身高，单位cm
   * @param var4 gender - 性别 男：1 女：0
   * @param var5 birthday - 生日，精确到天
   * @param var6 callback - 称重过程的回调接口
   */
  public void Connect(final BluetoothDevice remoteDevice,byte[] scanRecord ,String var2, int var3, int var4, String var5, final GetBodyfat var6) {
    doDisconnect();
    height= String.valueOf(var3);
    this.getBodyfat=var6;
    UserShape userShape;
    userShape = UserShape.SHAPE_NONE;
    UserGoal userGoal;
    userGoal = UserGoal.GOAL_NONE;
    try {
      createQNUser = qnBleApi.buildUser("123456789",
          var3, var4 == 1 ? QNInfoConst.GENDER_MAN : QNInfoConst.GENDER_WOMAN, dateFormat.parse(var5), 0,
          userShape, userGoal, 0, new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
              Log.d("createQNUser", "创建用户信息返回:" + msg);
            }
          });
    } catch (ParseException e) {
      e.printStackTrace();
    }

    qnBleDevice = qnBleApi.buildDevice(remoteDevice, -1, scanRecord, new QNResultCallback() {
      @Override
      public void onResult(int code, String msg) {
        if (code != CheckStatus.OK.getCode()) {
          QNLogUtils.log("LeScanCallback", msg);
        }
      }
    });
    initUserData(); //设置数据监听器,返回数据,需在连接当前设备前设置
    buildHandler(qnBleDevice);
    mBluetoothGatt = remoteDevice.connectGatt(ContextProvider.get().getContext(), false, mGattCallback);
  }
  public void UnRegisterReceiver() {
    doDisconnect();
    if (getBodyfat!=null)getBodyfat.err();
  }
  /**
   * 断开连接
   */
  private void doDisconnect() {
    if (mBluetoothGatt != null) {
      mBluetoothGatt.disconnect();
    }
    if (mProtocolhandler != null) {
      mProtocolhandler = null;
    }
  }

  private void initUserData() {
    qnBleApi.setDataListener(new QNScaleDataListener() {
      @Override
      public void onGetUnsteadyWeight(QNBleDevice device, double weight) {
        LogUtils.e(  "体重是:" + weight);
      }

      @Override
      public void onGetScaleData(QNBleDevice device, QNScaleData data) {
        LogUtils.e(   "完成测量收到测量数据"+data.toString());
        double weight= data.getItem(QNIndicator.TYPE_WEIGHT).getValue();
        LogUtils.e( "收到体重:"+weight);
        if (getBodyfat!=null)getBodyfat.getBodyfat(data,height);
        //测量结束,断开连接
        UnRegisterReceiver();
        LogUtils.e(   "加密hmac为:" + data.getHmac());

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
  private void buildHandler(QNBleDevice mBleDevice) {
    mProtocolhandler = qnBleApi.buildProtocolHandler(mBleDevice, createQNUser, new QNBleProtocolDelegate() {
      @Override
      public void writeCharacteristicValue(String service_uuid, String characteristic_uuid, byte[] data, QNBleDevice qnBleDevice) {
        writeCharacteristicData(service_uuid, characteristic_uuid, data, qnBleDevice.getMac());
      }

      @Override
      public void readCharacteristic(String service_uuid, String characteristic_uuid, QNBleDevice qnBleDevice) {
        readCharacteristicData(service_uuid, characteristic_uuid, qnBleDevice.getMac());

      }
    }, new QNResultCallback() {
      @Override
      public void onResult(int code, String msg) {
        LogUtils.e("创建结果----" + code + " ------------- " + msg);
      }
    });
  }

  private void readCharacteristicData(String service_uuid, String characteristic_uuid, String mac) {

    switch (characteristic_uuid) {
      case QNBleConst.UUID_IBT_READ:

        if (mBluetoothGatt != null && qnReadBgc != null) {
          mBluetoothGatt.readCharacteristic(qnReadBgc);
        }

        break;
      case QNBleConst.UUID_IBT_BLE_READER:

        if (mBluetoothGatt != null && qnBleReadBgc != null) {
          mBluetoothGatt.readCharacteristic(qnBleReadBgc);
        }

        break;
      case QNBleConst.UUID_IBT_READ_1:

        if (mBluetoothGatt != null && qnReadBgc != null) {
          mBluetoothGatt.readCharacteristic(qnReadBgc);
        }

        break;

    }

  }

  private void writeCharacteristicData(String service_uuid, String characteristic_uuid, byte[] data, String mac) {
    switch (characteristic_uuid) {
      case QNBleConst.UUID_IBT_WRITE:

        if (mBluetoothGatt != null && qnWriteBgc != null) {
          qnWriteBgc.setValue(data);
          mBluetoothGatt.writeCharacteristic(qnWriteBgc);
        }

        break;
      case QNBleConst.UUID_IBT_BLE_WRITER:

        if (mBluetoothGatt != null && qnBleWriteBgc != null) {
          qnBleWriteBgc.setValue(data);
          mBluetoothGatt.writeCharacteristic(qnBleWriteBgc);
        }

        break;
      case QNBleConst.UUID_IBT_WRITE_1:

        if (mBluetoothGatt != null && qnWriteBgc != null) {
          qnWriteBgc.setValue(data);
          mBluetoothGatt.writeCharacteristic(qnWriteBgc);
        }

        break;
    }

  }

  private boolean enableNotifications(BluetoothGattCharacteristic characteristic) {

    final BluetoothGatt gatt = mBluetoothGatt;

    if (gatt == null || characteristic == null)
      return false;

    int properties = characteristic.getProperties();
    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0)
      return false;

    boolean isSuccess = gatt.setCharacteristicNotification(characteristic, true);

    final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(QNBleConst.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
    if (descriptor != null) {
      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      return gatt.writeDescriptor(descriptor);
    }

    return false;
  }

  private boolean enableIndications(BluetoothGattCharacteristic characteristic) {

    final BluetoothGatt gatt = mBluetoothGatt;

    if (gatt == null || characteristic == null)
      return false;

    int properties = characteristic.getProperties();
    if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0)
      return false;

    boolean isSuccess = gatt.setCharacteristicNotification(characteristic, true);

    final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(QNBleConst.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
    if (descriptor != null) {
      descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
      LogUtils.e("enableIndications----------" + characteristic.getUuid());
      return gatt.writeDescriptor(descriptor);
    }
    return false;
  }

  private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      super.onConnectionStateChange(gatt, status, newState);

      LogUtils.e("onConnectionStateChange: " + newState);

      if (status != BluetoothGatt.GATT_SUCCESS) {
        String err = "Cannot connect device with error status: " + status;
        // 当尝试连接失败的时候调用 disconnect 方法是不会引起这个方法回调的，所以这里直接回调就可以了
        // 当尝试连接失败的时候调用 disconnect 方法是不会引起这个方法回调的，所以这里直接回调就可以了
        gatt.close();
        if (mBluetoothGatt != null) {
          mBluetoothGatt.disconnect();
          mBluetoothGatt.close();
          mBluetoothGatt = null;
        }
        if (getBodyfat!=null)getBodyfat.err();
        LogUtils.e( err);
        return;
      }
      if (newState == BluetoothProfile.STATE_CONNECTED) {

        // TODO: 2019/9/7  某些手机可能存在无法发现服务问题,此处可做延时操作
        if (mBluetoothGatt != null) {
          SystemClock.sleep(1000);
          mBluetoothGatt.discoverServices();
          if (getBodyfat!=null)getBodyfat.succed();
        }

        LogUtils.e("onConnectionStateChange: " + "连接成功");

      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        //当设备无法连接
        if (mBluetoothGatt != null) {
          mBluetoothGatt.disconnect();
          mBluetoothGatt.close();
          mBluetoothGatt = null;
        }

        qnReadBgc = null;
        qnWriteBgc = null;
        qnBleReadBgc = null;
        qnBleWriteBgc = null;

        gatt.close();
        //TODO 实际运用中可发起重新连接
//                if (mBleDevice != null) {
//                    connectQnDevice(mBleDevice);
//                }
        if (getBodyfat!=null)getBodyfat.err();

      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      super.onServicesDiscovered(gatt, status);
      LogUtils.e("onServicesDiscovered------: " + "发现服务----" + status);

      if (status == BluetoothGatt.GATT_SUCCESS) {
        //发现服务,并遍历服务,找到公司对于的设备服务
        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services) {
          //第一套
          if (service.getUuid().equals(UUID.fromString(QNBleConst.UUID_IBT_SERVICES))) {
            if (mProtocolhandler != null) {
              //使能所有特征值
              initCharacteristic(gatt, true);
              LogUtils.e(  "onServicesDiscovered------: " + "发现服务为第一套");
              mProtocolhandler.prepare(QNBleConst.UUID_IBT_SERVICES);
            }
            break;
          }
          //第二套
          if (service.getUuid().equals(UUID.fromString(QNBleConst.UUID_IBT_SERVICES_1))) {
            if (mProtocolhandler != null) {
              //使能所有特征值
              LogUtils.e( "onServicesDiscovered------: " + "发现服务为第二套");
              initCharacteristic(gatt, false);
              mProtocolhandler.prepare(QNBleConst.UUID_IBT_SERVICES_1);
            }
            break;
          }

        }

      } else {
        LogUtils.e(  "onServicesDiscovered---error: " + status);
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      super.onCharacteristicRead(gatt, characteristic, status);
      LogUtils.e( "onCharacteristicRead---收到数据:  " + QNLogUtils.byte2hex(characteristic.getValue()));
      if (status == BluetoothGatt.GATT_SUCCESS) {
        //获取到数据
        if (mProtocolhandler != null) {
          mProtocolhandler.onGetBleData(getService(), characteristic.getUuid().toString(), characteristic.getValue());
        }
      } else {
        LogUtils.e( "onCharacteristicRead---error: " + status);
      }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);

      LogUtils.e(  "onCharacteristicChanged---收到数据:  " + QNLogUtils.byte2hex(characteristic.getValue()));
      //获取到数据
      if (mProtocolhandler != null) {
        mProtocolhandler.onGetBleData(getService(), characteristic.getUuid().toString(), characteristic.getValue());
      }

    }

  };
  private String getService() {
    if (isFirstService) {
      return QNBleConst.UUID_IBT_SERVICES;
    } else {
      return QNBleConst.UUID_IBT_SERVICES_1;
    }
  }

  private void initCharacteristic(BluetoothGatt gatt, boolean isFirstService) {

    this.isFirstService = isFirstService;

    //第一套服务
    if (isFirstService) {
      qnReadBgc = getCharacteristic(gatt, QNBleConst.UUID_IBT_SERVICES, QNBleConst.UUID_IBT_READ);
      qnWriteBgc = getCharacteristic(gatt, QNBleConst.UUID_IBT_SERVICES, QNBleConst.UUID_IBT_WRITE);
      qnBleReadBgc = getCharacteristic(gatt, QNBleConst.UUID_IBT_SERVICES, QNBleConst.UUID_IBT_BLE_READER);
      qnBleWriteBgc = getCharacteristic(gatt, QNBleConst.UUID_IBT_SERVICES, QNBleConst.UUID_IBT_BLE_WRITER);
    } else {
      qnReadBgc = getCharacteristic(gatt, QNBleConst.UUID_IBT_SERVICES_1, QNBleConst.UUID_IBT_READ_1);
      qnWriteBgc = getCharacteristic(gatt, QNBleConst.UUID_IBT_SERVICES_1, QNBleConst.UUID_IBT_WRITE_1);
    }

    enableNotifications(qnReadBgc);
    enableIndications(qnBleReadBgc);
  }
  private BluetoothGattCharacteristic getCharacteristic(final BluetoothGatt gatt, String serviceUuid, String characteristicUuid) {
    BluetoothGattService service = gatt.getService(UUID.fromString(serviceUuid));
    if (service == null) {
      return null;
    }
    return service.getCharacteristic(UUID.fromString(characteristicUuid));
  }

}
