package com.akshay.statuscatch.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.akshay.statuscatch.R
import com.akshay.statuscatch.adapters.SettingsAdapter
import com.akshay.statuscatch.databinding.FragmentSettingsBinding
import com.akshay.statuscatch.model.SettingsModel



/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSettings.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSettings : Fragment() {
    private val binding by lazy {
        FragmentSettingsBinding.inflate(layoutInflater)
    }
    private val list = ArrayList<SettingsModel>()
    private val adapter by lazy {
        SettingsAdapter(list, requireActivity())
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
        settingsRecyclerView.adapter = adapter

            list.add(
                SettingsModel(
                    image = R.drawable.how_to_use,
                    title = "How to use",
                    desc = "Know how to download statuses"
                )
            )
            list.add(
                SettingsModel(
                    image=R.drawable.save_in_folder,
                    title = "Save in Folder",
                    desc = "/internalstorage/Documents/${getString(R.string.app_name)}"
                )
            )
            list.add(
                SettingsModel(
                    R.drawable.privacy_policy,
                    title = "Privacy Policy",
                    desc = "Read Our Terms & Conditions"
                )
            )
            list.add(
                SettingsModel(
                    R.drawable.share,
                    title = "Share",
                    desc = "Sharing is caring"
                )
            )
            list.add(
                SettingsModel(
                    R.drawable.rate_us,
                    title = "Rate Us",
                    desc = "Please support our work by rating on PlayStore"
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
}