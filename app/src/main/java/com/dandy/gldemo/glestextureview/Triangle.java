package com.dandy.gldemo.glestextureview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.dandy.helper.android.LogHelper;
import com.dandy.helper.gles.MaterialAider;
import com.dandy.helper.gles.ShaderHelper;

/**
 * 这个三角形及其用到的材质都是自写的，没有用到OpenGL内建变量，就连总变换矩阵，顶点坐标以及颜色等都是由代码传进去的
 * 
 * @author dengchukun 2016年11月26日
 */
public class Triangle {
    private Context mContext;
    private MatrixAid mMatrix = new MatrixAid();
    private int mVertexCount = 0;
    // 之所以要有缓存数据是因为绘制每一帧图像的时候，如果缓存已经有数据了，就不需要每次都将顶点数据通过GL传进去了，这样就可以省好多IO带宽
    private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;// 顶点着色数据缓冲
    private int mProgram;// 自定义渲染管线程序id
    private int muMVPMatrixHandle;// 总变换矩阵引用id
    private int maPositionHandle; // 顶点位置属性引用id
    private int maColorHandle; // 顶点颜色属性引用id

    public Triangle(Context context) {
        mContext = context;
        // 初始化顶点坐标与着色数据
        initVertexData();
        // 初始化shader
        initShader();
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    private void initVertexData() {
        // 顶点坐标数据的初始化
        mVertexCount = 3;
        final float UNIT_SIZE = 0.2f;
        float vertices[] = new float[]// 首先将顶点此属性数据一次存放入数组，这里是顶点坐标
        { //
        -4 * UNIT_SIZE, 0, 0, // 第1个顶点的XYZ坐标值
                0, -4 * UNIT_SIZE, 0, // // 第2个顶点的XYZ坐标值
                4 * UNIT_SIZE, 0, 0// // 第3个顶点的XYZ坐标值
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);// 开辟对应容量的缓冲
        vbb.order(ByteOrder.nativeOrder());// 设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();// 浮点型缓冲
        mVertexBuffer.put(vertices);// 将数组中的顶点数据送入缓冲
        mVertexBuffer.position(0);// 设置缓冲的其实位置

        float colors[] = new float[]//
        { //
                1, 1, 1, 0, //
                0, 0, 1, 0, //
                0, 1, 0, 0//
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    private void initShader() {
        MaterialAider mat = new MaterialAider(mContext);
        mat.setMaterialName("GLESDemo/SimpleTriangle/gles_triangle.mat");
        mProgram = ShaderHelper.getProgramFromAsset(mContext, mat);
        // 获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中顶点颜色属性引用id
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        // 获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    public void onDrawSelf() {
        LogHelper.d("Triangle", "onDrawSelf");
        // 制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        // 初始化变换矩阵
        Matrix.setRotateM(mMatrix.mMMatrix, 0, 0, 0, 1, 0);
        // 设置沿Z轴正向位移1
        Matrix.translateM(mMatrix.mMMatrix, 0, 0, 0, 1);
        // 设置绕x轴旋转
        Matrix.rotateM(mMatrix.mMMatrix, 0, 30, 1, 0, 0);
        //
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMatrix.getFianlMatrix(mMatrix.mMMatrix), 0);
        // 主要分为三步，1.获取着色器对应属性变量的引用
        // 2.通过引用将缓存中的数据传入管线
        // 3.启用传入的数据
        // 为画笔指定顶点位置数据
        // glVertexAttribPointer将顶点坐标数据以及顶点颜色数据传送进渲染管线，以备渲染时在顶点着色器中使用
        GLES20.glVertexAttribPointer(// 将顶点位置数据传送进渲染管线
                maPositionHandle, // 顶点位置属性引用
                3, // 每顶点一组的数据个数（这里是X、Y、Z坐标，所以是3）
                GLES20.GL_FLOAT, // 数据类型
                false, // 是否格式化
                3 * 4, // 每组数据的尺寸，这里每组3个浮点数值（XYZ坐标），每个浮点数4个字节所以是3*4=12个字节
                mVertexBuffer// 存放了数据的缓存
        );
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);
        // 允许顶点位置数据数组
        // glEnableVertexAttribArray启用顶点位置数据和顶点颜色数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
    }

    /**
     * <pre>
     * 
     * </pre>
     * 
     * @param width
     * @param height
     */
    public void onSurfaceChanged(int width, int height) {
        // 计算GLSurfaceView的宽高比
        float ratio = (float) width / height;
        // 调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mMatrix.mProjMatrix, // 存储生成矩阵元素的float[]类型数组
                0, // 填充起始偏移量
                -ratio, ratio, // near面的left、right
                -1, 1, // near面的bottom、top
                1, 10// near面、far面与视点的距离
        );
        // 调用此方法产生摄像机9参数位置矩阵
        Matrix.setLookAtM(mMatrix.mVMatrix, // 存储生成矩阵元素的float[]类型数组
                0, // 填充起始偏移量
                0, 0, 3, // 摄像机位置的XYZ坐标
                0f, 0f, 0f, // 观察目标点XYZ坐标
                0.0f, 1.0f, 0.0f// up向量在XYZ轴上的分量
        );

    }

}
