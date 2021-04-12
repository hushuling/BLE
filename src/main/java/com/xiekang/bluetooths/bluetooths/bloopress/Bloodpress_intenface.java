package com.xiekang.bluetooths.bluetooths.bloopress;

import com.xiekang.bluetooths.interfaces.Bluetooth_Satus;

/**
 * @项目名称 MedicalFollowUp
 * @类名 name：com.xiekang.medicalfollowup.fragment.detection.bluetooths.bloopress
 * @类描述 describe
 * @创建人 hsl20
 * @创建时间 2020-03-12 10:18
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface Bloodpress_intenface<T> extends Bluetooth_Satus {
//  实时数据
  void current(int date);
  void getDate(T uiData);
}
