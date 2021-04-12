package com.xiekang.bluetooths.utlis;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 处理时间 字符串 之间的互转
 *
 * @author hu
 *
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

  public static DateFormat mFormat;
  /**
   * 日期类型 *
   */
  public static final String yyyyMMDD = "yyyy-MM-dd";
  public static final String yyyyMMddHHmm2 = "yyyy-MM-dd HH:mm";
  public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
  public static final String yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";
  public static final String HHmmss = "HH:mm:ss";
  public static final String hhmmss = "HH:mm:ss";
  public static final String LOCALE_DATE_FORMAT = "yyyy年M月d日 EEEE";
  public static final String DB_DATA_FORMAT = "yyyy-MM-dd  HH:mm";
  public static final String NEWS_ITEM_DATE_FORMAT = "hh:mm M月d日 yyyy";
  public static final String MMDD = "M月d日";
  public static final String YYYMMDD = "yyy年M月d日";
  public static final String YYYMMDDHHmmss = "yyyy年M月d日 HH:mm:ss";
  public static final String yyyy = "yyyy/MM/dd";
  public static final String yyyyHHMMSS = "yyyy/MM/dd HH:mm";

  public static final String MMdd = "MM/dd";
  public static final String YYYY = "yyyy";
  public static final String MMddHHmm = "MM/dd HH:mm";
  public static final String yyyyMMDD2 = "yyyyMMdd";
  public static final String yyyyMMddHHmm = "yyyyMMddHHmm";
  public static final String yyyyMMddHHmmsss = "yyyyMMddHHmmss";
  /**
   * 判断时间格式 格式必须为“yyyy年M月d日”
   * 2004-2-30 是无效的
   * 2003-2-29 是无效的
   * @param
   * @return
   */
  public static boolean isValidDate(String str) {
    boolean convertSuccess=true;
    // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
    SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日");
    try {
      // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
      format.setLenient(false);
      format.parse(str);
    } catch (ParseException e) {
      // e.printStackTrace();
// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
      convertSuccess=false;
    }
    return convertSuccess;
  }

  /**
   * 将Date类型转换为日期字符串
   *
   * @param date Date对象
   * @param type 需要的日期格式
   * @return 按照需求格式的日期字符串
   */
  public static String formatDate(Date date, String type) {
    try {
      SimpleDateFormat df = new SimpleDateFormat(type);
      return df.format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  /**
   * 获取固定格式时间 yyyy/mm/hh
   * @param date
   * @return
   */
  public static String getDataFormats(Date date){
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH) + 1)+"/"+c.get(Calendar.DAY_OF_MONTH);
  }
  /**
   * yyyy-MM-dd hh:mm:ss
   *
   * 获取当前时间
   *
   * @return 当前时间
   *
   */
  public static String getDate() {
    mFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.MEDIUM, Locale.getDefault());
    return mFormat.format(new Date());
  }
  /**
   * yyyy-MM-dd
   *
   * 获取当前时间
   *
   * @return 当前时间
   *
   */
  public static String getDate2() {
    mFormat = DateFormat.getDateInstance(DateFormat.MEDIUM,
        Locale.getDefault());
    return mFormat.format(new Date());
  }
  /***
   * 获取当期系统时间的年 yyyy
   */
  public static String getyaer() {
    SimpleDateFormat day= new SimpleDateFormat("yyyy");
    String date=day.format(new Date());
    return date;
  }
  /***
   * 获取当期系统时间的年 yyyy
   */
  public static String getHour() {
    SimpleDateFormat day= new SimpleDateFormat("HH");
    String date=day.format(new Date());
    return date;
  }
  public static String getMimnter() {
    SimpleDateFormat day = new SimpleDateFormat("mm");
    String date = day.format(new Date());
    return date;
  }
  public static String getDateofMonth() {
    SimpleDateFormat day = new SimpleDateFormat("dd");
    String date = day.format(new Date());
    return date;
  }
  public static String getMonth_year() {
    SimpleDateFormat day = new SimpleDateFormat("MM");
    String date = day.format(new Date());
    return date;
  }
  /**
   * 格式化时间显示格式（例2011 - 11 -12 12：20）
   *
   * @param time
   * @return
   */
  public static String dateToString1(Date time) {
    SimpleDateFormat formatter;
    formatter = new SimpleDateFormat("yyyy - MM - dd   HH:mm");
    String ctime = formatter.format(time);

    return ctime;
  }

  /**
   * 格式化后的时间字符串转成loog类型的毫秒值
   * @throws ParseException
   */
  public static long stringTolongtime(String time , String format)  {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    long times = 0;// 毫秒
    try {
      times = sdf.parse(time).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return times;
  }
  /**
   * 格式化后的时间字符串转成loog类型的毫秒值
   * @throws ParseException
   */
  public static long stringTolongtime(String time)  {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.yyyyMMDD);
    long times = 0;// 毫秒
    try {
      times = sdf.parse(time).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return times;
  }

  /**
   * 根据格式 获取当前时间
   *
   * @return 当前时间
   * @author PanFeng
   * */
  public static String getDate(String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(new Date());
  }


  /**
   * 把后台返回数据库格式时间，转化为对应格式的时间
   * @param oldDate
   * @param format
   * @return
   */
  public static String dealDateFormat(String oldDate , String format) throws ParseException {
    //此格式只有  jdk 1.7才支持  yyyy-MM-dd‘T‘HH:mm:ss.SSSXXX
    //这个后面的.SSSXXX写了的话这一行就直接抛异常了，所以我去掉了，还有前面的T  一定要用英文的单引号包裹起来
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    Date date = df.parse(oldDate);
    SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
    Date date1 =  df1.parse(date.toString());
    DateFormat df2 = new SimpleDateFormat(format);
    return df2.format(date1);
  }

  /**
   * 根据身份证号码，获取周岁年龄，每过一个生日就长一岁。
   * @param cardNo
   * @return
   */
  public static int getAge(final String cardNo) {
    String birthdayStr ="";
    Date birthdayDate = null;
    if (!TextUtils.isEmpty(cardNo) &&cardNo.length() ==18){
      birthdayStr = cardNo.substring(6, 10) + "-" + cardNo.substring(10, 12) + "-" + cardNo.substring(12, 14);
    }
    birthdayDate = stringToDate2(birthdayStr , yyyyMMDD);

    Calendar cal = Calendar.getInstance();
    if (cal.before(birthdayDate)) {
      return -1;
    }
    int yearNow = cal.get(Calendar.YEAR);
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    cal.setTime(birthdayDate);

    int yearBirth = cal.get(Calendar.YEAR);
    int monthBirth = cal.get(Calendar.MONTH);
    int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

    int age = yearNow - yearBirth;

    if (monthNow <= monthBirth) {
      if (monthNow == monthBirth) {
        if (dayOfMonthNow < dayOfMonthBirth) age--;
      }else{
        age--;
      }
    }
    return age;
  }
  //根据身份证号码 获取性别  1男 2女
  public static int getSex(final String cardNo ){
    if (!TextUtils.isEmpty(cardNo) &&cardNo.length() ==18){
      String mSubstring = cardNo.substring(cardNo.length() - 2, cardNo.length() - 1);
      int cursex = Integer.parseInt(mSubstring);
      if (cursex % 2 == 0) {
       return  2;
      } else {
        return 1;
      }
    }

    return -1;
  }
  /**
   * 处理身份证刷卡得到的年龄
   */
  public static String GetAges(String dates) {
    if (TextUtils.isEmpty(dates)) {
      return "0";
    } else {
      Calendar cal = Calendar.getInstance();
      int yearNow = cal.get(Calendar.YEAR);
      int monthNow = cal.get(Calendar.MONTH);
      int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
      cal.setTime(new Date(stringTolongtime(dates)));

      int yearBirth = cal.get(Calendar.YEAR);
      int monthBirth = cal.get(Calendar.MONTH);
      int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

      int age = yearNow - yearBirth;

      if (monthNow <= monthBirth) {
        if (monthNow == monthBirth) {
          if (dayOfMonthNow < dayOfMonthBirth) age--;
        } else {
          age--;
        }
      }
      return String.valueOf(age);
    }
  }

  /**
   * 字符串 转 yyyy-MM-dd
   *
   * @param date
   * @return
   *
   * @author
   */
  public static String stringToDate(String date) {
    Long transition_time = Long.parseLong(date);
    mFormat = DateFormat.getDateInstance(DateFormat.MEDIUM,
        Locale.getDefault());
    return mFormat.format(new Date(transition_time));
  }

  // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
  // HH时mm分ss秒，
  // strTime的时间格式必须要与formatType的时间格式相同
  public static Date stringToDate2(String strTime, String formatType){
    SimpleDateFormat formatter = new SimpleDateFormat(formatType);
    Date date = null;
    try {
      date = formatter.parse(strTime);
    }catch (Exception e){
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 字符串 转 yyyy-MM-dd hh:mm:ss
   *
   * @param date
   * @return
   *
   * @author
   */
  public static String stringToDate2(String date) {
    Long transition_time = Long.parseLong(date);
    mFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.MEDIUM, Locale.getDefault());
    return mFormat.format(new Date(transition_time));
  }

  public static String stringToDate(String date, String format) {
    Long transition_time = Long.parseLong(date);
    Date dt = new Date(transition_time);
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(dt);
  }

  /**
   * long 转 String
   *
   * @param date
   * @return
   */
  public static String longToString(long date) {
    mFormat = DateFormat.getDateInstance(DateFormat.MEDIUM,
        Locale.getDefault());
    return mFormat.format(new Date(date));
  }

  public static String longToString(long date, String myformat) {
    if (date==0){
      return "";
    }
    SimpleDateFormat format = new SimpleDateFormat(myformat);
    Date dt = new Date(date);
    return format.format(dt);
  }

  /**
   * 截取服务端返回的日期字符串
   *
   * @param date
   * @return
   */
  public static String jsonStringToDate(String date) {
    if (TextUtils.isEmpty(date)) {
      return "";
    }
    return stringToDate(date.substring(date.indexOf("(") + 1,
        date.indexOf("+")));
  }

  public static String jsonStringToDateTime(String date, String format) {
    return stringToDate(
        date.substring(date.indexOf("(") + 1, date.indexOf("+")),
        format);
  }

  /**
   * Date 转 String
   *
   * yyyy-MM-dd hh:mm
   *
   * @param date
   * @return
   */
  public static String dateToString(String date) {
    mFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.MEDIUM, Locale.getDefault());
    return mFormat.format(date);
  }

  public static String dateToString(Date date, String format) {
    SimpleDateFormat mFormatter = new SimpleDateFormat(format);
    String time = mFormatter.format(date);
    return time;
  }
  /**
   * 处理服务器返回的时间换算成对应时间格式 yy年mm月dd日
   */
  public static String jsonStringToDates(int date) {
    if (date==0) {
      return "";
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(Long.parseLong(String.valueOf(date)));
    int          year   = calendar.get(Calendar.YEAR);
    int          month  = calendar.get(Calendar.MONTH);
    int          day    = calendar.get(Calendar.DAY_OF_MONTH);
    Integer moths  = Integer.valueOf(month + 1);
    Integer days   = Integer.valueOf(day);
    StringBuffer mothss = new StringBuffer();
    StringBuffer dayss  = new StringBuffer();
    if (moths.toString().length() == 1) {
      mothss.append("0").append(moths);
    } else {
      mothss.append(moths);
    }
    if (days.toString().length() == 1) {
      dayss.append("0").append(days);
    } else {
      dayss.append(days);
    }
    return (year + "-" + mothss + "-" + dayss);
  }
  /**
   * 判断是否为合法的日期时间字符串
   *
   * @param str_input
   * @param str_input
   * @return boolean;符合为true,不符合为false
   */
  public static boolean isDate(String str_input, String rDateFormat) {
    if (!isNull(str_input)) {
      mFormat = new SimpleDateFormat(rDateFormat);
      mFormat.setLenient(false);
      try {
        mFormat.format(mFormat.parse(str_input));
      } catch (Exception e) {
        return false;
      }
      return true;
    }
    return false;
  }

  public static boolean isNull(String str) {
    return str == null;
  }

  public static String[] getThisWeekDateOnlyPass() {
    Date d = new Date();
    Calendar c = Calendar.getInstance();
    c.setTime(d);
    int week = c.get(Calendar.DAY_OF_WEEK) - 1;
    week = week > 0 ? week : 7;// 1-7 星期的周期
    String[] arr = new String[week];
    int count = 0;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    long addTime = 24 * 60 * 60 * 1000;
    while (count < week) {
      String str = df.format(new Date(d.getTime() - (count * addTime)));
      arr[count] = str;
      count++;
    }
    return arr;
  }

  /**
   *
   * @param age
   * 服务器返回年月日格式  举例： /Date(575222400000+0800)/
   * @author XW
   * @return
   */
  public static int getMillisecondtoAge(String age){
    int myear = 0;
    try {
      age = age.substring(age.indexOf("(") + 1, age.indexOf("+"));
      //DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(Long.parseLong(age));

      long mday = (new Date().getTime() - Long.parseLong(age))
          / (24 * 60 * 60 * 1000) + 1;
      myear = (int) (mday / 365);

      System.out.println("PC300上传数据年龄："+myear);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return myear;
  }



  /**
   * 将日期字符串转换为Date类型
   *
   * @param dateStr 日期字符串
   * @param type    日期字符串格式
   * @return Date对象
   */
  public static Date parseDate(String dateStr, String type) {
    SimpleDateFormat df = new SimpleDateFormat(type);
    Date date = null;
    try {
      date = df.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;

  }


  /**
   * 得到年
   *
   * @param date Date对象
   * @return 年
   */
  public static int getYear(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.YEAR);
  }
  /**
   * 得到年
   *
   * @param date Date对象
   * @return 年
   */
  public static int getYear(long date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(date));
    return cal.get(Calendar.YEAR);
  }
  /**
   * 得到月
   *
   * @param date Date对象
   * @return 月
   */
  public static int getMonth(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.MONTH) + 1;

  }

  /**
   * 得到日
   *
   * @param date Date对象
   * @return 日
   */
  public static int getDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 转换日期 将日期转为今天, 昨天, 前天, XXXX-XX-XX, ...
   *
   * @param time 时间
   * @return 当前日期转换为更容易理解的方式
   */
  public static String translateDate(Long time) {
    long oneDay = 24 * 60 * 60 * 1000;
    Calendar current = Calendar.getInstance();
    Calendar today = Calendar.getInstance();    //今天

    today.set(Calendar.YEAR, current.get(Calendar.YEAR));
    today.set(Calendar.MONTH, current.get(Calendar.MONTH));
    today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
    //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);

    long todayStartTime = today.getTimeInMillis();

    if (time >= todayStartTime && time < todayStartTime + oneDay) { // today
      return "今天";
    } else if (time >= todayStartTime - oneDay && time < todayStartTime) { // yesterday
      return "昨天";
    } else if (time >= todayStartTime - oneDay * 2 && time < todayStartTime - oneDay) { // the day before yesterday
      return "前天";
    } else if (time > todayStartTime + oneDay) { // future
      return "将来某一天";
    } else {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = new Date(time);
      return dateFormat.format(date);
    }
  }

  /**
   * 转换日期 转换为更为人性化的时间
   *
   * @param time 时间
   * @return
   */
  public static String translateDate(long time, long curTime) {
    long oneDay = 24 * 60 * 60;
    Calendar today = Calendar.getInstance();    //今天
    today.setTimeInMillis(curTime * 1000);
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    long todayStartTime = today.getTimeInMillis() / 1000;
    if (time >= todayStartTime) {
      long d = curTime - time;
      if (d <= 60) {
        return "1分钟前";
      } else if (d <= 60 * 60) {
        long m = d / 60;
        if (m <= 0) {
          m = 1;
        }
        return m + "分钟前";
      } else {
        SimpleDateFormat dateFormat = new SimpleDateFormat("今天 HH:mm");
        Date date = new Date(time * 1000);
        String dateStr = dateFormat.format(date);
        if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
          dateStr = dateStr.replace(" 0", " ");
        }
        return dateStr;
      }
    } else {
      if (time < todayStartTime && time > todayStartTime - oneDay) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("昨天 HH:mm");
        Date date = new Date(time * 1000);
        String dateStr = dateFormat.format(date);
        if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {

          dateStr = dateStr.replace(" 0", " ");
        }
        return dateStr;
      } else if (time < todayStartTime - oneDay && time > todayStartTime - 2 * oneDay) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("前天 HH:mm");
        Date date = new Date(time * 1000);
        String dateStr = dateFormat.format(date);
        if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
          dateStr = dateStr.replace(" 0", " ");
        }
        return dateStr;
      } else {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time * 1000);
        String dateStr = dateFormat.format(date);
        if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
          dateStr = dateStr.replace(" 0", " ");
        }
        return dateStr;
      }
    }
  }


  /**
   * 转化时间输入时间与当前时间的间隔
   *
   * @param timestamp
   * @return
   */
  public static String converTime(long timestamp) {
    long currentSeconds = System.currentTimeMillis() / 1000;
    long timeGap = currentSeconds - timestamp;// 与现在时间相差秒数
    String timeStr = null;
    if (timeGap > 72 * 60*60){//3天以上
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      Date date = new Date(timestamp * 1000);
      String dateStr = dateFormat.format(date);
      if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
        dateStr = dateStr.replace(" 0", " ");
      }
      timeStr = dateStr;
    }else if (timeGap > 24 * 60 * 60) {// 1天以上
      timeStr = timeGap / (24 * 60 * 60) + "天前";
    } else if (timeGap > 60 * 60) {// 1小时-24小时
      timeStr = timeGap / (60 * 60) + "小时前";
    } else if (timeGap > 60) {// 1分钟-59分钟
      timeStr = timeGap / 60 + "分钟前";
    } else {// 1秒钟-59秒钟
      timeStr = "刚刚";
    }
    return timeStr;
  }

  /**
   * 根据传入日期计算与当前日期 相差的天数
   * @param time
   * @return
   */
  public static int intervalTime(long time){
    long currentSeconds = System.currentTimeMillis() ;
    long timeGap =time-currentSeconds;// 与现在时间相差秒数
    return (int) (timeGap/(24 * 60 * 60*1000));
  }

  /**
   * 根据传入天数  返回秒数
   * @param time
   * @return
   */
  public static long intervalTime(int  time){
    Calendar curr = Calendar.getInstance();
    curr.set(Calendar.DAY_OF_MONTH,curr.get(Calendar.DAY_OF_MONTH)+(time-1));
    Date date=curr.getTime();
//    long currentSeconds = System.currentTimeMillis() ;
//    long timeGap =currentSeconds+time*(24 * 60 * 60*1000);// 与现在时间相差秒数
    return stringTolongtime(dateToString(date,yyyyMMDD));
  }


  /**
   * 将Date类型转换为日期字符串
   *
   * @param date Date对象
   * @param type 需要的日期格式
   * @return 按照需求格式的日期字符串
   */
  public static String formatDate(long date, String type) {
    try {
      SimpleDateFormat df = new SimpleDateFormat(type);
      return df.format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }




  // date要转换的date类型的时间
  public static long dateToLong(Date date) {
    return date.getTime();
  }

  /**
   * 将String型格式化,比如想要将2011-11-11格式化成2011年11月11日,就StringPattern("2011-11-11","yyyy-MM-dd","yyyy年MM月dd日").
   * @param date String 想要格式化的日期
   * @param oldPattern String 想要格式化的日期的现有格式
   * @param newPattern String 想要格式化成什么格式
   * @return String
   */
  public static String StringPattern(String date, String oldPattern, String newPattern) {
    if (date == null || oldPattern == null || newPattern == null)
      return "";
    SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern) ;        // 实例化模板对象
    SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern) ;        // 实例化模板对象
    Date d = null ;
    try{
      d = sdf1.parse(date) ;   // 将给定的字符串中的日期提取出来
    }catch(Exception e){            // 如果提供的字符串格式有错误，则进行异常处理
      e.printStackTrace() ;       // 打印异常信息
    }
    return sdf2.format(d);
  }


  /**
   * 获取两个日期之间的间隔天数
   * @return
   */
  public static int getGapCount(Date startDate, Date endDate) {
    Calendar fromCalendar = Calendar.getInstance();
    fromCalendar.setTime(startDate);
    fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
    fromCalendar.set(Calendar.MINUTE, 0);
    fromCalendar.set(Calendar.SECOND, 0);
    fromCalendar.set(Calendar.MILLISECOND, 0);

    Calendar toCalendar = Calendar.getInstance();
    toCalendar.setTime(endDate);
    toCalendar.set(Calendar.HOUR_OF_DAY, 0);
    toCalendar.set(Calendar.MINUTE, 0);
    toCalendar.set(Calendar.SECOND, 0);
    toCalendar.set(Calendar.MILLISECOND, 0);

    return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
  }

  /**
   * 判断是否为当天
   * @return
   */
  public static boolean isToday(String date){
    SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
    //格式化为相同格式
    return fmt.format(date).toString().equals(fmt.format(new Date()).toString());

  }


  /**
   * 根据毫秒数返回对应时间格式
   *
   * @param time 时间
   * @return 当前日期转换为更容易理解的方式
   */
  public static String compareTime(Long time) {
    long oneDay = 24 * 60 * 60 ;
    Calendar current = Calendar.getInstance();
    Calendar today = Calendar.getInstance();    //今天

    today.set(Calendar.YEAR, current.get(Calendar.YEAR));
    today.set(Calendar.MONTH, current.get(Calendar.MONTH));
    today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
    //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);

    long todayStartTime = today.getTimeInMillis() / 1000;
    if (time < todayStartTime &&  todayStartTime - oneDay  < time) { // today
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      Date date = new Date(time);
      return dateFormat.format(date);
    }else {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = new Date(time);
      return dateFormat.format(date);
    }
  }
  /**
   * 根据毫秒数返回对应时间格式
   *
   * @param time 时间
   * @return 当前日期转换为更容易理解的方式
   */
  public static String compareTimes(Long time , String timeStr) {

    Calendar today = Calendar.getInstance();

    today.setTimeInMillis(time);

    if (timeStr.equals("DD")){
      return today.get(Calendar.DAY_OF_MONTH)+"日";
    }
    if (timeStr.equals("HHMM")){
      return today.get(Calendar.HOUR_OF_DAY) +":" + today.get(Calendar.MINUTE);
    }
    if (timeStr.equals("YYYYMM")){
      return today.get(Calendar.YEAR) +"年" + (today.get(Calendar.MONTH)+1) +"月";
    }
    return null;
  }

  public static String StringFilter(String str) throws PatternSyntaxException {
    String regEx = "[^0-9]";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(str);
    return m.replaceAll("").trim();
  }

  /**
   * 返回年-月-日 hh:ff:ss 星期几
   * <p>
   * <p>
   * long系统时间
   *
   * @return String 例如2013-05-26 19:39:26 星期日
   */

  public static String getDateAndWeek(long time, int type) {
    //Type 1  为yyyy-MM-dd HH:mm:ss  星期日
    //Type 2  为yyyy-MM-dd
    //Type 3  为yyyy

    Date dt = new Date();
    if (time == 0) {
      time = dt.getTime();//这就是距离1970年1月1日0点0分0秒的毫秒数(当前时间毫秒值)
    }
    SimpleDateFormat format = null;
    if (type == 1) {
      format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    } else if (type == 2) {
      format = new SimpleDateFormat("yyyy-MM-dd");
    } else if (type == 4) {
      format = new SimpleDateFormat("yyyy-MM");
    } else if (type == 3) {
      format = new SimpleDateFormat("yyyy");
    } else if (type == 5) {
      format = new SimpleDateFormat("MM-dd");
    } else if (type == 6) {
      format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    Date date = new Date(time);

    String dateString = format.format(date);

    String dayString = getWeekOfDate(date);
    if (type == 1) {
      return dateString + " " + dayString;
    } else {
      return dateString;
    }
  }

  /**
   * 星期几
   *
   * @return 星期一到星期日
   */
  public static String getWeekOfDate(Date date) {
    String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0)
      w = 0;

    return weekDays[w];
  }



  /**
   * 星期几
   *
   * @param time
   * long 系统时间的long类型
   * @return 星期一到星期日
   *
   * */
  public static String getWeekOfDate(long time) {

    Date date = new Date(time);
    String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0)
      w = 0;

    return weekDays[w];
  }


  /**
   * 将年月日时分秒转成Long类型
   * @param dateTime
   * @return
   */
  public static Long dateTimeToTimeStamp(String dateTime, String format) {
    try {
      SimpleDateFormat YYYYMMDDHHMMSS_FORMAT = new SimpleDateFormat(format, Locale.getDefault());
      Date e = YYYYMMDDHHMMSS_FORMAT.parse(dateTime);
      return Long.valueOf(e.getTime());
    } catch (ParseException var2) {
      var2.printStackTrace();
      return Long.valueOf(0L);
    }
  }

  /**
   * 将时间字符串转为时间戳
   * <p>time格式为format</p>
   *
   * @param time   时间字符串
   * @param format 时间格式
   * @return 毫秒时间戳
   */
  public static long string2Millis(final String time, final DateFormat format) {
    try {
      return format.parse(time).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return -1;
  }


  public static String getTime(String user_time) {
    String re_time = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
    Date d;
    try {
      d = sdf.parse(user_time);
      long l = d.getTime();
      String str = String.valueOf(l);
      re_time = str.substring(0, 10);


    } catch (ParseException e) {
// TODO Auto-generated catch block
      e.printStackTrace();
    }
    return re_time;
  }

  // 将时间戳转为字符串
  public static String getStrTime(String cc_time) {
    String re_StrTime = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm:ss秒");
// 例如：cc_time=1291778220
    long lcc_time = Long.valueOf(cc_time);
    re_StrTime = sdf.format(new Date(lcc_time * 1000L));
    return re_StrTime;
  }






  public static Date parse(String strDate, String pattern) {
    if (TextUtils.isEmpty(strDate)) {
      return null;
    }
    try {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      return df.parse(strDate);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String format(Date date, String pattern) {
    String returnValue = "";
    if (date != null) {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      returnValue = df.format(date);
    }
    return (returnValue);
  }

  /**
   * 返回 yyyy-MM-dd HH:mm:ss格式日期时间
   *
   * @param date
   * @return
   */
  public static String formatDateTime(Date date) {
    return format(date, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * 返回 yyyy-MM-dd HH:mm:ss格式日期时间
   *
   * @param time 毫秒数
   * @return
   */
  public static String formatDateTime(long time) {
    return format(new Date(time), "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * 返回时间yy-MM-dd HH:mm:ss格式
   * @return
   */
  public static String formatDate(){
    return format(new Date(),"yy-MM-dd HH:mm:ss");
  }

  /**
   * 返回 yyyy-MM-dd格式日期时间
   */
  public static String formatDateWithoutTime(Date date) {
    return format(date, "yyyy-MM-dd");
  }

  public static String formatTrackQueryTime(String s) {
    Date date = null;
    try {
      date = new SimpleDateFormat("yyyyMMddhhmmss").parse(s);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return DateUtil.format(date,"yyyy-MM-dd HH:mm:ss");
  }
  /**
   * 获取毫秒数
   * @param dateTime (格式：yyyy-MM-dd HH:mm:ss)
   * @return
   */
  public static String getLongTimeByYearMonthDay(String dateTime){
    String time = "";
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    try {
      time = sDateFormat.parse(dateTime).getTime()+ "";
    }catch (Exception ex){
      ex.printStackTrace();
    }
    return time;
  }

  /**
   * 获取时间差
   */
  public static boolean isNeedFixTime(String serverTime, long resultTime){
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    String now = DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
    long nowTime = 0;
    long fixTime = 0;
    try {
      nowTime = sDateFormat.parse(now).getTime();
      fixTime = sDateFormat.parse(serverTime).getTime();
    }catch (Exception ex){
      ex.printStackTrace();
    }
    return Math.abs(fixTime - nowTime) > resultTime;
  }
  //分钟转小时
  public static String getHour(int restTime) {
    String time="0";
    if (restTime>0){
      DecimalFormat df=new DecimalFormat("0.0");
      time=(df.format((float)restTime/60));
    }


    return time;
  }
  //获取当前 早上，上午，中午，下午，晚上
  public static String getCurrentTiem(){
    int hours=new Date().getHours();

    if (hours>0&&hours<=6){
      return "凌晨";
    }else
    if (hours>6&&hours<=12){
      return "上午";
    }else
    if (hours>12&&hours<=18){
      return "下午";
    }else
    if (hours>18&&hours<=24){
      return "晚上";
    }
    return null;
  }

  /**
   * 将毫秒数转为月日
   */
  public static String getMmdd(String time){
    Date d = new Date(time);
    SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
    return  sdf.format(d);
  }

  //近一周
  public static String getLastWeek(String formats){
    SimpleDateFormat format = new SimpleDateFormat(formats);
    Calendar c = Calendar.getInstance();
    //过去七天
    c.setTime(new Date());
    c.add(Calendar.DATE, - 7);
    Date d = c.getTime();
    return format.format(d);
  }
  //近一个月
  public static String getLastMonth(String formats, int cont){
    SimpleDateFormat format = new SimpleDateFormat(formats);
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.MONTH, -cont);
    Date m = c.getTime();
    return  format.format(m);
  }
  //近一年
  public static String getLastYear(String formats){
    SimpleDateFormat format = new SimpleDateFormat(formats);
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.YEAR, -1);
    Date y = c.getTime();
    return format.format(y);
  }
  // string类型转换为long类型
  // strTime要转换的String类型的时间
  // formatType时间格式
  // strTime的时间格式和formatType的时间格式必须相同
  public static long stringToLong(String strTime, String formatType) {
    Date date = stringToDateTime(strTime, formatType); // String类型转成date类型
    if (date == null) {
      return 0;
    } else {
      long currentTime = dateToLong(date); // date类型转成long类型
      return currentTime;
    }
  }
  // string类型转换为date类型
  // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
  // HH时mm分ss秒，
  // strTime的时间格式必须要与formatType的时间格式相同
  public static Date stringToDateTime(String strTime, String formatType) {
    SimpleDateFormat formatter = new SimpleDateFormat(formatType);
    Date date = null;
    try {
      date = formatter.parse(strTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 根据输入的年龄获取大致的出生日期
   *
   * @param trim
   * @return
   */
  public static String getBirthday(int trim) {
    //获取当前系统时间
    Calendar cal = Calendar.getInstance();
    //取出系统当前时间的年、月、日部分
    int yearNow = cal.get(Calendar.YEAR);
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    String birthday = (yearNow - trim) + "-" + monthNow + "-" + dayOfMonthNow + " 00:00:00";
    return birthday;
  }



  /**
   *   获取距离传入日期 几个月的时间
   * @param date
   * @param time   正数为后几个月   负数为前几个月
   * @return
   */
  public static String getMonthAgo(Date date, int time) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyyMMDD);
    Calendar calendar = Calendar.getInstance(); calendar.setTime(date);
    calendar.add(Calendar.MONTH, time);
    String monthAgo = simpleDateFormat.format(calendar.getTime());
    return monthAgo;
  }
  public static String getDayAgo(Date date, int time) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyyMMDD);
    Calendar calendar = Calendar.getInstance(); calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, time);
    String monthAgo = simpleDateFormat.format(calendar.getTime());
    return monthAgo;
  }

  public static int getAge(long birthdayStr) {
    Date birthdayDate = null;
    birthdayDate = stringToDate2( longToString(birthdayStr , yyyyMMDD) , yyyyMMDD);

    Calendar cal = Calendar.getInstance();
    if (cal.before(birthdayDate)) {
      return -1;
    }
    int yearNow = cal.get(Calendar.YEAR);
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    cal.setTime(birthdayDate);

    int yearBirth = cal.get(Calendar.YEAR);
    int monthBirth = cal.get(Calendar.MONTH);
    int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

    int age = yearNow - yearBirth;

    if (monthNow <= monthBirth) {
      if (monthNow == monthBirth) {
        if (dayOfMonthNow < dayOfMonthBirth) age--;
      }else{
        age--;
      }
    }
    return age;
  }

  public static String getDecimal(float decimal,int size){
    return  (new BigDecimal(decimal).setScale(size, BigDecimal.ROUND_HALF_UP).doubleValue())+"";
  }

  public static String getDecimal(float decimal){
    return String.valueOf(Math.floor(decimal*10d)/10);
  }
}
