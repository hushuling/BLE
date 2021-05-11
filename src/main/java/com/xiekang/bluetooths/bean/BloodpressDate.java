package com.xiekang.bluetooths.bean;

/**
 * @项目名称 bluetooths
 * @类名 name：com.xiekang.bluetooths.bean
 * @类描述 describe
 * @创建人 hsl20
 * @创建时间 2021-04-26 10:11
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class BloodpressDate {
  String dbp;
  String sbp;
  String rhp;

  @Override
  public String toString() {
    return "Info{" +
        "dbp='" + dbp + '\'' +
        ", sbp='" + sbp + '\'' +
        ", rhp='" + rhp + '\'' +
        '}';
  }

  public String getDbp() {
    return dbp;
  }

  public String getSbp() {
    return sbp;
  }

  public String getRhp() {
    return rhp;
  }

  public BloodpressDate(String sbp, String rhp, String dbp) {
    this.dbp = dbp;
    this.sbp = sbp;
    this.rhp = rhp;
  }
}
