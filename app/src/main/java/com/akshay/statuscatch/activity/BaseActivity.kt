package com.akshay.statuscatch.activity

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * BaseActivity enables edge-to-edge and applies system bar insets padding to the activity's root view.
 * Subclasses should call setContentView(binding.root) as usual — this class will apply edge-to-edge
 * and window insets automatically.
 */
open class BaseActivity : AppCompatActivity() {
    override fun setContentView(view: View?) {
        // Prepare edge-to-edge behavior before attaching the content view.
        enableEdgeToEdge()
        super.setContentView(view)

        // Apply window insets to the root view so content avoids system bars.
        if (view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}

