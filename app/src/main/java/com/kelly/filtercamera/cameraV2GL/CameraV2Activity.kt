package com.kelly.filtercamera.cameraV2GL

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics

/**
 * Created by zongkaili on 2017/11/9.
 */
class CameraV2Activity: AppCompatActivity() {
    private lateinit var mCameraV2GLSurfaceView: CameraV2GLSurfaceView
    private lateinit var mCameraHelper: CameraV2Helper

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCameraV2GLSurfaceView = CameraV2GLSurfaceView(this)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mCameraHelper = CameraV2Helper(this)
        mCameraHelper.setupCamera(dm.widthPixels, dm.heightPixels)
        if (!mCameraHelper.openCamera()) {
            return
        }
        mCameraV2GLSurfaceView.init(this, mCameraHelper, false)
        setContentView(mCameraV2GLSurfaceView)
    }


}