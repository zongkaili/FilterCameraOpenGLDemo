package com.kelly.filtercamera.cameraV2GL

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by zongkaili on 2017/11/10.
 */
class CameraV2Renderer: GLSurfaceView.Renderer {
    companion object {
        val TAG = CameraV2Renderer::class.java.simpleName
    }
    private var mContext: Context? = null
    private var mCameraV2GLSurfaceView: CameraV2GLSurfaceView? = null
    private var mCameraV2Helper: CameraV2Helper? = null
    private var bIsPreviewStarted: Boolean = false
    private var mOESTextureId = -1
    private var mSurfaceTexture: SurfaceTexture? = null
    private val transformMatrix = FloatArray(16)
    private var mFilterEngine: FilterEngine? = null
    private var mDataBuffer: FloatBuffer? = null
    private var mShaderProgram = -1
    private var aPositionLocation = -1
    private var aTextureCoordLocation = -1
    private var uTextureMatrixLocation = -1
    private var uTextureSamplerLocation = -1
    private val mFBOIds = IntArray(1)

    fun init(context: Context, surfaceView: CameraV2GLSurfaceView, cameraV2Helper: CameraV2Helper, isPreviewStarted: Boolean) {
        mContext = context
        mCameraV2GLSurfaceView = surfaceView
        mCameraV2Helper = cameraV2Helper
        bIsPreviewStarted = isPreviewStarted
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        //创建OES纹理
        mOESTextureId = Utils.createOESTextureObject()
        mFilterEngine = FilterEngine(mOESTextureId, mContext!!)
        mDataBuffer = mFilterEngine?.getBuffer()
        mShaderProgram = mFilterEngine?.getShaderProgram()!!
        glGenFramebuffers(1, mFBOIds, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, mFBOIds[0])
        Log.i(TAG, "onSurfaceCreated: mFBOId: " + mFBOIds[0])
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Log.i(TAG, "onSurfaceChanged: $width, $height")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDrawFrame(p0: GL10?) {
        val t1 = System.currentTimeMillis()
        /**
         * 更新数据，其实也是消耗数据，
         * 将上一帧的数据处理或者抛弃掉，
         * 要不然SurfaceTexture是接收不到最新数据
         */
        if (mSurfaceTexture != null) {
            mSurfaceTexture?.updateTexImage()
            mSurfaceTexture?.getTransformMatrix(transformMatrix)
        }

        if (!bIsPreviewStarted) {
            bIsPreviewStarted = initSurfaceTexture()
            bIsPreviewStarted = true
            return
        }

        //glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        aPositionLocation = glGetAttribLocation(mShaderProgram, FilterEngine.POSITION_ATTRIBUTE)
        aTextureCoordLocation = glGetAttribLocation(mShaderProgram, FilterEngine.TEXTURE_COORD_ATTRIBUTE)
        uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_MATRIX_UNIFORM)
        uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_SAMPLER_UNIFORM)

        glActiveTexture(GL_TEXTURE_EXTERNAL_OES)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId)
        glUniform1i(uTextureSamplerLocation, 0)
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)

        if (mDataBuffer != null) {
            mDataBuffer?.position(0)
            glEnableVertexAttribArray(aPositionLocation)
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mDataBuffer)

            mDataBuffer?.position(2)
            glEnableVertexAttribArray(aTextureCoordLocation)
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, mDataBuffer)
        }

        //glDrawElements(GL_TRIANGLE_FAN, 6,GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_TRIANGLE_FAN, 0 , 6);
        glDrawArrays(GL_TRIANGLES, 0, 6)
        //glDrawArrays(GL_TRIANGLES, 3, 3);
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        val t2 = System.currentTimeMillis()
        val t = t2 - t1
        Log.i(TAG, "onDrawFrame: time: " + t)
    }

    /**
     * 根据OES纹理id创建SurfaceTexture
     * 用来接收Camera预览数据
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initSurfaceTexture(): Boolean {
        if (mCameraV2Helper == null || mCameraV2GLSurfaceView == null) {
            Log.i(TAG, "mCamera or mGLSurfaceView is null!")
            return false
        }
        mSurfaceTexture = SurfaceTexture(mOESTextureId)
        //当SurfaceTexture接收到一帧数据时，请求OpenGL ES进行渲染
        mSurfaceTexture?.setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener {
            mCameraV2GLSurfaceView?.requestRender() })
        mCameraV2Helper?.setPreviewTexture(mSurfaceTexture!!)
        //开始camera预览
        mCameraV2Helper?.startPreview()
        return true
    }
}