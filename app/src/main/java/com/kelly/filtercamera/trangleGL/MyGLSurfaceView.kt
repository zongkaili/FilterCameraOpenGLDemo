package com.kelly.filtercamera.trangleGL

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.annotation.SuppressLint


/**
 * Created by zongkaili on 2017/11/10.
 */
class MyGLSurfaceView(context: Context): GLSurfaceView(context) {
    private lateinit var mRenderer: MyGLRenderer

    private val TOUCH_SCALE_FACTOR = 180.0f / 320
    private var mPreviousX: Float = 0.toFloat()
    private var mPreviousY: Float = 0.toFloat()

    init {
        setEGLContextClientVersion(2)
        mRenderer = MyGLRenderer()
        setRenderer(mRenderer)

        //此模式下只有在绘制数据改变时才绘制view，可以防止GLSurfaceView帧重绘
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        val x = e!!.x
        val y = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx = x - mPreviousX
                var dy = y - mPreviousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR  // = 180.0f / 320
                requestRender()
            }
        }

        mPreviousX = x
        mPreviousY = y
        return true
    }
}