package com.vfs.fingerprint.base

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vfs.fingerprint.R
import kotlinx.android.synthetic.main.activity_enrollment.*
import kotlinx.android.synthetic.main.activity_enrollment.txtViewUserIcon
import kotlinx.android.synthetic.main.activity_login.*

class EnrollmentActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.lnrLytNewEnroll -> onNewEnrollmentClick()
        }
    }

    private fun onNewEnrollmentClick() {
        var intent = Intent(EnrollmentActivity@ this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrollment)
        lnrLytNewEnroll.setOnClickListener(this)

//        setFont()
    }

    private fun setFont() {
//        val typeFace = Typeface.createFromAsset(assets, "fonts/fontawesome-webfont.ttf")
//        txtViewUserIcon.typeface = typeFace
    }


}
