package com.dandy.helper.gles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import android.content.Context;
import android.text.TextUtils;

import com.dandy.helper.android.FileHelper;
import com.dandy.helper.android.LogHelper;

/**
 * need the key word new to create a MaterialAid object
 * 
 * @author dandy
 * 
 */
public class MaterialAider {
    private static final String TAG = MaterialAider.class.getSimpleName();
    private String mMaterialName = "origin.mat";
    private String mMaterialDirectory = "";
    private Properties property = new Properties();
    private Context mContext;
    private HashMap<String, String> mMaterialUniformMap = new HashMap<String, String>();

    class Key {
        public static final String VERTEX_FILE = "VertexFile";
        public static final String FRAGMENT_FILE = "FragmentFile";
        public static final String VERSION = "Version";
        public static final String UNIFORM = "Uniform";
    }

    /**
     * Initialize the MaterialAid object
     * <p>
     * the default material file will not be changed until we invoke setMaterialName
     * 
     * @param context
     */
    public MaterialAider(Context context) {
        mContext = context;
//        try {
//            property.load(FileHelper.getInputStreamFromAsset(mContext, mMaterialName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * set the material file we want to use
     * 
     * @param name
     */
    public void setMaterialName(String name) {
        LogHelper.d(TAG, LogHelper.getThreadName() + " name-" + name + " mContext=" + mContext);
        mMaterialName = name;
        try {
            property.load(FileHelper.getInputStreamFromAsset(mContext, mMaterialName));
            int lastSep = name.lastIndexOf(File.separator);
            if (lastSep != -1) {
                mMaterialDirectory = name.substring(0, lastSep);
            }
            LogHelper.d(TAG, LogHelper.getThreadName() + " name-" + name + " mMaterialDirectory=" + mMaterialDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.d(TAG, LogHelper.getThreadName() + " e=" + e.getMessage());
        }
    }

    /**
     * get the material file name under assets
     * 
     * @return
     */
    public String getMaterialName() {
        return mMaterialName;
    }

    /**
     * get the vertex shader file name in the material file
     * 
     * @return
     */
    public String getMaterialVertexName() {
        String vertexFile = property.getProperty(Key.VERTEX_FILE);
//        LogHelper.d(TAG, LogHelper.getThreadName() + " vertexFile-" + vertexFile);
        if (TextUtils.isEmpty(mMaterialDirectory)) {
            return vertexFile;
        }
        return mMaterialDirectory + File.separator + vertexFile;
    }

    /**
     * get the fragment shader file name in the material file
     * 
     * @return
     */
    public String getMaterialFragmentName() {
        String fragmentFile = property.getProperty(Key.FRAGMENT_FILE);
//        LogHelper.d(TAG, LogHelper.getThreadName() + " fragmentFile-" + fragmentFile);
        if (TextUtils.isEmpty(mMaterialDirectory)) {
            return fragmentFile;
        }
        return mMaterialDirectory + File.separator + fragmentFile;
    }

    /**
     * get the version of the material file
     * 
     * @return
     */
    public String getMaterialVersion() {
        String version = property.getProperty(Key.VERSION);
//        LogHelper.d(TAG, LogHelper.getThreadName() + " version-" + version);
        return version;
    }

    public void setMaterialProperty(String key, float value) {
        if (key.startsWith("u")) {// uniform
            mMaterialUniformMap.put(key, String.valueOf(value));
        } else if (key.startsWith("a")) {// attribute

        }
    }

    public HashMap<String, String> getMaterialUniformMapFromClient() {
//        LogHelper.d(TAG, LogHelper.getThreadName() + " mMaterialUniformMap-" + mMaterialUniformMap.toString());
        return mMaterialUniformMap;
    }

    public HashMap<String, String> getMaterialUniformKeyAndType() {
        HashMap<String, String> result = new HashMap<String, String>();
        String[] typeKeys = getMaterialUniforms();
        if (typeKeys.length == 0) {
//            LogHelper.d(TAG, LogHelper.getThreadName() + " there is no uniform key");
            return null;
        }
        for (int i = 0; i < typeKeys.length; i++) {
            String typeKey = typeKeys[i];
//            LogHelper.d(TAG, LogHelper.getThreadName() + " typeKey-" + typeKey);
            String[] typeAndKey = typeKey.split("_");
//            LogHelper.d(TAG, LogHelper.getThreadName() + " typeAndKey[0]-" + typeAndKey[0] + " typeAndKey[1]-" + typeAndKey[1]);
            String type = typeAndKey[0];
            String key = typeAndKey[1];
            result.put(key, type);
        }
//        LogHelper.d(TAG, LogHelper.getThreadName() + " result-" + result.toString());
        return result;
    }

    public String[] getMaterialUniforms() {
//        LogHelper.d(TAG, LogHelper.getThreadName());
        String uniform = getMaterialUniformFromFile();
        String[] typeKey = uniform.split(",");
//        LogHelper.d(TAG, LogHelper.getThreadName() + " typeKey length-" + typeKey.length);
        return typeKey;
    }

    public String getMaterialUniformFromFile() {
        String uniform = property.getProperty(Key.UNIFORM);
//        LogHelper.d(TAG, LogHelper.getThreadName() + " uniform-" + uniform);
        return uniform;
    }
}
