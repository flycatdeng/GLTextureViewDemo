package com.dandy.helper.gles;

import java.io.IOException;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;

import com.dandy.helper.android.FileHelper;
import com.dandy.helper.android.LogHelper;

@SuppressLint("NewApi")
public class ShaderHelper {
    private static final String TAG = ShaderHelper.class.getSimpleName();

    /**
     * 
     * @param shaderType
     *            shader的类型 GLES20.GL_VERTEX_SHADER(顶点) GLES20.GL_FRAGMENT_SHADER(片元)
     * @param source
     *            shader的脚本字符串
     * @return
     */
    public static int getShader(int shaderType, String source) {
        // 创建一个新shader
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {// 创建shader不成功
            throw new RuntimeException(TAG + LogHelper.getThreadName() + " Error creating vertex shader.");
        } // 若创建成功则加载shader
          // 加载shader的源代码
        GLES20.glShaderSource(shader, source);
        // 编译shader
        GLES20.glCompileShader(shader);
        // 存放编译成功shader数量的数组
        int[] compiled = new int[1];
        // 获取Shader的编译情况
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {// 若编译失败则显示错误日志并删除此shader
            LogHelper.d(TAG + "_ES20_ERROR", "Could not compile shader " + shaderType + ":");
            LogHelper.d(TAG + "_ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    /**
     * 通过顶点着色器字符串得到顶点着色器ID
     * 
     * @param vertexSource
     *            顶点着色器字符串
     * @return
     */
    public static int getVertexShader(String vertexSource) {
        return getShader(GLES20.GL_VERTEX_SHADER, vertexSource);
    }

    /**
     * 通过片元着色器字符串得到片元着色器ID
     * 
     * @param fragmentSource
     *            片元着色器字符串
     * @return
     */
    public static int getFragmentShader(String fragmentSource) {
        return getShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
    }

    /**
     * 创建shader在gpu上运行的程序,从assets目录下获取
     * 
     * @param vertexSourcePath
     *            顶点着色器字符串在assets目录下的路径
     * @param fragmentSourcePath
     *            片元着色器字符串在assets目录下的路径
     * @return
     */
    public static int getProgramFromAsset(Context context, MaterialAider material) {
        String vertexSourcePath = material.getMaterialVertexName();
        String fragmentSourcePath = material.getMaterialFragmentName();
        return getProgramFromAsset(context, vertexSourcePath, fragmentSourcePath);
    }

    /**
     * 创建shader在gpu上运行的程序,从assets目录下获取
     * 
     * @param vertexSourcePath
     *            顶点着色器字符串在assets目录下的路径
     * @param fragmentSourcePath
     *            片元着色器字符串在assets目录下的路径
     * @return
     */
    public static int getProgramFromAsset(Context context, String materialPath) {
        String vertexSourcePath = "origin.vert";
        String fragmentSourcePath = "origin.frag";
        Properties property = new Properties();
        try {
            property.load(FileHelper.getInputStreamFromAsset(context, materialPath));
            vertexSourcePath = property.getProperty("VertextFile");
            fragmentSourcePath = property.getProperty("FragmentFile");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getProgramFromAsset(context, vertexSourcePath, fragmentSourcePath);
    }

    /**
     * 创建shader在gpu上运行的程序,从assets目录下获取
     * 
     * @param vertexSourcePath
     *            顶点着色器字符串在assets目录下的路径
     * @param fragmentSourcePath
     *            片元着色器字符串在assets目录下的路径
     * @return
     */
    public static int getProgramFromAsset(Context context, String vertexSourcePath, String fragmentSourcePath) {
        String vertexSource = FileHelper.getFileContentFromAsset(context, vertexSourcePath);
        String fragmentSource = FileHelper.getFileContentFromAsset(context, fragmentSourcePath);
//        LogHelper.d(TAG, LogHelper.getThreadName() + "vertexSource-" + vertexSource);
//        LogHelper.d(TAG, LogHelper.getThreadName() + "fragmentSource-" + fragmentSource);
        return getProgram(vertexSource, fragmentSource);
    }

    /**
     * 创建shader在gpu上运行的程序
     * 
     * @param vertexSource
     *            顶点着色器字符串
     * @param fragmentSource
     *            片元着色器字符串
     * @return
     */
    public static int getProgram(String vertexSource, String fragmentSource) {
        int vertexShader = getVertexShader(vertexSource);
        int fragmentShader = getFragmentShader(fragmentSource);
        return getProgram(vertexShader, fragmentShader);
    }

    /**
     * 创建shader在gpu上运行的程序
     * 
     * @param vertexShader
     *            顶点着色器的ID
     * @param fragShader
     *            片元着色器的ID
     * @return
     */
    public static int getProgram(int vertexShader, int fragShader) {
        if (vertexShader == 0) {
            return 0;
        }
        if (fragShader == 0) {
            return 0;
        }
        // 创建程序
        int program = GLES20.glCreateProgram();// 创建程序，在GPU上跑，返回其ID
        if (program == 0) {// 创建程序失败
            throw new RuntimeException("Error creating program.");
        } // 创建程序成功
          // 加入着色器
        GLES20.glAttachShader(program, vertexShader);// 向程序中加入顶点着色器
        CommonUtils.checkGlError("glAttachShader vertexShader");
        GLES20.glAttachShader(program, fragShader);// 向程序中加入片元点着色器
        CommonUtils.checkGlError("glAttachShader fragShader");
        // 链接程序
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1]; // 存放链接成功program数量的数组
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0); // 获取program的链接情况
        // 若链接失败则报错并删除程序
        if (linkStatus[0] != GLES20.GL_TRUE) {
            LogHelper.d(TAG + "_ES20_ERROR", "Could not link program: ");
            LogHelper.d(TAG + "_ES20_ERROR", GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
            throw new RuntimeException("Error creating program.");
        }
        LogHelper.d(TAG, LogHelper.getThreadName() + "program ID=" + program);
        return program;
    }

}
