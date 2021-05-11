package com.xiekang.bluetooths.bean;

import android.text.TextUtils;


import java.io.Serializable;

/**
 * java 实体类
 * 身份证
 */
public class IdCardInfo implements Serializable {

  //姓名
  public String name;
  //身份证
  public String cardNO;
  //性别
  public String sexs;
  //签发机构
  public String dep;
  //有效期
  public String lifestart;
  //有效期结束
  public String lifeend;
  //民族
  public String nation;
  //生日
  public String birthday;

  @Override
  public String toString() {
    return "IdCardInfo{" +
        "name='" + name + '\'' +
        ", cardNO='" + cardNO + '\'' +
        ", sexs=" + sexs +
        ", dep='" + dep + '\'' +
        ", lifestart='" + lifestart + '\'' +
        ", lifeend='" + lifeend + '\'' +
        ", nation='" + nation + '\'' +
        ", birthday='" + birthday + '\'' +
        ", address='" + address + '\'' +
        '}';
  }

  public IdCardInfo() {
  }

  //地址
  public String address;

  public IdCardInfo(String name, String cardNO, String sexs, String dep, String lifestart, String lifeend, String nation, String birthday, String address) {
    this.name = name;
    this.cardNO = cardNO;
    this.sexs = sexs;
    this.dep = dep;
    this.lifestart = lifestart;
    this.lifeend = lifeend;
    this.nation = nation;
    this.birthday = birthday;
    this.address = address;
  }
}
