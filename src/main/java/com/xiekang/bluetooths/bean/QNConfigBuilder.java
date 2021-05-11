package com.xiekang.bluetooths.bean;

import java.io.Serializable;

/**
 * @项目名称 SimpleOneMachine
 * @类名 name：com.hoho.android.usbserial
 * @类描述 轻牛体脂称测量之前的基础配置
 * @创建人 hsl20
 * @创建时间 2020-06-09 11:13
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class QNConfigBuilder implements Serializable {
//  远程设备提供的配对号
  byte[] scanRecord ;
  //userId - 用户标识，用户唯一，传非空的字符串，可以使用 用户名，手机号，邮箱等其它标识
  String userId;
  //height - 身高，单位cm
  int height;
  //gender - 性别 男：1 女：0
  int gender;
  //birthday - 生日，精确到天 格式："yyyy-M-d"
  String birthday;

  public byte[] getScanRecord() {
    return scanRecord;
  }

  public QNConfigBuilder setScanRecord(byte[] scanRecord) {
    this.scanRecord = scanRecord;
    return this;
  }

  public String getUserId() {
    return userId;
  }

  public QNConfigBuilder setUserId(String userId) {
    this.userId = userId;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public QNConfigBuilder setHeight(int height) {
    this.height = height;
    return this;
  }

  public int getGender() {
    return gender;
  }

  public QNConfigBuilder setGender(int gender) {
    this.gender = gender;
    return this;
  }

  public String getBirthday() {
    return birthday;
  }

  public QNConfigBuilder setBirthday(String birthday) {
    this.birthday = birthday;
    return this;
  }

  public QNConfigBuilder build() {
    return this;
  }
}
