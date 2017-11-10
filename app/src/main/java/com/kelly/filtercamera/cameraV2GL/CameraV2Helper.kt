package com.kelly.filtercamera.cameraV2GL

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.util.Size
import java.util.*
import android.view.Surface


/**
* Created by zongkaili on 2017/11/10.
*/
class CameraV2Helper(activity: Activity) {
    companion object {
        val TAG = CameraV2Helper::class.java.simpleName!!
    }
    private val mActivity: Activity = activity
    private lateinit var mHandlerThread: HandlerThread
    private lateinit var mCameraHandler: Handler
    private var mPreviewSize: Size? = null
    private var mCameraId: String? = null
    private var mCameraDevice: CameraDevice? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mCaptureRequestBuilder: CaptureRequest.Builder? = null
    private var mCaptureRequest: CaptureRequest? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    init {
        startCameraThread()
    }

    private fun startCameraThread() {
        mHandlerThread = HandlerThread("CameraThread")
        mHandlerThread.start()
        mCameraHandler = Handler(mHandlerThread.looper)
    }


    /**
     * 设置相机参数
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setupCamera(width: Int, height: Int): String? {
         val cameraManager: CameraManager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraManager.cameraIdList.forEach {
                it -> val characteristics = cameraManager.getCameraCharacteristics(it)
                //打开后置摄像头
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    return@forEach
                }
                //获取StreamConfigurationMap: 管理摄像头支持的所有输出格式和尺寸
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
                mCameraId = it
                Log.d(TAG, "preview width = " + mPreviewSize?.width + ", height = " + mPreviewSize?.height + ", cameraId = " + mCameraId)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return mCameraId
    }

    /**
     *
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openCamera(): Boolean {
        val cameraManager: CameraManager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        try {
            cameraManager.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private var mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            //摄像头打开了，此处拿到具体的摄像头设备
            mCameraDevice = camera
        }

        override fun onDisconnected(camera: CameraDevice?) {
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            mCameraDevice = null
        }

    }

    fun setPreviewTexture(surfaceTexture: SurfaceTexture) {
        mSurfaceTexture = surfaceTexture
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            /**
     * 开启camera预览
     */
    fun startPreview() {
        mSurfaceTexture?.setDefaultBufferSize(mPreviewSize?.width!!, mPreviewSize?.height!!)
        val surface = Surface(mSurfaceTexture)
        //创建preview捕获请求
        try {
            //创建preview捕获请求
            mCaptureRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            //将此请求输出目标设为Surface对象，这个Surface对象也必须添加给createCaptureSession才行
            mCaptureRequestBuilder?.addTarget(surface)
            /**
             * 创建捕获会话，第一个参数是捕获数据的输出Surface列表，
             * 第二个参数是CameraCaptureSession的状态回调接口，
             * 当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
             */
            mCameraDevice?.createCaptureSession(Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession?) {
                    //构建捕获请求
                    mCaptureRequest = mCaptureRequestBuilder?.build()
                    mCameraCaptureSession = session
                    /**
                     * 设置重复捕获数据的请求，
                     * 之后surface绑定的SurfaceTexture中就会一直有数据到达，
                     * 然后就会回调SurfaceTexture.OnFrameAvailableListener接口
                     */
                    mCameraCaptureSession?.setRepeatingRequest(mCaptureRequest,null, mCameraHandler)
                }

                override fun onConfigureFailed(p0: CameraCaptureSession?) {

                }
            },mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 根据屏幕尺寸匹配最合适的预览尺寸
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getOptimalSize (sizeMap: Array<Size>, width: Int, height: Int): Size? {
        val sizeList = ArrayList<Size>()
        sizeMap.forEach {
            it ->
            if (width > height) {
                 if (it.width > width && it.height > height) {
                     sizeList.add(it)
                 } else {
                     if (it.width > height && it.height > height) {
                         sizeList.add(it)
                     }
                 }
            }
        }
        if (sizeList.size > 0) {
            return Collections.min(sizeList) { lhs, rhs ->
                java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
            }
        }
        return sizeMap[0]
    }
}