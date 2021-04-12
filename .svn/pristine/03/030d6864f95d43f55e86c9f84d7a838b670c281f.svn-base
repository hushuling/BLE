package com.xiekang.bluetooths.bluetooths;


import com.xiekang.bluetooths.bean.NewUIData;
import com.xiekang.bluetooths.utlis.LogUtils;

import java.util.Vector;

/**
 * @项目名称 HealthMachine1.4.6
 * @类名 name：com.xiekang.healthmachine.fargment.detection.bluetooths
 * @类描述 康泰尿液解析
 * @创建人 hsl20
 * @创建时间 2018/7/26 10:49
 * @修改人
 * @修改时间 time
 * @修改备注 describe
 */
public class Verifier {
  private static byte HEAD1 = -109;
  private static byte HEAD2 = -114;
  private static Verifier verifier;
  private static Vector<Byte> mRecvBuffer = new Vector();

  public static synchronized Verifier getInstance() {
    if (verifier == null) {
      verifier = new Verifier();
    }

    return verifier;
  }

  public int checkIntactCnt(Vector<Byte> buffer) {
    int cnt = 0;
    int begin = 0;

    try {
      while (begin < buffer.size() - 6) {
        if (((Byte) buffer.get(begin)).byteValue() == HEAD1 && ((Byte) buffer.get(begin + 1)).byteValue() == HEAD2) {
          if (((Byte) buffer.get(5)).byteValue() == 5) {
            return 1;
          }

          int e = ((Byte) buffer.get(begin + 2)).byteValue() & 255;
          int checkSumPosi = begin + 2 + 4 + e - 4;
          if (buffer.size() <= checkSumPosi) {
            return cnt;
          }

          try {
            if (this.checkSum(buffer, begin + 2, checkSumPosi)) {
              begin = checkSumPosi + 1;
              ++cnt;
            } else {
              buffer.remove(begin);
            }
          } catch (Exception var7) {
            var7.printStackTrace();
            buffer.remove(begin);
          }
        } else {
          buffer.remove(0);
        }
      }
    } catch (Exception var8) {
      var8.printStackTrace();
    }

    return cnt;
  }

  public boolean checkSum(Vector<Byte> buffer, int begin, int end) {
    byte total = 0;

    for (int i = begin; i < end; ++i) {
      total += ((Byte) buffer.get(i)).byteValue();
    }

    return total == ((Byte) buffer.get(end)).byteValue();
  }

  public NewUIData analyse() throws Exception {
    int cnt = checkIntactCnt(mRecvBuffer);
    NewUIData uiData = null;
    boolean len = false;
    boolean type = false;

    for (int i = 0; i < cnt; ++i) {
      mRecvBuffer.remove(0);
      mRecvBuffer.remove(0);
      int var18 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      mRecvBuffer.remove(0);
      mRecvBuffer.remove(0);
      byte var19 = ((Byte) mRecvBuffer.remove(0)).byteValue();
      int paramLen;
      byte packIndex;
      switch (var19) {
        case 0:
          paramLen = ((Byte) mRecvBuffer.remove(0)).byteValue() & 128;
          break;
        case 4:
          uiData = getOneRecord();
          LogUtils.e("得到数据" + uiData + "***");
          break;
      }
    }
    return uiData;
  }

  private NewUIData getOneRecord() {
    boolean temp1 = false;
    boolean temp2 = false;
    NewUIData uiData = null;
    int id = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
    int user = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
    id += (user & 3) << 8;
    user = user >>> 2 & 31;
    int temp11 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
    if (temp11 == 255) {
      return uiData;
    } else {
      int year = (temp11 & 127) + 2000;
      int temp21 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int mon = (temp11 >>> 7 & 1) + ((temp21 & 7) << 1);
      int day = temp21 >>> 3 & 31;
      temp11 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int hour = temp11 & 31;
      int min = ((Byte) mRecvBuffer.remove(0)).byteValue() & 7;
      min = (min << 3) + (temp11 >>> 5 & 7);
      int sec = ((Byte) mRecvBuffer.remove(0)).byteValue() & 127;
      String strMon = null;
      if (mon < 10) {
        strMon = "0" + mon;
      } else {
        strMon = String.valueOf(mon);
      }

      String strDay = null;
      if (day < 10) {
        strDay = "0" + day;
      } else {
        strDay = String.valueOf(day);
      }

      String strHour = null;
      if (hour < 10) {
        strHour = "0" + hour;
      } else {
        strHour = String.valueOf(hour);
      }

      String strMin = null;
      if (min < 10) {
        strMin = "0" + min;
      } else {
        strMin = String.valueOf(min);
      }

      String strSec = null;
      if (sec < 10) {
        strSec = "0" + sec;
      } else {
        strSec = String.valueOf(sec);
      }

      String time = year + "-" + strMon + "-" + strDay + " " + strHour + ":" + strMin + ":" + strSec;
      mRecvBuffer.remove(0);
      byte item = ((Byte) mRecvBuffer.remove(0)).byteValue();
      temp11 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int item1 = (temp11 << 8 & 7) + item;
      int uro = temp11 >>> 3 & 7;
      temp11 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int bld = temp11 & 7;
      int bil = temp11 >>> 3 & 7;
      temp21 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int ket = (temp11 >>> 6 & 3) + ((temp21 & 1) << 2);
      int glu = temp21 >>> 1 & 7;
      int pro = temp21 >>> 4 & 7;
      temp11 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int ph = temp11 & 7;
      int nit = temp11 >>> 3 & 7;
      temp21 = ((Byte) mRecvBuffer.remove(0)).byteValue() & 255;
      int leu = (temp11 >>> 6 & 3) + ((temp21 & 1) << 2);
      int sg = temp21 >>> 1 & 7;
      int vc = temp21 >>> 4 & 7;
      String[] LEU = new String[]{"-", "+-", "1+", "2+", "3+", "4+"};
      String[] BIL = new String[]{"-", "1+", "2+", "3+"};
      String[] NIT = new String[]{"-", "+"};
      String[] PH = new String[]{"5", "6", "7", "8", "9"};
      String[] SG = new String[]{"1.005", "1.010", "1.015", "1.020", "1.025", "1.030"};
      uiData = new NewUIData(id, user, time, item1, BIL[uro], LEU[bld], BIL[bil], BIL[ket], LEU[glu], LEU[pro], PH[ph], NIT[nit], LEU[leu], SG[sg], LEU[vc],null,null,null);
      return uiData;
    }
  }

  public void addByte(Byte aByte) {
    mRecvBuffer.add(aByte);
  }

  public Vector<Byte> getByte() {
    return mRecvBuffer;
  }

  public void clear() {
    mRecvBuffer.clear();
  }
}
