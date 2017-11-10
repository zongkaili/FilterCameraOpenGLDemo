package com.kelly.filtercamera.trangleGL

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * Created by zongkaili on 2017/11/10.
 */
class MyGLSurfaceView(context: Context): GLSurfaceView(context) {
    private lateinit var mRenderer: MyGLRenderer

    init {
        setEGLContextClientVersion(2)
        mRenderer = MyGLRenderer()
        setRenderer(mRenderer)

        //此模式下只有在绘制数据改变时才绘制view，可以防止GLSurfaceView帧重绘
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}