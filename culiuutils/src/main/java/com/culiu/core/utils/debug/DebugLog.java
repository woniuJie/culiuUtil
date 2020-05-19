/**
 * Project Name:culiulib File Name:DebugLog.java Package Name:com.culiu.core.utils.log Date:2014-10-10下午11:27:25 Copyright (c) 2014,
 * adison All Rights Reserved.
 */

package com.culiu.core.utils.debug;

import android.util.Log;

/**
 * @author adison
 * @describe 创建一个简单并且更易懂的log
 * @date: 2014-10-10 下午11:27:25
 */
public class DebugLog {

    private static LogFuncExt mLogFuncExt;

    public interface LogFuncExt {
        void writeLog(int level, String tag, String info);
    }

    public static void setLogFuncExtImp(LogFuncExt logFuncExt) {
        mLogFuncExt = logFuncExt;
    }

    /**
     * 类名
     */
    private static String className;

    /**
     * 方法名
     */
    private static String methodName;

    /**
     * 行数
     */
    private static int lineNumber;

    public static boolean isDebuggable;


    private DebugLog() {
    }

    public static void setPrintLog(boolean isDebuggable) {
        DebugLog.isDebuggable = isDebuggable;
    }

    public static boolean isDebuggable() {
        return isDebuggable;
    }

    private static String createLog(String log) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append(methodName);
        buffer.append(":");
        buffer.append(lineNumber);
        buffer.append("]");
        buffer.append(log);

        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.ERROR, className, message);
        }
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message));
    }

    public static void i(String message) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.INFO, className, message);
        }
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(message));
    }

    public static void d(String message) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.DEBUG, className, message);
        }
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.d(className, createLog(message));
    }

    public static void v(String message) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.VERBOSE, className, message);
        }
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(message));
    }

    public static void w(String message) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.WARN, className, message);
        }
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.w(className, createLog(message));
    }

    public static void wtf(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(className, createLog(message));
    }

    public static void e(String message, Throwable tr) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.ERROR, className, message);
        }
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message), tr);
    }

    public static void wtf(String message, Throwable tr) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(className, createLog(message), tr);
    }

    /******************** 一般调试 *********************/
    public static void v(String tag, String msg) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.VERBOSE, tag, msg);
        }
        if (!isDebuggable() || msg == null)
            return;
        Log.v(tag, msg);

    }

    public static void d(String tag, String msg) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.DEBUG, tag, msg);
        }
        if (!isDebuggable() || msg == null)
            return;
        Log.d(tag, msg);

    }

    public static void i(String tag, String msg) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.INFO, tag, msg);
        }
        if (!isDebuggable() || msg == null)
            return;
        Log.i(tag, msg);

    }

    public static void w(String tag, String msg) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.WARN, tag, msg);
        }
        if (!isDebuggable() || msg == null)
            return;
        Log.w(tag, msg);

    }

    public static void e(String tag, String msg) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.ERROR, tag, msg);
        }
        if (!isDebuggable() || msg == null)
            return;
        Log.e(tag, msg);

    }

    public static void e(String tag, String msg, Throwable tr) {
        if (mLogFuncExt != null) {
            mLogFuncExt.writeLog(Log.ERROR, tag, msg);
        }
        if (!isDebuggable() || msg == null)
            return;
        Log.e(tag, msg, tr);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if (!isDebuggable() || msg == null)
            Log.wtf(tag, msg, tr);
    }
    /******************** end *********************/
}
