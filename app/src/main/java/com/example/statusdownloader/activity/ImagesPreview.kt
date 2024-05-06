package com.example.statusdownloader.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devatrii.statussaver.views.adapters.ImagePreviewAdapter
import com.example.statusdownloader.databinding.ActivityImagesPreviewBinding
import com.example.statusdownloader.model.MediaModel
import com.example.statusdownloader.utils.Constants

class ImagesPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = ImagePreviewAdapter(list, activity)
            imagesViewPager.adapter = adapter
            imagesViewPager.currentItem = scrollTo
        }
    }
}