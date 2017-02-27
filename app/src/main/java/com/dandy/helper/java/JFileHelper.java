package com.dandy.helper.java;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

import com.dandy.helper.android.LogHelper;

public class JFileHelper {

    private static final String TAG = JFileHelper.class.getSimpleName();

    /**
     * 输入流转字节数组
     * 
     * @param in
     * @return
     * @throws IOException
     */
    public static void copyStream(InputStream is, OutputStream out) throws IOException {
        try {
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
            closeIOStream(is, out);
        }
    }

    /**
     * 关闭io流
     * 
     * @param closeable
     */
    public static void closeIOStream(Closeable... closeable) {
        if (closeable == null) {
            return;
        }
        for (Closeable ca : closeable) {
            try {
                if (ca == null) {
                    continue;
                }
                ca.close();
                ca = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断某个路径的文件是否存在
     * 
     * @param path
     * @return
     */
    public static boolean isFileExit(String path) {
        File f = new File(path);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 流转字符串方法
     * 
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 返回该文件的大小，例如1.3GB
     * 
     * @param fileLength
     *            文件长度，可以由File.getLength（）得到
     * @return
     */
    public static String formatFileLength(long fileLength) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (fileLength >= gb) {
            return String.format(Locale.getDefault(), "%.1f GB", (float) fileLength / gb);
        } else if (fileLength >= mb) {
            float f = (float) fileLength / mb;
            return String.format(Locale.getDefault(), f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (fileLength >= kb) {
            float f = (float) fileLength / kb;
            return String.format(Locale.getDefault(), f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format(Locale.getDefault(), "%d B", fileLength);
        }
    }

    /**
     * 复制文件
     * 
     * @param ins
     *            该文件的输入流
     * @param destFileFullPath
     *            要存放的文件的路径
     * @return
     */
    public static boolean copyFile(InputStream ins, String destFileFullPath) {
        LogHelper.d(TAG, LogHelper.getThreadName() + "destFileFullPath-" + destFileFullPath);
        FileOutputStream fos = null;
        try {
            File file = new File(destFileFullPath);
            LogHelper.d(TAG, LogHelper.getThreadName() + "开始读入");
            fos = new FileOutputStream(file);
            LogHelper.d(TAG, LogHelper.getThreadName() + "开始写出");
            byte[] buffer = new byte[8192];
            int count = 0;
            LogHelper.d(TAG, LogHelper.getThreadName() + "准备循环了");
            while ((count = ins.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            LogHelper.d(TAG, LogHelper.getThreadName() + "已经创建该文件");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.d(TAG, LogHelper.getThreadName() + e.getMessage());
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (ins != null) {
                    ins.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.d(TAG, LogHelper.getThreadName() + e.getMessage());
            }
        }
    }

    public static boolean copyFolder(String srcFolderFullPath, String destFolderFullPath) {
        LogHelper.d(TAG, LogHelper.getThreadName() + "srcFolderFullPath-" + srcFolderFullPath + " destFolderFullPath-" + destFolderFullPath);
        try {
            boolean success = (new File(destFolderFullPath)).mkdirs(); // 如果文件夹不存在
                                                                       // 则建立新文件夹
            if (!success) {
                return false;
            }
            File file = new File(srcFolderFullPath);
            String[] files = file.list();
            File temp = null;
            for (int i = 0; i < files.length; i++) {
                if (srcFolderFullPath.endsWith(File.separator)) {
                    temp = new File(srcFolderFullPath + files[i]);
                } else {
                    temp = new File(srcFolderFullPath + File.separator + files[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    copyFile(input, destFolderFullPath + "/" + (temp.getName()).toString());
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(srcFolderFullPath + "/" + files[i], destFolderFullPath + "/" + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.d(TAG, LogHelper.getThreadName() + e.getMessage());
            return false;
        }
        return true;
    }

    public static String getFileContent(String filePath) {
        try {
            FileInputStream fins = new FileInputStream(filePath);
            return getFileContent(fins);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileContent(InputStream ins) {
        try {
            byte[] contentByte = new byte[ins.available()];
            ins.read(contentByte);
            return new String(contentByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean copyContentToFile(String content, String destFileFullPath) {
        FileWriter fWriter = null;
        try {
            fWriter = new FileWriter(destFileFullPath);
            fWriter.write(content);
            fWriter.flush();
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                fWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static InputStream getInputStreamFromPath(String path) {
        LogHelper.d(TAG, LogHelper.getThreadName());
        InputStream ins = null;
        // File file = new File(path);
        try {
            ins = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ins;
    }

    public static void removeFile(String file_path) {
        try {
            File f = new File(file_path);
            if (!f.exists()) {
                return;
            }
            f.delete();
        } catch (Exception e) {
        }
    }
    
    /**
     * <pre>
     * 在某个大的文件夹中删除指定名称的所有文件，
     * 例如这个文件夹下不同的子文件夹下都有一个叫a.txt的文件，那么此时可以用这个方法来删除这个a.txt
     * </pre>
     * 
     * @param targetFolderFullPath
     *            目标文件夹
     * @param fileSimpleName
     *            要删除的文件的名称（不要全路劲）
     */
    public static void deleteAppointedFilesInDirectory(String targetFolderFullPath, String fileSimpleName) {
        File file = new File(targetFolderFullPath);
        if (!file.exists()) {// 文件夹不存在，不用查找
            LogHelper.d(TAG, LogHelper.getThreadName() + "file does not exist");
            return;
        }
        String[] files = file.list();
        File temp = null;
        if (files == null || files.length == 0) {// 文件夹下没有子文件或子文件夹，不用查找
            LogHelper.d(TAG, LogHelper.getThreadName() + "files.length == 0");
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (!files[i].equals(fileSimpleName)) {// 如果你的文件名或者文件夹的名称都和目标名称不一致了，那这个就不用判断了，直接判断下一个
                continue;
            }
            if (targetFolderFullPath.endsWith(File.separator)) {
                temp = new File(targetFolderFullPath + files[i]);
            } else {
                temp = new File(targetFolderFullPath + File.separator + files[i]);
            }
            if (temp.isFile()) {// 是文件，而且名称相同，删除
                temp.delete();
//                deleteAppointedFile(targetFolderFullPath + "/" + (temp.getName()).toString());
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                deleteAppointedFilesInDirectory(targetFolderFullPath + "/" + files[i], fileSimpleName);
            }
        }
    }

    /**
     * <pre>
     * 在某个大的文件夹中删除指定名称的所有文件夹，
     * 例如这个文件夹下不同的子文件夹下都有一个叫.svn的文件夹，那么此时可以用这个方法来删除这个.svn夹
     * </pre>
     * 
     * @param targetFolderFullPath
     *            目标文件夹
     * @param directorySimpleName
     *            要删除的文件夹的名称（不要全路劲）
     */
    public static void deleteAppointedDirectorysInDirectory(String targetFolderFullPath, String directorySimpleName) {
        File file = new File(targetFolderFullPath);
        if (file.getName().equals(directorySimpleName)) {// 如果该文件夹已经是要删除的文件夹名称了，直接删除这个文件夹
            LogHelper.d(TAG, LogHelper.getThreadName() + " file.getName()=" + file.getName() + " directorySimpleName=" + directorySimpleName);
            deleteFolder(targetFolderFullPath);
            return;
        }
        String[] files = file.list();
        File temp = null;
        if (files == null || files.length == 0) {// 文件夹下没有子文件或子文件夹，不用查找
            LogHelper.d(TAG, LogHelper.getThreadName() + "files.length == 0");
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (!files[i].equals(directorySimpleName)) {// 如果你的文件名或者文件夹的名称都和目标名称不一致了，那这个就不用判断了，直接判断下一个
                continue;
            }
            if (targetFolderFullPath.endsWith(File.separator)) {
                temp = new File(targetFolderFullPath + files[i]);
            } else {
                temp = new File(targetFolderFullPath + File.separator + files[i]);
            }
            if (temp.isFile()) {// 是文件，而且名称相同，删除
                continue;
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                deleteAppointedDirectorysInDirectory(targetFolderFullPath + File.separator + files[i], directorySimpleName);
            }
        }
    }

    /**
     * 删除某个文件夹
     * 
     * @param targetFolderFullPath
     *            要删除的文件夹的路径
     * @return
     */
    public static boolean deleteFolder(String targetFolderFullPath) {
        LogHelper.d(TAG, LogHelper.getThreadName() + "targetFolderFullPath-" + targetFolderFullPath);
        File file = new File(targetFolderFullPath);
        if (!file.exists()) {
            LogHelper.d(TAG, LogHelper.getThreadName() + "file does not exist");
            return true;
        }
        String[] files = file.list();
        File temp = null;
        if (files == null) {
            LogHelper.d(TAG, LogHelper.getThreadName() + "files == null");
            return true;
        }
        if (files.length == 0) {
            LogHelper.d(TAG, LogHelper.getThreadName() + "files.length == 0");
            boolean success = file.delete();
            return success;
        }
        for (int i = 0; i < files.length; i++) {
            if (targetFolderFullPath.endsWith(File.separator)) {
                temp = new File(targetFolderFullPath + files[i]);
            } else {
                temp = new File(targetFolderFullPath + File.separator + files[i]);
            }
            if (temp.isFile()) {
                deleteFile(targetFolderFullPath + File.separator + (temp.getName()).toString());
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                deleteFolder(targetFolderFullPath + File.separator + files[i]);
            }
        }
        boolean success = file.delete();
        return success;
    }

    /**
     * 删除某个文件
     * 
     * @param targetFileFullPath
     *            要删除的文件的路径
     */
    public static void deleteFile(String targetFileFullPath) {
        File file = new File(targetFileFullPath);
        file.delete();
    }
}
