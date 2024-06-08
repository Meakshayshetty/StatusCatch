package com.example.statusdownloader.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.statusdownloader.viewmodels.StatusViewModel
import com.devatrii.statussaver.viewmodels.factories.StatusViewModelFactory
import com.example.statusdownloader.adapters.MediaViewPagerAdapter
import com.example.statusdownloader.repository.StatusRepository
import com.example.statusdownloader.databinding.FragmentStatusBinding
import com.example.statusdownloader.utils.Constants
import com.example.statusdownloader.utils.SharedPrefKeys
import com.example.statusdownloader.utils.SharedPrefUtils
import com.example.statusdownloader.utils.getFolderPermissions
import com.google.android.material.tabs.TabLayoutMediator

class FragmentStatus : Fragment() {
    private val binding by lazy {
        FragmentStatusBinding.inflate(layoutInflater)
    }
    private lateinit var type: String
    private val WHATSAPP_REQUEST_CODE = 101
    private val WHATSAPP_BUSINESS_REQUEST_CODE = 102

    private val viewPagerTitles = arrayListOf("Images", "Videos")
    private lateinit var viewModel: StatusViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        // check permission
                        // granted then fetch statuses
                        // get permission
                        // fetch statuses
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
                            getFolderPermissions(
                                context = requireActivity(),
                                REQUEST_CODE = WHATSAPP_REQUEST_CODE,
                                initialUri = Constants.getWhatsappUri()
                            )
                        }
                        val viewPagerAdapter = MediaViewPagerAdapter(requireActivity())
                        statusViewPager.adapter = viewPagerAdapter
                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
                            tab.text = viewPagerTitles[pos]
                        }.attach()

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
                            getFolderPermissions(
                                context = requireActivity(),
                                REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
                                initialUri = Constants.getWhatsappBusinessUri()
                            )
                        }
                        val viewPagerAdapter = MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
                        )
                        statusViewPager.adapter = viewPagerAdapter
                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
                            tab.text = viewPagerTitles[pos]
                        }.attach()
                    }
                }

            }
        }
    }
    fun bottomNavListener(item:Int){
        when(item){
            1 ->{
                Log.e("bottomSelected","1")
            }
            2->{
                Log.e("bottomSelected","2")

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            val treeUri = data?.data!!
            requireActivity().contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            if (requestCode == WHATSAPP_REQUEST_CODE) {
                // whatsapp logic here
                SharedPrefUtils.putPrefString(
                    SharedPrefKeys.PREF_KEY_WP_TREE_URI,
                    treeUri.toString()
                )
                SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, true)
                getWhatsAppStatuses()
            } else if (requestCode == WHATSAPP_BUSINESS_REQUEST_CODE) {
                // whatsapp business logic here
                SharedPrefUtils.putPrefString(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI,
                    treeUri.toString()
                )
                SharedPrefUtils.putPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    true
                )
                getWhatsAppBusinessStatuses()
            }
        }


    }
}















