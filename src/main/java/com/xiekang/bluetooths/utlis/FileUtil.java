package com.xiekang.bluetooths.utlis;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


/**
 * 文件操作类
 * 
 * @author xw
 * 
 */
public class FileUtil {

	private static File cacheDir;
	private static File imageDir;


	/**
	 * 得到本地版本号
	 * @return
	 */
	public static int getLocalVersionNo( Context mContext) {

		try {
			PackageInfo packageInfo = mContext
					.getPackageManager().getPackageInfo(
						mContext.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 获取应用目录路径
	 * 
	 * @return
	 */
	public static String getSaveDir() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory() + Cotexts.getAppDir();
		}
		return Environment.getDataDirectory().getAbsolutePath()
				+ Cotexts.getAppDir();
	}

	/**
	 * 是否存在SD卡
	 * 
	 * @return
	 */
	public static boolean isSDCardExist() {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

  return sdCardExist;
	}

	/**
	 * 读取表情配置文件 图片资源全称+","+[描述]
	 * @return
	 */
	public static List<String> getEmojiFile(Context mContext) {
		try {

			List<String> list = new ArrayList<String>();

			InputStream in = mContext.getResources()
					.getAssets().open("emoji");

			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));

			String str = null;

			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取文件的MD5值
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileMD5(File file) {

		if (!file.isFile()) {
			return null;
		}

		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}



	/**
	 * 写入日志
	 */
	public static void writeLog(String log, String fileName, boolean bl) {
		try {
			String time = DateUtil.getDate();
			log = time + ":" + log + "\r\n";
			if (isSDCardExist()) {
				File dir = new File(getSaveDir());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(getSaveDir()
						+ fileName, bl);
				fos.write(log.getBytes());
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建文件夹
	 */
	public static void CreaterFile(String path) {
		File dir = new File(getSaveDir()+path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	//创建文件
	public static File createFile(String path, String fileName) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(path + fileName);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}
	/**
	 * 写入日志
	 */
	public static void writeLog(String log, String fileName) {
		writeLog(log, fileName, true);
	}

	public static void writeLog(String log) {
		writeLog(log, "/login.txt");
	}

 public static void delete(File path, String filename) {
		if (path.exists()){
     for(File file: path.listFiles()){
     	if (file.getName().contains(filename)){
     		file.delete();
						}
					}
		}
 }



	/**判断传入的文件夹是否存在,不存在则创建,存在则改变为可读可写*/
	public static void judjePath(String filePath) {
		File file = new File(filePath);
		if(!file.exists()){
			LogUtils.e("", " 不存在,创建");
			file.mkdirs();
			file.setWritable(true);
			file.setReadable(true);
		}else {
			LogUtils.e("TAG", "存在");
			if (file.canRead() && file.canWrite() && file.canExecute()) {
				LogUtils.e("TAG", "读写权限正常");
			} else {
				file.setWritable(true);
				file.setReadable(true);
			}
		}
	}

	//判断一个文件是否存在,不存在就创建
	public static void IsFileExite(String file) {
		File file_1 = new File(file);
		if (!file_1.exists()) {
			try {
				file_1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtils.e("TAG", "onPreExecute: Excel创建失败");
			}
		}
	}

	//获取资源文件中json数据
	public static JSONArray getJsonArraylist(String fileurl, String code) {
		JSONArray text = null;
		try {
			InputStream is = ContextProvider.get().getContext().getAssets().open(fileurl);//此处为要加载的json文件名称
			String textFromSDcard = readTextFromSDcard(is, code);
			text = new JSONArray(textFromSDcard);
		} catch (Exception e) {
			LogUtils.e("readFromAssets", e.toString());
		}
		return text;
	}

	//将传入的is一行一行解析读取出来出来
	private static String readTextFromSDcard(InputStream is, String code) throws Exception {
		InputStreamReader reader = new InputStreamReader(is, code);
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuffer buffer = new StringBuffer("");
		String str;
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
			buffer.append("\n");
		}
		return buffer.toString();//把读取的数据返回
	}

	//删除心电数据&人脸图片
	public static void DeleteEcg_face(String path, String filename) {
		File file = new File(getSaveDir() + path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().contains(filename)) {
						files[i].delete();
					}
				}
			}
			// 是目录且为空
			if (file.isDirectory() && file.listFiles().length <= 0) {
				file.delete();
			}
		}
	}

	/**
	 * 追加文件：使用RandomAccessFile
	 *
	 * @param fileName 文件名
	 * @param content  追加的内容
	 */
	public static void writeDataToFile(String fileName, String content) {
		RandomAccessFile randomFile = null;
		try {
			// 打开一个随机访问文件流，按读写方式
			randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件读取
	 *
	 * @param
	 * @return 返回文件数组
	 */
	public static byte[] read(File file) {
		//缓冲区inputStream
		BufferedInputStream bis = null;
		//用于存储数据
		ByteArrayOutputStream baos = null;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);

			//每次读1024
			byte[] b = new byte[1024];
			//初始化
			bis = new BufferedInputStream(fis);
			baos = new ByteArrayOutputStream();

			int length;
			while ((length = bis.read(b)) != -1) {
				//bis.read()会将读到的数据添加到b数组
				//将数组写入到baos中
				baos.write(b, 0, length);
			}
			return baos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {//关闭流
			try {
				if (bis != null) {
					bis.close();
				}
				if (fis != null) {
					fis.close();
				}

				if (baos != null) baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
