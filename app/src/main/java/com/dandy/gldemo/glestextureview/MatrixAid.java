package com.dandy.gldemo.glestextureview;

import android.opengl.Matrix;

/**
 * 
 * @author dengchukun 2016年11月26日
 */
public class MatrixAid {
    public float[] mProjMatrix = new float[16];// 4x4矩阵 投影用
    public float[] mVMatrix = new float[16];// 摄像机位置朝向9参数矩阵
    public float[] mMMatrix = new float[16];// 具体物体的移动旋转矩阵，旋转、平移
    public float[] mMVPMatrix;// 最后起作用的总变换矩阵

    public float[] getFianlMatrix(float[] spec) {
        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}
