package net.getquicker.hook;

import android.util.Log;


public class XLog {

    private static final String LOG_TAG ="hook";
    private static int sLogLevel = 2;
    private static final boolean LOG_TO_XPOSED = true;

    private XLog() {
    }

    private static void log(int priority, String message, Object... args) {
        if (priority < sLogLevel)
            return;

        message = String.format(message, args);

        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            Throwable throwable = (Throwable) args[args.length - 1];
            String stacktraceStr = Log.getStackTraceString(throwable);
            message += '\n' + stacktraceStr;
        }

        // Write to the default log tag
        Log.println(priority, LOG_TAG, message);

        // Duplicate to the Xposed log if enabled
        if (LOG_TO_XPOSED) {
            // only log to LSPosed
            Log.println(priority, "LSPosed-Bridge", LOG_TAG + ": " + message);
        }
    }

    public static void v(String message, Object... args) {
        log(Log.VERBOSE, message, args);
    }

    public static void d(String message, Object... args) {
        log(Log.DEBUG, message, args);
    }

    public static void i(String message, Object... args) {
        log(Log.INFO, message, args);
    }

    public static void w(String message, Object... args) {
        log(Log.WARN, message, args);
    }

    public static void e(String message, Object... args) {
        log(Log.ERROR, message, args);
    }

    public static void setLogLevel(int logLevel) {
        sLogLevel = logLevel;
    }
}
