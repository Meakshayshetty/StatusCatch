package com.akshay.statuscatch.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.akshay.statuscatch.viewmodels.StatusViewModel
import com.akshay.statuscatch.viewmodels.factories.StatusViewModelFactory
import com.akshay.statuscatch.adapters.MediaViewPagerAdapter
import com.akshay.statuscatch.databinding.FragmentStatusBinding
import com.akshay.statuscatch.repository.StatusRepository
import com.akshay.statuscatch.utils.Constants
import com.akshay.statuscatch.utils.SharedPrefKeys
import com.akshay.statuscatch.utils.SharedPrefUtils
import com.akshay.statuscatch.utils.buildFolderPickerIntent
import com.google.android.material.tabs.TabLayoutMediator
import com.akshay.statuscatch.model.MediaModel

class FragmentStatus : Fragment() {
    private val binding by lazy {
        FragmentStatusBinding.inflate(layoutInflater)
    }
    private lateinit var type: String
    private lateinit var wpPickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var wpBusinessPickerLauncher: ActivityResultLauncher<Intent>

    private val viewPagerTitles = arrayListOf("Images", "Videos")
    private lateinit var viewModel: StatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // register activity result launchers once
        wpPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val treeUri = result.data?.data
                if (treeUri != null) {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    SharedPrefUtils.putPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, treeUri.toString())
                    SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, true)
                    getWhatsAppStatuses()
                }
            }
        }

        wpBusinessPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val treeUri = result.data?.data
                if (treeUri != null) {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    SharedPrefUtils.putPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, treeUri.toString())
                    SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, true)
                    getWhatsAppBusinessStatuses()
                }
            }
        }

        binding.apply {
            arguments?.let {
                val repo = StatusRepository(requireActivity())
                viewModel = ViewModelProvider(
                    requireActivity(),
                    StatusViewModelFactory(repo)
                )[StatusViewModel::class.java]

                type = it.getString(Constants.FRAGMENT_TYPE_KEY, "")

                when (type) {
                    Constants.TYPE_WHATSAPP_MAIN -> {
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                            false
                        )
                        if (isPermissionGranted) {
                            getWhatsAppStatuses()
                            binding.swipeRefreshLayout.setOnRefreshListener {
                                refreshStatuses()
                            }
                        }

                        permissionLayout.btnPermission.setOnClickListener {
                            val intent = buildFolderPickerIntent(Constants.getWhatsappUri())
                            wpPickerLauncher.launch(intent)
                        }

                        // set adapter first
                        val viewPagerAdapter = MediaViewPagerAdapter(requireActivity())
                        statusViewPager.adapter = viewPagerAdapter

                        // setup tabs & observers
                        setupTabs(viewModel.whatsAppImagesLiveData, viewModel.whatsAppVideosLiveData)

                    }

                    Constants.TYPE_WHATSAPP_BUSINESS -> {
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                            false
                        )
                        if (isPermissionGranted) {
                            getWhatsAppBusinessStatuses()

                            binding.swipeRefreshLayout.setOnRefreshListener {
                                refreshStatuses()
                            }

                        }

                        permissionLayout.btnPermission.setOnClickListener {
                            val intent = buildFolderPickerIntent(Constants.getWhatsappBusinessUri())
                            wpBusinessPickerLauncher.launch(intent)
                        }

                        val viewPagerAdapter = MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
                        )
                        statusViewPager.adapter = viewPagerAdapter

                        // setup tabs & observers for business
                        setupTabs(viewModel.whatsAppBusinessImagesLiveData, viewModel.whatsAppBusinessVideosLiveData)
                    }
                }

            }
        }
    }

    // helper to setup TabLayout mediator, custom views and observers
    private fun setupTabs(
        imageLiveData: LiveData<ArrayList<MediaModel>>,
        videoLiveData: LiveData<ArrayList<MediaModel>>
    ) {
        val tabLayout = binding.tabLayout
        val viewPager = binding.statusViewPager

        val mediator = TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = viewPagerTitles[pos]
        }
        mediator.attach()

        // set custom views for tabs
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val custom = layoutInflater.inflate(com.akshay.statuscatch.R.layout.tab_custom, tabLayout, false)
            val titleView = custom.findViewById<TextView>(com.akshay.statuscatch.R.id.tab_title)
            val countView = custom.findViewById<TextView>(com.akshay.statuscatch.R.id.tab_count)
            titleView.text = viewPagerTitles[i]
            countView.text = "0"
            tab?.customView = custom
        }

        imageLiveData.observe(requireActivity()) { imagesList ->
            val count = imagesList?.size ?: 0
            val tab = tabLayout.getTabAt(0)
            val countView = tab?.customView?.findViewById<TextView>(com.akshay.statuscatch.R.id.tab_count)
            countView?.text = count.toString()
        }

        videoLiveData.observe(requireActivity()) { videosList ->
            val count = videosList?.size ?: 0
            val tab = tabLayout.getTabAt(1)
            val countView = tab?.customView?.findViewById<TextView>(com.akshay.statuscatch.R.id.tab_count)
            countView?.text = count.toString()
        }
    }

    fun bottomNavListener(item: Int) {
        when (item) {
            1 -> {
                Log.e("bottomSelected", "1")
            }
            2 -> {
                Log.e("bottomSelected", "2")

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    private fun refreshStatuses() {
        when (type) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                Toast.makeText(requireActivity(), "Refreshing WP Statuses", Toast.LENGTH_SHORT)
                    .show()
                getWhatsAppStatuses()
            }

            else -> {
                Toast.makeText(
                    requireActivity(),
                    "Refreshing WP Business Statuses",
                    Toast.LENGTH_SHORT
                ).show()
                getWhatsAppBusinessStatuses()
            }
        }

        Handler(Looper.myLooper()!!).postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }

    private fun getWhatsAppStatuses() {
        // function to get wp statuses
        binding.permissionLayoutHolder.visibility = View.GONE
        viewModel.getWhatsAppStatuses()
    }

    private val TAG = "FragmentStatus"
    private fun getWhatsAppBusinessStatuses() {
        // function to get wp statuses
        binding.permissionLayoutHolder.visibility = View.GONE
        Log.d(TAG, "getWhatsAppBusinessStatuses: Getting Wp Business Statuses")
        viewModel.getWhatsAppBusinessStatuses()
    }
}
