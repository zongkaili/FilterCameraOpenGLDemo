package com.kelly.filtercamera.trangleGL

import android.opengl.GLES20

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

import android.opengl.GLES20.GL_FLOAT

/**
 * Created by lb6905 on 2017/6/30.
 */

class Triangle {
    private val vertexBuffer: FloatBuffer
    private val mProgram: Int

    private var mPositionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0

    private val vertexCount = triangleCoords.size
    private val vertexStride = COORDS_PER_VERTEX * 4

    /*private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";*/

    /**
     * 用于渲染形状的顶点的OpenGL ES图形代码
     */
    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}"

    /**
     * 用于渲染形状的外观（颜色或纹理）的OpenGL ES代码
     */
    private val fragmentShaderCode = (
            "precision mediump float;"
                    + "uniform vec4 vColor;"
                    + "void main() {"
                    + "  gl_FragColor = vColor;"
                    + "}")

    internal var color = floatArrayOf(0.0f, 1f, 0f, 1.0f)

    init {
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())

        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)

        //编译shader代码
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        //创建空的OpenGL ES Program : 一个OpenGL ES对象，包含了你想要用来绘制一个或多个形状的shader
        mProgram = GLES20.glCreateProgram()
        //将vertex/fragment shader添加到program
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        //创建可执行的 OpenGL ES program
        GLES20.glLinkProgram(mProgram)
    }

    fun draw(mvpMatrix: FloatArray) {
        // 添加program到OpenGL ES环境中
        GLES20.glUseProgram(mProgram)
        //获取指向vertex shader的成员vPosition的handle
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        //启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false, vertexStride, vertexBuffer)
        //获取指向fragment shader的成员vColor的handle
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")//此方法名若写错，则绘制出来的图形颜色全是默认黑色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        //绘制三角形
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        //禁用指向三角形的顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object {

        internal val COORDS_PER_VERTEX = 3

        internal var triangleCoords = floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f)

        fun loadShader(type: Int, shaderCode: String): Int {
            /**
             *  创建一个vertex shader类型(GLES20.GL_VERTEX_SHADER)
             *  或一个fragment shader类型(GLES20.GL_FRAGMENT_SHADER)
             */
            val shader = GLES20.glCreateShader(type)

            /**
             * 将源码添加到shader并编译它
             */
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)

            return shader
        }
    }
}
