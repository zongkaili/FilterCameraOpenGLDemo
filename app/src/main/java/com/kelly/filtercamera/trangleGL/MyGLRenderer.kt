package com.kelly.filtercamera.trangleGL

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by zongkaili on 2017/11/10.
 */
class MyGLRenderer : GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle
    private val mMVPMatrix = FloatArray(16)
    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)

    @Volatile
    var mAngle: Float = 0.toFloat()

    override fun onSurfaceCreated(p0: GL10?, config: EGLConfig?) {
        //设置背景色（r、g、b、a）:白色不透明
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        //初始化三角形
        mTriangle = Triangle()
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 3.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        //绘制窗口
        GLES20.glViewport(0, 0, width, height)
        //计算宽高比
        val ratio = height.toFloat() / width
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -1f, 1f, -ratio, ratio, 3f, 7f)

        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)

    }

    override fun onDrawFrame(p0: GL10?) {
        //重绘背景色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0f, 0f, -1.0f)
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0)

        mTriangle.draw(mMVPMatrix)
    }

}