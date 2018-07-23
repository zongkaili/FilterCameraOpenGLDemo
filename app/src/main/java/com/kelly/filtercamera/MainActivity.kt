package com.kelly.filtercamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kelly.filtercamera.airhockey.AirHockeyActivity
import com.kelly.filtercamera.cameraV2GL.CameraV2Activity
import com.kelly.filtercamera.trangleGL.TrangleGLActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rxPermissions = RxPermissions(this)

        btn.setOnClickListener {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe { granted ->
                        if (granted) {
                            startActivity(Intent(this, CameraV2Activity::class.java))
                        } else {
                            Toast.makeText(this, "相机权限被拒绝，请前往设置界面开启相机权限.", Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        triangle.setOnClickListener {
            startActivity(Intent(this, TrangleGLActivity::class.java))
        }

        touch.setOnClickListener {
            startActivity(Intent(this, AirHockeyActivity::class.java))
        }

    }
}
