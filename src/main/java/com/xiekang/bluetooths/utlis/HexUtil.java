package com.xiekang.bluetooths.utlis;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description: 十六进制转换类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/8/7 21:57.
 */
public class HexUtil {
  /**
   * 用于建立十六进制字符的输出的小写字符数组
   */
  private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  /**
   * 用于建立十六进制字符的输出的大写字符数组
   */
  private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  /**
   * 将字节数组转换为十六进制字符数组
   *
   * @param data byte[]
   * @return 十六进制char[]
   */
  public static char[] encodeHex(byte[] data) {
    return encodeHex(data, true);
  }

  /**
   * 将字节数组转换为十六进制字符数组
   *
   * @param data        byte[]
   * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
   * @return 十六进制char[]
   */
  public static char[] encodeHex(byte[] data, boolean toLowerCase) {
    return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
  }
  private  static final byte[] hex = "0123456789ABCDEF".getBytes();
  // 从字节数组到十六进制字符串转
  public static String Bytes2HexString(byte[] b) {
    byte[] buff = new byte[2 * b.length];
    for (int i = 0; i < b.length; i++) {
      buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
      buff[2 * i + 1] = hex[b[i] & 0x0f];
    }
    return new String(buff);
  }
  /**
   * 字节数组转为普通字符串（ASCII对应的字符）
   *
   * @param bytearray byte[]
   * @return String
   */
  public static String bytetoString(byte[] bytearray) {
    String result = "";
    char temp;

    int length = bytearray.length;
    for (int i = 0; i < length; i++) {
      temp = (char) bytearray[i];
      result += temp;
    }
    return result;
  }

  /**
   * 将字符串转为16进制
   * @param str
   * @return
   */
  public static String str2HexStr(String str) {
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    byte[] bs = str.getBytes();
    int bit;
    for (int i = 0; i < bs.length; i++) {
      bit = (bs[i] & 0x0f0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0x0f;
      sb.append(chars[bit]);
      // sb.append(' ');
    }
    return sb.toString().trim();
  }


  /**
   * 将字节数组转换为十六进制字符数组
   *
   * @param data     byte[]
   * @param toDigits 用于控制输出的char[]
   * @return 十六进制char[]
   */
  protected static char[] encodeHex(byte[] data, char[] toDigits) {
    int l = data.length;
    char[] out = new char[l << 1];
    // two characters form the hex value.
    for (int i = 0, j = 0; i < l; i++) {
      out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
      out[j++] = toDigits[0x0F & data[i]];
    }
    return out;
  }

  /**
   * 将字节数组转换为十六进制字符串
   *
   * @param data byte[]
   * @return 十六进制String
   */
  public static String encodeHexStr(byte[] data) {
    return encodeHexStr(data, true);
  }

  /**
   * 将字节数组转换为十六进制字符串
   *
   * @param data        byte[]
   * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
   * @return 十六进制String
   */
  public static String encodeHexStr(byte[] data, boolean toLowerCase) {
    return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
  }

  /**
   * 将字节数组转换为十六进制字符串
   *
   * @param data     byte[]
   * @param toDigits 用于控制输出的char[]
   * @return 十六进制String
   */
  protected static String encodeHexStr(byte[] data, char[] toDigits) {
    return new String(encodeHex(data, toDigits));
  }

  /**
   * 将十六进制字符数组转换为字节数组
   *
   * @param data 十六进制char[]
   * @return byte[]
   * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
   */
  public static byte[] decodeHex(char[] data) {

    int len = data.length;

    if ((len & 0x01) != 0) {
      throw new RuntimeException("Odd number of characters.");
    }

    byte[] out = new byte[len >> 1];

    // two characters form the hex value.
    for (int i = 0, j = 0; j < len; i++) {
      int f = toDigit(data[j], j) << 4;
      j++;
      f = f | toDigit(data[j], j);
      j++;
      out[i] = (byte) (f & 0xFF);
    }

    return out;
  }

  /**
   * 将十六进制字符转换成一个整数
   *
   * @param ch    十六进制char
   * @param index 十六进制字符在字符数组中的位置
   * @return 一个整数
   * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
   */
  protected static int toDigit(char ch, int index) {
    int digit = Character.digit(ch, 16);
    if (digit == -1) {
      throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
    }
    return digit;
  }

  /**
   * 16进制字符串转换为字符串
   *
   * @param s
   * @return
   */
  public static String hexStringToString(String s) {
    if (s == null || s.equals("")) {
      return null;
    }
    s = s.replace(" ", "");
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      try {
        baKeyword[i] = (byte) (0xff & Integer.parseInt(
            s.substring(i * 2, i * 2 + 2), 16));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      s = new String(baKeyword, "gbk");
      new String();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return s;
  }
  //将十六进制string转成byte数组

  public static byte[] hexStringToBytes(String hexString) {

    if (hexString == null || hexString.equals("")) {

      return null;

    }

    hexString = hexString.toUpperCase();

    int length = hexString.length() / 2;

    char[] hexChars = hexString.toCharArray();

    byte[] d = new byte[length];

    for (int i = 0; i < length; i++) {

      int pos = i * 2;

      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

    }

    return d;

  }
  public static String deal16to10(String str) {
    String rightstr = str.substring(0, 1);
    String leftstr = str.substring(1, 2);
    String r = String.valueOf(Integer.valueOf(get10v(rightstr)) * 16 * 1 + Integer.valueOf(get10v(leftstr)) * 1);
    if (Integer.valueOf(r) < 10) {
      return "0" + r;
    } else {
      return r;
    }
  }
  private static int get10v(String str) {
    str = str.toLowerCase();
    if ("a".equals(str)) {
      return 10;
    } else if ("b".equals(str)) {
      return 11;
    } else if ("c".equals(str)) {
      return 12;
    } else if ("d".equals(str)) {
      return 13;
    } else if ("e".equals(str)) {
      return 14;
    } else if ("f".equals(str)) {
      return 15;
    } else {
      return Integer.valueOf(str);
    }
  }

  private static byte charToByte(char c) {

    return (byte) "0123456789ABCDEF".indexOf(c);

  }

  public static void main(String arg[]) {
    String log=(0xCC^0x80^0x03^0x03^0x01^0x02^0x00)+"";
    byte[] cmd = new byte[]{
        (byte)0x03, (byte) 0x03,
        (byte) 0x01, (byte) 0x03,
        (byte) 0x00, };
//    System.out.println(encodeHexStr(cmd));
//    String crc16 = Integer.toHexString(FindCRC(cmd));//把10进制的结果转化为16进制
//    System.out.println(crc16);
//
//  System.out.println(HexUtil.deal16to10("83"));
    String weight="#SMW10653$#SMW10653$";
    String[] bytr=  weight.split("\\$");

    System.out.println("#SMW10131$".substring(0,2));
    System.out.println("19931011".substring(0,4));
  }
  public static int FindCRC(byte[] data){
    int CRC=0;
    for(int i=0;i<data.length; i++){
      CRC ^= data[i];
    }
    CRC &= 0xff;//保证CRC余码输出为2字节。
    return CRC;
  }

  //将指定byte数组以16进制的形式打印到控制台
  public static String printHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder();

    if (src == null || src.length <= 0) {

      return null;

    }

    for (int i = 0; i < src.length; i++) {

      int v = src[i] & 0xFF;

      String hv = Integer.toHexString(v);

      if (hv.length() < 2) {

        stringBuilder.append(0);

      }

      stringBuilder.append(hv);

    }

    return stringBuilder.toString().toUpperCase();

  }

  //保留两位小数&&四舍五入
  public static float formatfloat(float d) {
    BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
    return (float) bg.doubleValue();
  }
}
