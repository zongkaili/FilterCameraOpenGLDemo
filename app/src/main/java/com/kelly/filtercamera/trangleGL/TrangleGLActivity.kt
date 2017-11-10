package com.kelly.filtercamera.trangleGL

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by zongkaili on 2017/11/10.
 */
class TrangleGLActivity: AppCompatActivity() {
    private lateinit var mGLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGLView = MyGLSurfaceView(this)
        setContentView(mGLView)
    }
}