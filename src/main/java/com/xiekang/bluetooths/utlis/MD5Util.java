package com.xiekang.bluetooths.utlis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 接口加密
 *
 * @author hu
 */
public class MD5Util {

  private static String byteArrayToHexString(byte b[]) {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++)
      resultSb.append(byteToHexString(b[i]));

    return resultSb.toString();
  }

  private static String byteToHexString(byte b) {
    int n = b;
    if (n < 0)
      n += 256;
    int d1 = n / 16;
    int d2 = n % 16;
    return hexDigits[d1] + hexDigits[d2];
  }

  public static String MD5Encode(String origin, String charsetname) {
    String resultString = null;
    try {
      resultString = new String(origin);
      MessageDigest md = MessageDigest.getInstance("MD5");
      if (charsetname == null || "".equals(charsetname))
        resultString = byteArrayToHexString(md.digest(resultString
            .getBytes()));
      else
        resultString = byteArrayToHexString(md.digest(resultString
            .getBytes(charsetname)));
    } catch (Exception exception) {
    }
    return resultString;
  }

  private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
      "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

  /**
   * @param origin       加密内容
   * @param encodingType 加密格式  UTF-8
   * @return MD5加密，返回32位小写
   */
  public static String Md5UpperCase(String origin, String encodingType) {
    System.out.println(origin);
    byte[] md5Bytes = toMd5(origin, encodingType);
    System.out.println(byteArrayToHexString(md5Bytes).toString());
    return byteArrayToHexString(md5Bytes).toString();
  }

  /**
   * @param origin       加密内容
   * @param encodingType 加密格式  UTF-8
   * @return MD5加密，返回32位大写
   */
  public static String Md5UpperCases(String origin, String encodingType) {
    System.out.println(origin);
    byte[] md5Bytes = toMd5(origin, encodingType);
    System.out.println(byteArrayToHexString(md5Bytes).toUpperCase());
    return byteArrayToHexString(md5Bytes).toUpperCase();
  }

  public static byte[] toMd5(String data, String encodingType) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException nsae) {
      System.err.println("Failed to load the MD5 MessageDigest. ");
      nsae.printStackTrace();
    }
    try {
      //--最重要的是这句,需要加上编码类型
      if (digest != null) digest.update(data.getBytes(encodingType));
    } catch (UnsupportedEncodingException e) {
      if (digest != null) digest.update(data.getBytes());
    }
    return digest.digest();
  }

  public static String GetMD5Code(String info) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(info.getBytes("UTF-8"));
      byte[] encryption = md5.digest();

      StringBuffer strBuf = new StringBuffer();
      for (int i = 0; i < encryption.length; i++) {
        if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
          strBuf.append("0").append(
              Integer.toHexString(0xff & encryption[i]));
        } else {
          strBuf.append(Integer.toHexString(0xff & encryption[i]));
        }
      }

      return strBuf.toString();
    } catch (NoSuchAlgorithmException e) {
      return "";
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

  //16位大写
  public static String Md5(String sourceStr) {
    String result = "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(sourceStr.getBytes("UTF-8"));
      byte b[] = md.digest();
      int i;
      StringBuffer buf = new StringBuffer("");
      for (int offset = 0; offset < b.length; offset++) {
        i = b[offset];
        if (i < 0)
          i += 256;
        if (i < 16)
          buf.append("0");
        buf.append(Integer.toHexString(i));
      }
      result = buf.toString();
      System.out.println("result: " + result);//32位的加密
//   System.out.println("result: " + buf.toString().substring(8,24));//16位的加密
    } catch (NoSuchAlgorithmException e) {
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result.substring(8, 24).toUpperCase();
  }
}
