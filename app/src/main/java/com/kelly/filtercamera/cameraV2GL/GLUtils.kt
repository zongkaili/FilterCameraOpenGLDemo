package com.kelly.filtercamera.cameraV2GL

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.microedition.khronos.opengles.GL10



/**
 * Created by zongkaili on 2017/11/10.
 */
class GLUtils {

    companion object {
        /**
         * 创建OES纹理
         */
        fun createOESTextureObject(): Int {
            val tex = IntArray(1)
            GLES20.glGenTextures(1, tex, 0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            return tex[0]
        }

        fun readShaderFromResource(context: Context, resourceId: Int): String {
            val builder = StringBuilder()
            var `is`: InputStream? = null
            var isr: InputStreamReader? = null
            var br: BufferedReader? = null
            try {
                `is` = context.resources.openRawResource(resourceId)
                isr = InputStreamReader(`is`!!)
                br = BufferedReader(isr)
//                var line: String
//                while ((line = br.readLine()) != null) {
//                    builder.append(line + "\n")
//                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    if (`is` != null) {
                        `is`.close()
                        `is` = null
                    }
                    if (isr != null) {
                        isr.close()
                        isr = null
                    }
                    if (br != null) {
                        br.close()
                        br = null
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return builder.toString()
        }
    }

}