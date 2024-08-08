package com.akshay.statuscatch.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akshay.statuscatch.R
import com.akshay.statuscatch.adapters.HowToUseAdapter
import com.akshay.statuscatch.databinding.ActivityHowToUseBinding
import com.akshay.statuscatch.databinding.ActivityMainBinding
import com.akshay.statuscatch.model.HowToUse

class HowToUseActivity : AppCompatActivity() {
    private val list2 = ArrayList<HowToUse>()
    private val binding by lazy {
        ActivityHowToUseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val adapter by lazy {
            HowToUseAdapter(list2, this)
        }
        addHowToUseList()
        binding.rvHowToUse.adapter =adapter

        binding.okayBtn.setOnClickListener {
            finish()
        }
    }
    fun addHowToUseList(){
        list2.add(
            HowToUse(
                "1. Open WhatsApp and View Status\n",
                "Open WhatsApp on your smartphone.\n" +
                        "Scroll through your contacts' statuses and view the status you want to download.\n" +
                        "Note: Ensure you view the status in WhatsApp first for it to appear in StatusCatch."
            )
        )
        list2.add(
            HowToUse(
                "2. Launch StatusCatch\n",
                "Open the StatusCatch app on your smartphone.\n"
            )
        )
        list2.add(
            HowToUse(
                "3. Grant Storage Permissions\n",
                "Make sure StatusCatch has permission to access your device's storage.\n" +
                        "If not granted already, enable storage access in your device's settings."
            )
        )
        list2.add(
            HowToUse(
                "4. Status Appears in StatusCatch\n",
                "The status you viewed on WhatsApp will automatically appear within the StatusCatch app.\n"
            )
        )
        list2.add(
            HowToUse(
                "5. Download Status\n",
                "Locate the status you wish to download within StatusCatch.\n" +
                        "Tap on the download icon or similar option next to the status."
            )
        )
        list2.add(
            HowToUse(
                "6. Save to Local Storage\n",
                "StatusCatch will save the downloaded status directly to your device's local storage.\n" +
                        "Access the saved status in the StatusCatch app or your device's gallery."
            )
        )
        list2.add(
            HowToUse(
                "Additional Tips\n",
                "Offline Viewing: Downloaded statuses can be viewed offline anytime, even after they expire on WhatsApp.\n" +
                        "Updates: StatusCatch may update to support new features or WhatsApp status format changes."
            )
        )
    }

}