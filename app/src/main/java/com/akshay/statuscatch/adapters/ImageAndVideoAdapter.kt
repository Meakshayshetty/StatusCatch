package com.akshay.statuscatch.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.akshay.statuscatch.R
import com.akshay.statuscatch.activity.ImagePreviewActivity
import com.akshay.statuscatch.activity.VideoPreviewActivity
import com.akshay.statuscatch.databinding.ItemMediaBinding
import com.akshay.statuscatch.model.MEDIA_TYPE_IMAGE
import com.akshay.statuscatch.model.MediaModel
import com.akshay.statuscatch.utils.Constants
import com.akshay.statuscatch.utils.saveStatus
import com.bumptech.glide.Glide

class ImageAndVideoAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<ImageAndVideoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(statusImage)
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    statusPlay.visibility = View.GONE
                }
                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.download_new
                }
                statusDownload.setImageResource(downloadImage)

                cardStatus.setOnClickListener {
                    if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                        // goto image preview activity
                        Intent().apply {
                            putExtra(Constants.MEDIA_LIST_KEY,list)
                            putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                            setClass(context, ImagePreviewActivity::class.java)
                            context.startActivity(this)
                        }
                    } else {
                        // goto video preview activity
                        Intent().apply {
                            putExtra(Constants.MEDIA_LIST_KEY,list)
                            putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                            setClass(context, VideoPreviewActivity::class.java)
                            context.startActivity(this)
                        }
                    }
                }

                statusDownload.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        // status is downloaded
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        // unable to download status
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMediaBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }
}