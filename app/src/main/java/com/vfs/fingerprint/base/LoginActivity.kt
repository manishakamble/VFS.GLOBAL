package com.vfs.fingerprint.base

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vfs.fingerprint.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtViewForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btnLogin.setOnClickListener(this)

        setFont()
    }

    private fun setFont() {
        val typeFace = Typeface.createFromAsset(assets, "fonts/fontawesome-webfont.ttf")
        txtViewlock.typeface = typeFace
        txtViewUserIcon.typeface = typeFace
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnLogin -> startHomeScreen()
        }
    }

    private fun startHomeScreen() {

        if (editTextEmail.text.toString().equals("admin") && editTextPasword.text.toString().equals("admin")) {
           // var intent = Intent(LoginActivity@ this, AdminScreenActivity::class.java)
            var intent = Intent(LoginActivity@ this, SettingsDynamic::class.java) //AdminPortalActivity
            startActivity(intent)
          //  finish()
        } else if (editTextEmail.text.toString().equals("vfs_bio") && editTextPasword.text.toString().equals("vfs")) {
            var intent = Intent(LoginActivity@ this, EnrollmentActivity::class.java)
            startActivity(intent)
          //  finish()
        } else {
            showToastMessage("Invalid Credentials")
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
