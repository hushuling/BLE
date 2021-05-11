package com.xiekang.bluetooths.interfaces;

import com.xiekang.bluetooths.bean.IdCardInfo;

/**
 * @项目名称 bluetooths
 * @类名 name：com.xiekang.bluetooths.interfaces
 * @类描述 describe
 * @创建人 hsl20
 * @创建时间 2021-04-26 11:18
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public interface GetIDcar extends Bluetooth_Satus {
 /**
  * @param info
  */
 void getIdCardInfo(IdCardInfo info);
}
