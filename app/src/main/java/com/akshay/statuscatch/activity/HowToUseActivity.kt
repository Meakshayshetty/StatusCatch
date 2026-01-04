package com.akshay.statuscatch.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.statuscatch.adapters.HowToUseAdapter
import com.akshay.statuscatch.databinding.ActivityHowToUseBinding
import com.akshay.statuscatch.model.HowToUse

class HowToUseActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityHowToUseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // apply window insets to root container to avoid overlap with system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // prepare data and adapter
        val items = buildHowToUseList()
        val adapter = HowToUseAdapter(ArrayList(items), this)

        // setup RecyclerView with stable layout manager and dividers
        binding.rvHowToUse.layoutManager = LinearLayoutManager(this)
        binding.rvHowToUse.setHasFixedSize(true)
        binding.rvHowToUse.adapter = adapter
        binding.rvHowToUse.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        binding.okayBtn.setOnClickListener {
            finish()
        }
    }

    private fun buildHowToUseList(): List<HowToUse> {
        return listOf(
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_step1_title),
                getString(com.akshay.statuscatch.R.string.htu_step1_body)
            ),
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_step2_title),
                getString(com.akshay.statuscatch.R.string.htu_step2_body)
            ),
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_step3_title),
                getString(com.akshay.statuscatch.R.string.htu_step3_body)
            ),
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_step4_title),
                getString(com.akshay.statuscatch.R.string.htu_step4_body)
            ),
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_step5_title),
                getString(com.akshay.statuscatch.R.string.htu_step5_body)
            ),
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_step6_title),
                getString(com.akshay.statuscatch.R.string.htu_step6_body)
            ),
            HowToUse(
                getString(com.akshay.statuscatch.R.string.htu_tips_title),
                getString(com.akshay.statuscatch.R.string.htu_tips_body)
            )
        )
    }

}