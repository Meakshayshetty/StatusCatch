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
                "1. Open WhatsApp and View Status",
                "Open WhatsApp on your smartphone.\nScroll through your contacts' statuses and view the status you want to download.\nNote: Ensure you view the status in WhatsApp first for it to appear in StatusCatch."
            ),
            HowToUse(
                "2. Launch StatusCatch",
                "Open the StatusCatch app on your smartphone."
            ),
            HowToUse(
                "3. Grant Storage Permissions",
                "Make sure StatusCatch has permission to access your device's storage. If not granted already, enable storage access in your device's settings."
            ),
            HowToUse(
                "4. Status Appears in StatusCatch",
                "The status you viewed on WhatsApp will automatically appear within the StatusCatch app."
            ),
            HowToUse(
                "5. Download Status",
                "Locate the status you wish to download within StatusCatch and tap the download icon next to it."
            ),
            HowToUse(
                "6. Save to Local Storage",
                "StatusCatch will save the downloaded status to your device's local storage. Access the saved status in StatusCatch or your gallery."
            ),
            HowToUse(
                "Additional Tips",
                "Offline Viewing: Downloaded statuses can be viewed offline anytime, even after they expire on WhatsApp.\nUpdates: StatusCatch may update to support new features or WhatsApp status format changes."
            )
        )
    }

}