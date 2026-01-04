package com.akshay.statuscatch.activity

import android.os.Bundle
import com.akshay.statuscatch.adapters.ImagePreviewAdapter
import com.akshay.statuscatch.databinding.ActivityImagesPreviewBinding
import com.akshay.statuscatch.model.MediaModel
import com.akshay.statuscatch.utils.Constants

class ImagePreviewActivity : BaseActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val list: ArrayList<MediaModel> = intent.getParcelableArrayListExtra(Constants.MEDIA_LIST_KEY) ?: arrayListOf()
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = ImagePreviewAdapter(list, activity)
            imagesViewPager.adapter = adapter
            imagesViewPager.currentItem = scrollTo
        }
    }
}