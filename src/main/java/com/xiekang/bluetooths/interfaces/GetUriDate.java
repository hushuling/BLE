package com.xiekang.bluetooths.interfaces;

/**
 * 获取尿液的接口
 * @param <T>
 */
public interface GetUriDate<T> extends Bluetooth_Satus {
  /**
   *
   * @param uiData 尿液数据
   */
  void getUri(T uiData);

}
