package co.pl.plsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class SampleLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_launcher)

        handleLaunchers()
    }

    private fun handleLaunchers() {
        findViewById<AppCompatButton>(R.id.serviceBased).setOnClickListener {
            startActivity(Intent(this, ServiceBasedIntegrationActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.broadcastBased).setOnClickListener {
            startActivity(Intent(this, BroadcastBasedIntegrationActivity::class.java))
        }
    }
}