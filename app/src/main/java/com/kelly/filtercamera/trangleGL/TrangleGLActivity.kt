package com.kelly.filtercamera.trangleGL

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent

/**
 * Created by zongkaili on 2017/11/10.
 */
class TrangleGLActivity : AppCompatActivity() {
    private lateinit var mGLView: GLSurfaceView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGLView = MyGLSurfaceView(this)
        setContentView(mGLView)

//        val mTouchShaderRender = OpenGLTouchShaderRender()
        mGLView.setOnTouchListener { v, event ->
            if (event != null) {
                val normalizedX = (event.x / v.width) * 2 - 1
                val normalizedY = -((event.y / v.height) * 2 - 1)

//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    mGLView.queueEvent {
//                        if (mTouchShaderRender != null)
//                            mTouchShaderRender.handleTouchPress(normalizedX, normalizedY)
//                    }
//                } else if (event.action == MotionEvent.ACTION_MOVE) {
//                    mGLView.queueEvent {
//                        if (mTouchShaderRender != null)
//                            mTouchShaderRender.handleTouchDrag(normalizedX, normalizedY)
//                    }
//                }
                true
            } else {
                false
            }
        }
    }
}