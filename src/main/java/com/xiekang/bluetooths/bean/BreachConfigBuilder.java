package com.xiekang.bluetooths.bean;

import java.io.Serializable;

/**
 * @项目名称 SimpleOneMachine
 * @类名 name：com.hoho.android.usbserial
 * @类描述 呼吸家测量之前的基础配置
 * @创建人 hsl20
 * @创建时间 2020-06-09 11:13
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BreachConfigBuilder implements Serializable {
//  变动的蓝牙名称
   String IMEI ;
  //height - 身高，单位cm
  int height;
  int weight;
  //gender - 性别 男：1 女：0
  int gender;
  //birthday - 生日，精确到天 格式："yyyy-M-d"
  String birthday;

  public String getIMEI() {
    return IMEI;
  }

  public BreachConfigBuilder setIMEI(String IMEI) {
    this.IMEI = IMEI;
    return this;
  }

  public int getWeight() {
    return weight;
  }

  public BreachConfigBuilder setWeight(int weight) {
    this.weight = weight;
    return this;
  }



  public int getHeight() {
    return height;
  }

  public BreachConfigBuilder setHeight(int height) {
    this.height = height;
    return this;
  }

  public int getGender() {
    return gender;
  }

  public BreachConfigBuilder setGender(int gender) {
    this.gender = gender;
    return this;
  }

  public String getBirthday() {
    return birthday;
  }

  public BreachConfigBuilder setBirthday(String birthday) {
    this.birthday = birthday;
    return this;
  }

  public BreachConfigBuilder build() {
    return this;
  }
}
