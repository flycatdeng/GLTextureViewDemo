package com.dandy.helper.gles;

import com.dandy.helper.android.LogHelper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;

/**
 * 
 * @author dengchukun 2016年11月23日
 */
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    /**
     * <pre>
     * 检查设备是否支持OpenGL ES 2.0
     * check whether the device support OpenGL ES 2.0
     * </pre>
     * 
     * @param context
     * @return
     */
    public static boolean isSupportEs2(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        LogHelper.d(TAG, LogHelper.getThreadName() + " supportsEs2=" + supportsEs2);
        return supportsEs2;
    }

    /**
     * 检查每一步操作是否有错误的方法
     * 
     * @param op
     *            TAG
     */
    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            LogHelper.d("ES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
