
/**
 * Copyright 2014 Zhenguo Jin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiekang.bluetooths.utlis;

import android.util.Log;

/**
 * This class can replace android.util.Log.
 *
 * @author jingle1267@163.com
 * @description And you can turn off the log by set DEBUG_LEVEL = Log.ASSERT.
 */
public final class LogUtils {
  private static boolean debug = true;

  /**
   * Don't let anyone instantiate this class.
   */
  private LogUtils() {
    throw new Error("Do not need instantiate!");
  }

  public static boolean isDebug() {
    return debug;
  }
/**
 * Master switch.To catch error info you need set this value below Log.WARN
 */

  /**
   * 'System.out' switch.When it is true, you can see the 'System.out' log.
   * Otherwise, you cannot.
   */

  /**
   * Send a {@link Log#VERBOSE} log message.
   *
   * @param obj
   */
  public static void v(Object obj) {
    if (debug) {
      String tag = getClassName();
      String msg = obj != null ? obj.toString() : "obj == null";
      Log.v(tag, msg);
    }
  }

  /**
   * Send a {@link #debug} log message.
   *
   * @param obj
   */
  public static void d(Object obj) {
    if (debug) {
      String tag = getClassName();
      String msg = obj != null ? obj.toString() : "obj == null";
      Log.d(tag, msg);
    }
  }

  /**
   * Send an {@link Log#INFO} log message.
   *
   * @param obj
   */
  public static void i(Object obj) {
    if (debug) {
      String tag = getClassName();
      String msg = obj != null ? obj.toString() : "obj == null";
      Log.i(tag, msg);
    }
  }

  /**
   * Send a {@link Log#WARN} log message.
   *
   * @param obj
   */
  public static void w(Object obj) {
    if (debug) {
      String tag = getClassName();
      String msg = obj != null ? obj.toString() : "obj == null";
      Log.w(tag, msg);
    }
  }

  /**
   * Send an {@link Log#ERROR} log message.
   *
   * @param obj
   */
  public static void e(Object obj) {
    if (debug) {
      String tag = getClassName();
      String msg = obj != null ? obj.toString() : "obj == null";
      Log.e(tag, msg);
    }
  }

  /**
   * What a Terrible Failure: Report a condition that should never happen. The
   * error will always be logged at level ASSERT with the call stack.
   * Depending on system configuration, a report may be added to the
   * {@link android.os.DropBoxManager} and/or the process may be terminated
   * immediately with an error dialog.
   *
   * @param obj
   */
  public static void wtf(Object obj) {
    if (debug) {
      String tag = getClassName();
      String msg = obj != null ? obj.toString() : "obj == null";
      Log.wtf(tag, msg);
    }
  }

  /**
   * Send a {@link Log#VERBOSE} log message.
   *
   * @param tag Used to identify the source of a log message. It usually
   *            identifies the class or activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void v(String tag, String msg) {
    if (debug) {
      Log.v(tag, msg);
    }
  }

  /**
   * Send a {@link #debug} log message.
   *
   * @param tag Used to identify the source of a log message. It usually
   *            identifies the class or activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void d(String tag, String msg) {
    if (debug) {
      Log.d(tag, msg);
    }
  }

  /**
   * Send an {@link Log#INFO} log message.
   *
   * @param tag Used to identify the source of a log message. It usually
   *            identifies the class or activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void i(String tag, String msg) {
    if (debug) {
      Log.i(tag, msg);
    }
  }

  /**
   * Send a {@link Log#WARN} log message.
   *
   * @param tag Used to identify the source of a log message. It usually
   *            identifies the class or activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void w(String tag, String msg) {
    if (debug) {
      Log.w(tag, msg);
    }
  }

  /**
   * Send an {@link Log#ERROR} log message.
   *
   * @param tag Used to identify the source of a log message. It usually
   *            identifies the class or activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void e(String tag, String msg) {
    if (debug) {
      Log.e(tag, msg);
    }
  }

  /**
   * What a Terrible Failure: Report a condition that should never happen. The
   * error will always be logged at level ASSERT with the call stack.
   * Depending on system configuration, a report may be added to the
   * {@link android.os.DropBoxManager} and/or the process may be terminated
   * immediately with an error dialog.
   *
   * @param tag Used to identify the source of a log message.
   * @param msg The message you would like logged.
   */
  public static void wtf(String tag, String msg) {
    if (debug) {
      Log.wtf(tag, msg);
    }
  }

  /**
   * Send a {@link Log#VERBOSE} log message. And just print method name and
   * position in black.
   */
  public static void print() {
    if (debug) {
      String tag = getClassName();
      String method = callMethodAndLine();
      Log.v(tag, method);
      if (debug) {
        System.out.println(tag + "  " + method);
      }
    }
  }

  /**
   * Send a {@link #debug} log message.
   *
   * @param object The object to print.
   */
  public static void print(Object object) {
    if (debug) {
      String tag = getClassName();
      String method = callMethodAndLine();
      String content = "";
      if (object != null) {
        content = object.toString() + "                    ----    "
            + method;
      } else {
        content = " ## " + "                ----    " + method;
      }
      Log.d(tag, content);
      if (debug) {
        System.out.println(tag + "  " + content + "  " + method);
      }
    }
  }

  /**
   * Send an {@link Log#ERROR} log message.
   *
   * @param object The object to print.
   */
  public static void printError(Object object) {
    if (debug) {
      String tag = getClassName();
      String method = callMethodAndLine();
      String content = "";
      if (object != null) {
        content = object.toString() + "                    ----    "
            + method;
      } else {
        content = " ## " + "                    ----    " + method;
      }
      Log.e(tag, content);
      if (debug) {
        System.err.println(tag + "  " + method + "  " + content);
      }
    }
  }

  /**
   * Print the array of stack trace elements of this method in black.
   *
   * @return
   */
  public static void printCallHierarchy() {
    if (debug) {
      String tag = getClassName();
      String method = callMethodAndLine();
      String hierarchy = getCallHierarchy();
      Log.v(tag, method + hierarchy);
      if (debug) {
        System.out.println(tag + "  " + method + hierarchy);
      }
    }
  }

  /**
   * Print debug log in blue.
   *
   * @param object The object to print.
   */
  public static void printMyLog(Object object) {
    if (debug) {
      String tag = "MYLOG";
      String method = callMethodAndLine();
      String content = "";
      if (object != null) {
        content = object.toString() + "                    ----    "
            + method;
      } else {
        content = " ## " + "                ----    " + method;
      }
      Log.d(tag, content);
      if (debug) {
        System.out.println(tag + "  " + content + "  " + method);
      }
    }
  }

  private static String getCallHierarchy() {
    String result = "";
    StackTraceElement[] trace = (new Exception()).getStackTrace();
    for (int i = 2; i < trace.length; i++) {
      result += "\r\t" + trace[i].getClassName() + ""
          + trace[i].getMethodName() + "():"
          + trace[i].getLineNumber();
    }
    return result;
  }

  private static String getClassName() {
    String result = "";
    StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
    result = thisMethodStack.getFileName();
    return result;
  }

  /**
   * Realization of double click jump events.
   *
   * @return
   */
  private static String callMethodAndLine() {
    String result = "at ";
    StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
    result += thisMethodStack.getClassName() + "";
    result += thisMethodStack.getMethodName();
    result += "(" + thisMethodStack.getFileName();
    result += ":" + thisMethodStack.getLineNumber() + ")  ";
    return result;
  }
}