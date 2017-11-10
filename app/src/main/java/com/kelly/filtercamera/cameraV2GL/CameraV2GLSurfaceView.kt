package com.kelly.filtercamera.cameraV2GL

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * Created by zongkaili on 2017/11/9.
 */
class CameraV2GLSurfaceView(context: Context) : GLSurfaceView(context) {
    companion object {
        val TAG = CameraV2GLSurfaceView::class.java.simpleName
    }

    private var mCameraV2Renderer: CameraV2Renderer? = null

    fun init(context: Context, cameraV2Helper: CameraV2Helper, isPreviewStarted: Boolean) {
        //配置OpenGL ES: 版本设置
        setEGLContextClientVersion(2)
        //设置Renderer，Renderer用于执行OpenGL的绘制
        mCameraV2Renderer = CameraV2Renderer()
        mCameraV2Renderer?.init(context, this, cameraV2Helper, isPreviewStarted)
        setRenderer(mCameraV2Renderer)
    }

}