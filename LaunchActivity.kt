package com.simplenursing.simplenursing.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.simplenursing.simplenursing.MainActivity
import com.simplenursing.simplenursing.Prefs
import com.simplenursing.simplenursing.R
import com.simplenursing.simplenursing.ui.login.LoginActivity

class LaunchActivity : AppCompatActivity() {

    lateinit var prefs: Prefs
    private var handler = Handler()
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

//        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_launch)

        prefs = Prefs(this)
        if (prefs.getString("token") != "null") {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            runnable = Runnable {
                startActivity(intent)
            }
            handler.postDelayed(runnable, 2000)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            runnable = Runnable {
                startActivity(intent)
            }
            handler.postDelayed(runnable, 2000)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.back_in, R.anim.back_out)
    }

    fun backPressed(view: View) {
        onBackPressed()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }
}