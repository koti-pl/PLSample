package co.pl.plsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import kotlin.system.exitProcess

class SampleLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_sample_launcher)
        handleLaunchers()
    }

    private fun handleLaunchers() {

        findViewById<AppCompatImageButton>(R.id.terminateApp).setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }

        findViewById<AppCompatButton>(R.id.serviceBased).setOnClickListener {
            startActivity(Intent(this, ServiceBasedIntegrationActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.broadcastBased).setOnClickListener {
            startActivity(Intent(this, BroadcastBasedIntegrationActivity::class.java))
        }
    }
}