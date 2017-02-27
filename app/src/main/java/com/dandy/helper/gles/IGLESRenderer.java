package com.dandy.helper.gles;

/**
 * <pre>
 * GLES里用到的用于渲染的一个接口。
 * 如果是GLSurfaceView要用到，则其对应的GLSurfaceView.Renderer可以来调用IGLESRenderer的实现类来实现逻辑
 * 如果是TextureView要用到，则使用自定义的一个线程里调用IGLESRenderer的实现类来做一个类似于GLSurfaceView.Renderer的操作
 * 所以IGLESRenderer中的方法都要在GL线程里运行（TextureView创建一个线程，把它当做一个GL线程）
 * </pre>
 * 
 * @author flycatdeng
 * 
 */
public interface IGLESRenderer {
    /**
     * <pre>
     * Surface创建好之后
     * </pre>
     */
    public void onSurfaceCreated();

    /**
     * <pre>
     * 界面大小有更改
     * </pre>
     * 
     * @param width
     * @param height
     */
    public void onSurfaceChanged(int width, int height);

    /**
     * <pre>
     * 绘制每一帧
     * </pre>
     */
    public void onDrawFrame();

    /**
     * <pre>
     * Activity的onResume时的操作
     * </pre>
     */
    public void onResume();

    /**
     * <pre>
     * Activity的onPause时的操作
     * </pre>
     */
    public void onPause();

    /**
     * <pre>
     * Activity的onDestroy时的操作
     * </pre>
     */
    public void onDestroy();

}
