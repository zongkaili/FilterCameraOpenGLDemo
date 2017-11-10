package com.kelly.filtercamera

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kelly.filtercamera.cameraV2GL.CameraV2Activity
import com.kelly.filtercamera.trangleGL.TrangleGLActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            startActivity(Intent(this, CameraV2Activity::class.java))
        }

        triangle.setOnClickListener {
            startActivity(Intent(this, TrangleGLActivity::class.java))
        }
    }
}
