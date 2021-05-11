package com.xiekang.bluetooths.utlis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileService {

  /**
   * 删除文件
   *
   * @param file 文件
   * @return {@code true}: 删除成功<br>{@code false}: 删除失败
   */
  public static boolean deleteFile(File file) {
    return file != null && (!file.exists() || file.isFile() && file.delete());
  }

  /**
   * 将字符串写入文件
   *
   * @param file    文件
   * @param content 写入内容
   * @param append  是否追加在文件末
   * @return {@code true}: 写入成功<br>{@code false}: 写入失败
   */
  public static boolean writeFileFromString(File file, String content, boolean append) {
    if (file == null || content == null) return false;
    if (!createOrExistsFile(file)) return false;
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(file, append));
      bw.write(content);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      closeIO(bw);
    }
  }

  /**
   * 将字节数组写入图片文件
   *
   * @param file    文件
   * @param content 写入内容
   * @param append  是否追加在文件末
   * @return {@code true}: 写入成功<br>{@code false}: 写入失败
   */
  public static boolean writeImagerFrombyte(File file, byte[] content, boolean append) {
    if (file == null || content == null) return false;
    if (!createOrExistsFile(file)) return false;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      fos.write(content, 0, content.length);
      fos.flush();
      fos.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      closeIO(fos);
    }
  }

  /**
   * 关闭IO
   *
   * @param closeables closeables
   */
  public static void closeIO(Closeable... closeables) {
    if (closeables == null) return;
    for (Closeable closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 判断目录是否存在，不存在则判断是否创建成功
   *
   * @param file 文件
   * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
   */
  public static boolean createOrExistsDir(File file) {
    // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
    return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
  }

  /**
   * 判断文件是否存在，不存在则判断是否创建成功
   *
   * @param file 文件
   * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
   */
  public static boolean createOrExistsFile(File file) {
    if (file == null) return false;
    // 如果存在，是文件则返回true，是目录则返回false
    if (file.exists()) return file.isFile();
    if (!createOrExistsDir(file.getParentFile())) return false;
    try {
      return file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 指定编码按行读取文件到字符串中
   *
   * @param file        文件
   * @param charsetName 编码格式
   * @return 字符串
   */
  public static String readFile2String(File file, String charsetName) {
    if (file == null) return null;
    BufferedReader reader = null;
    try {
      StringBuilder sb = new StringBuilder();
      if (isSpace(charsetName)) {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      } else {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
      }
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\r\n");// windows系统换行为\r\n，Linux为\n
      }
      // 要去除最后的换行符
      return sb.delete(sb.length() - 2, sb.length()).toString();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      closeIO(reader);
    }
  }

  /**
   * 判断字符串是否为null或全为空白字符
   *
   * @param s 待校验字符串
   * @return {@code true}: null或全空白字符<br> {@code false}: 不为null且不全空白字符
   */
  public static boolean isSpace(String s) {
    if (s == null) return true;
    for (int i = 0, len = s.length(); i < len; ++i) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}