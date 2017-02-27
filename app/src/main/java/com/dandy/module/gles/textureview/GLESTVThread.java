package com.dandy.module.gles.textureview;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;

import com.dandy.helper.android.LogHelper;
import com.dandy.helper.gles.IGLESRenderer;
import com.dandy.helper.java.PendingThreadAider;

/**
 * <pre>
 * TextureView中要用的GLThread
 * <a href="http://www.cnblogs.com/kiffa/archive/2013/02/21/2921123.html">一个很好的EGL相关学习参考</a>
 * 一般在Android中使用OpenGL ES，总是会从GLSurfaceView和Renderer开始，只需要提供一个合适的SurfaceHolder，就可以完成整个环境初始化，并进行绘制。
 * GLSurfaceView和Renderer事实上只是在本文描述的基础上封装了一些便利的功能，便于开发者开发，比如渲染同步、状态控制、主（渲染）循环等
 * </pre>
 * 
 * @author flycatdeng
 */
class GLESTVThread extends Thread {
    private static final String TAG = "GLESTVThread";
    private SurfaceTexture mSurfaceTexture;
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay = EGL10.EGL_NO_DISPLAY;// 显示设备
    private EGLSurface mEglSurface = EGL10.EGL_NO_SURFACE;
    private EGLContext mEglContext = EGL10.EGL_NO_CONTEXT;
//    private GL mGL;
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private IGLESRenderer mRenderer;
    private PendingThreadAider mPendingThreadAider = new PendingThreadAider();
    private boolean mNeedRenderring = true;
    private Object LOCK = new Object();
    private boolean mIsPaused = false;

    public GLESTVThread(SurfaceTexture surface, IGLESRenderer renderer) {
        mSurfaceTexture = surface;
        mRenderer = renderer;
    }

    @Override
    public void run() {
        LogHelper.d(TAG, LogHelper.getThreadName());
        initGLESContext();
        mRenderer.onSurfaceCreated();
        while (mNeedRenderring) {
            mPendingThreadAider.runPendings();// 执行未执行的，或要执行的事件。（后期可以开放以便模仿GLSurfaceView的queueEvent(Runnable r)）
            mRenderer.onDrawFrame();// 绘制
            // 一帧完成之后，调用eglSwapBuffers(EGLDisplay dpy, EGLContext ctx)来显示
            mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);// 这一句不能少啊，少了就GG了，一片空白
            // 1.凡是onPause都要停止，2.如果是onResume的状态，如果是循环刷新则会继续下一次循环，否则会暂停等待调用requestRender()
            if (mIsPaused) {
                pauseWhile();
            } else if (mRendererMode == GLESTextureView.RENDERMODE_WHEN_DIRTY) {
                pauseWhile();
            }
        }
        destoryGLESContext();
    }

    private void initGLESContext() {
        LogHelper.d(TAG, LogHelper.getThreadName());
        mEgl = (EGL10) EGLContext.getEGL();
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);// 获取显示设备
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetdisplay failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {// //version中存放EGL 版本号，int[0]为主版本号，int[1]为子版本号
            throw new RuntimeException("eglInitialize failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        // 构造需要的特性列表
        int[] configAttribs = { //
        EGL10.EGL_BUFFER_SIZE, 32,//
                EGL10.EGL_ALPHA_SIZE, 8, // 指定Alpha大小，以下四项实际上指定了像素格式
                EGL10.EGL_BLUE_SIZE, 8, // 指定B大小
                EGL10.EGL_GREEN_SIZE, 8,// 指定G大小
                EGL10.EGL_RED_SIZE, 8,// 指定RGB中的R大小（bits）
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,// 指定渲染api类别,这里或者是硬编码的4，或者是EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, EGL10.EGL_NONE// 总是以EGL10.EGL_NONE结尾
        };

        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        // eglChooseConfig(display, attributes, configs, num, configNum);
        // 用于获取满足attributes的所有config，参数1、2其意明显，参数3用于存放输出的configs，参数4指定最多输出多少个config，参数5由EGL系统写入，表明满足attributes的config一共有多少个
        if (!mEgl.eglChooseConfig(mEglDisplay, configAttribs, configs, 1, numConfigs)) {// 获取所有满足attributes的configs,并选择一个
            throw new RuntimeException("eglChooseConfig failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] contextAttribs = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};// attrib_list,目前可用属性只有EGL_CONTEXT_CLIENT_VERSION, 1代表OpenGL ES 1.x,
                                                                                // 2代表2.0。同样在Android4.2之前，没有EGL_CONTEXT_CLIENT_VERSION这个属性，只能使用硬编码0x3098代替
        mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, // share_context,是否有context共享，共享的contxt之间亦共享所有数据。EGL_NO_CONTEXT代表不共享
                contextAttribs);// 创建context
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], mSurfaceTexture,// 负责对Android Surface的管理
                null// Surface属性
                );// 获取显存，create a new EGL window surface
        if (mEglSurface == EGL10.EGL_NO_SURFACE || mEglContext == EGL10.EGL_NO_CONTEXT) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                throw new RuntimeException("eglCreateWindowSurface returned  EGL_BAD_NATIVE_WINDOW. ");
            }
            throw new RuntimeException("eglCreateWindowSurface failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {// 设置为当前的渲染环境
            throw new RuntimeException("eglMakeCurrent failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
//        mGL = mEglContext.getGL();
    }

    private void pauseWhile() {
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void destoryGLESContext() {
        LogHelper.d(TAG, LogHelper.getThreadName());
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEglContext = EGL10.EGL_NO_CONTEXT;
        mEglSurface = EGL10.EGL_NO_SURFACE;
    }

    public void onPause() {
        mRenderer.onPause();
        mIsPaused = true;
    }

    public void onResume() {
        mRenderer.onResume();
        mIsPaused = false;
        requestRender();
    }

    public void onSurfaceChanged(final int width, final int height) {
        mPendingThreadAider.addToPending(new Runnable() {// 在GL线程中执行

                    @Override
                    public void run() {
                        mRenderer.onSurfaceChanged(width, height);
                    }
                });
    }

    private int mRendererMode = GLESTextureView.RENDERMODE_CONTINUOUSLY;

    public void setRenderMode(int mode) {
        mRendererMode = mode;
    }

    public void requestRender() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    public void onDestroy() {
        mNeedRenderring = false;
        mRenderer.onDestroy();
        destoryGLESContext();
    }
}
