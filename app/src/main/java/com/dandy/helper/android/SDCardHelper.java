package com.dandy.helper.android;

import android.os.Environment;
import android.os.StatFs;

public class SDCardHelper {

    /**
     * 得到SD卡的目录路径
     */
    public static String getSDCardDirPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 判断SD卡是否可用
     * 
     * @return
     */
    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getSDCardAllSize() {
        if (isSDCardExist()) {
            StatFs stat = new StatFs(getSDCardDirPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     * 
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    @SuppressWarnings("deprecation")
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardDirPath())) {
            filePath = getSDCardDirPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     * 
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }
}
