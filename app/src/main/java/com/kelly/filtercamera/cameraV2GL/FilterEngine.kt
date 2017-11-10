package com.kelly.filtercamera.cameraV2GL

import android.content.Context
import android.opengl.GLES11Ext

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_TRIANGLES
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glBindTexture
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetError
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniformMatrix4fv
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES20.glVertexAttribPointer
import com.kelly.filtercamera.R

/**
 * Created by lb6905 on 2017/6/12.
 */

class FilterEngine(OESTextureId: Int, private val mContext: Context) {
    private val buffer: FloatBuffer?
    var oesTextureId = -1
    private var vertexShader = -1
    private var fragmentShader = -1

    private var shaderProgram = -1

    private var aPositionLocation = -1
    private var aTextureCoordLocation = -1
    private var uTextureMatrixLocation = -1
    private var uTextureSamplerLocation = -1

    init {
        oesTextureId = OESTextureId
        buffer = createBuffer(vertexData)
        vertexShader = loadShader(GL_VERTEX_SHADER, Utils.readShaderFromResource(mContext, R.raw.base_vertex_shader))
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, Utils.readShaderFromResource(mContext, R.raw.base_fragment_shader))
        shaderProgram = linkProgram(vertexShader, fragmentShader)
    }

    fun createBuffer(vertexData: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(vertexData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        buffer.put(vertexData, 0, vertexData.size).position(0)
        return buffer
    }

    fun loadShader(type: Int, shaderSource: String): Int {
        val shader = glCreateShader(type)
        if (shader == 0) {
            throw RuntimeException("Create Shader Failed!" + glGetError())
        }
        glShaderSource(shader, shaderSource)
        glCompileShader(shader)
        return shader
    }

    fun linkProgram(verShader: Int, fragShader: Int): Int {
        val program = glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Create Program Failed!" + glGetError())
        }
        glAttachShader(program, verShader)
        glAttachShader(program, fragShader)
        glLinkProgram(program)

        glUseProgram(program)
        return program
    }

    fun drawTexture(transformMatrix: FloatArray) {
        aPositionLocation = glGetAttribLocation(shaderProgram, POSITION_ATTRIBUTE)
        aTextureCoordLocation = glGetAttribLocation(shaderProgram, TEXTURE_COORD_ATTRIBUTE)
        uTextureMatrixLocation = glGetUniformLocation(shaderProgram, TEXTURE_MATRIX_UNIFORM)
        uTextureSamplerLocation = glGetUniformLocation(shaderProgram, TEXTURE_SAMPLER_UNIFORM)

        glActiveTexture(GL_TEXTURE_EXTERNAL_OES)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)
        glUniform1i(uTextureSamplerLocation, 0)
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)

        if (buffer != null) {
            buffer.position(0)
            glEnableVertexAttribArray(aPositionLocation)
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, buffer)

            buffer.position(2)
            glEnableVertexAttribArray(aTextureCoordLocation)
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, buffer)

            glDrawArrays(GL_TRIANGLES, 0, 6)
        }
    }

    fun getShaderProgram(): Int {
        return shaderProgram
    }

    fun getBuffer(): FloatBuffer {
        return buffer!!
    }

    fun getOESTextureId(): Int {
        return oesTextureId
    }

    fun setOESTextureId(OESTextureId: Int) {
        oesTextureId = OESTextureId
    }

    companion object {

        private val filterEngine: FilterEngine? = null

        /*public static FilterEngine getInstance() {
        if (filterEngine == null) {
            synchronized (FilterEngine.class) {
                if (filterEngine == null)
                    filterEngine = new FilterEngine();
            }
        }
        return filterEngine;
    }*/

        private val vertexData = floatArrayOf(1f, 1f, 1f, 1f, -1f, 1f, 0f, 1f, -1f, -1f, 0f, 0f, 1f, 1f, 1f, 1f, -1f, -1f, 0f, 0f, 1f, -1f, 1f, 0f)

        val POSITION_ATTRIBUTE = "aPosition"
        val TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate"
        val TEXTURE_MATRIX_UNIFORM = "uTextureMatrix"
        val TEXTURE_SAMPLER_UNIFORM = "uTextureSampler"
    }
}

