package com.dandy.gldemo.glestextureview;

import android.content.Context;
import android.opengl.GLES20;

import com.dandy.helper.android.LogHelper;
import com.dandy.helper.gles.IGLESRenderer;

/**
 * GLESRendererImpl可以被GLSurfaceView的Renderer和GLESTextureView复用
 * 
 * @author flycatdeng
 * 
 */
public class GLESRendererImpl implements IGLESRenderer {
    private static final String TAG = "GLESRendererImpl";
    private Context mContext;
    private Triangle mTriangle;

    public GLESRendererImpl(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated() {
        LogHelper.d(TAG, LogHelper.getThreadName());
        // 设置屏幕背景色RGBA
        GLES20.glClearColor(1, 0, 0, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mTriangle = new Triangle(mContext);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        LogHelper.d(TAG, LogHelper.getThreadName());
        // 设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);
        mTriangle.onSurfaceChanged(width, height);
    }

    @Override
    public void onResume() {
        // do something
    }

    @Override
    public void onPause() {
        // do something
    }

    @Override
    public void onDrawFrame() {// 绘制
        // 清除深度缓冲与颜色缓冲（就是清除缓存）
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.onDrawSelf();
    }

    @Override
    public void onDestroy() {
        // do something
    }
}
