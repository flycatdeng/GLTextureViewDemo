package com.dandy.module.gles.textureview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

import com.dandy.helper.gles.IGLESRenderer;

/**
 * <pre>
 * 一个类似于GLSurfaceView的TextureView，用于显示opengl
 *  {@link #setRenderer(IGLESRenderer)}类似于GLSurfaceView的setRenderer（Renderer）
 *  {{@link #setRenderMode(int)}类似于GLSurfaceView的setRenderMode(int)
 *  详细调用可模仿com.dandy.gldemo.glestextureview.DemoGlesTextureView
 *  没事可看<a href='http://blog.csdn.net/fuyajun01/article/details/8931647#' >参照1</>或者<a href="http://www.jianshu.com/p/b2d949ab1a1a">参照2</a>
 * </pre>
 * 
 * @author flycatdeng
 * 
 */
public class GLESTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private GLESTVThread mGLThread;
    private IGLESRenderer mRenderer;
    private int mRendererMode = RENDERMODE_CONTINUOUSLY;

    public GLESTextureView(Context context) {
        super(context);
        init(context);
    }

    public GLESTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * <pre>
     * 类似于GLSurfaceView的setRenderer
     * </pre>
     */
    public void setRenderer(IGLESRenderer renderer) {
        mRenderer = renderer;
    }

    /**
     * <pre>
     * 类似于GLSurfaceView的setRenderMode
     * 渲染模式，是循环刷新，还是请求的时候刷新
     * </pre>
     */
    public void setRenderMode(int mode) {
        mRendererMode = mode;
    }

    /**
     * Request that the renderer render a frame. This method is typically used when the render mode has been set to {@link #RENDERMODE_WHEN_DIRTY}, so
     * that frames are only rendered on demand. May be called from any thread. Must not be called before a renderer has been set.
     */
    public void requestRender() {
        if (mRendererMode != RENDERMODE_WHEN_DIRTY) {
            return;
        }
        mGLThread.requestRender();
    }

    private void init(Context context) {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mGLThread = new GLESTVThread(surface, mRenderer);// 创建一个线程，作为GL线程
        mGLThread.setRenderMode(mRendererMode);
        mGLThread.start();
        mGLThread.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mGLThread.onSurfaceChanged(width, height);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onResume() {
        if (mGLThread != null) {
            mGLThread.onResume();
        }
    }

    public void onPause() {
        if (mGLThread != null) {
            mGLThread.onPause();
        }
    }

    public void onDestroy() {
        if (mGLThread != null) {
            mGLThread.onDestroy();
        }
    }
}
