package com.xiekang.bluetooths.interfaces;

import com.breathhome_ble_sdk.bean.PefDataFromBleBean;

/**
 * 呼吸家的接口
 */
public interface GetBreath extends Bluetooth_Satus {
    void sendDataFromBleDevice(String fvc,String fev,String pef);
}