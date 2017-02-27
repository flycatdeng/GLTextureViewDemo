package com.dandy.gldemo.glestextureview;

import android.content.Context;
import android.util.AttributeSet;

import com.dandy.module.gles.textureview.GLESTextureView;

/**
 * 调用方法类似于GLSurfaceView的了
 * 
 * @author flycatdeng
 * 
 */
public class DemoGlesTextureView extends GLESTextureView {
    protected static final String TAG = "DemoGlesTextureView";

    public DemoGlesTextureView(Context context) {
        super(context);
        init(context);
    }

    public DemoGlesTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        GLESRendererImpl renerer = new GLESRendererImpl(context);
        setRenderer(renerer);
//        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
