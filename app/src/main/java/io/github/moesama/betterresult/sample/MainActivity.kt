package io.github.moesama.betterresult.sample

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import io.github.moesama.betterresult.requestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val res = requestResult("appUsage", App.appUsageContracts).toString()
                ActivityResultContracts.OpenDocument()
                findViewById<TextView>(R.id.textView).text = res
                Toast.makeText(this@MainActivity, res, Toast.LENGTH_SHORT).show()
            }
        }
    }
}