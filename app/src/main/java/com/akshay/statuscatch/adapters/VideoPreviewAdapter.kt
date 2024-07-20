package com.akshay.statuscatch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.akshay.statuscatch.R
import com.akshay.statuscatch.databinding.ItemVideoPreviewBinding
import com.akshay.statuscatch.model.MediaModel
import com.akshay.statuscatch.utils.saveStatus


class VideoPreviewAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {

                val player = ExoPlayer.Builder(context).build()
                playerView.player = player
                val mediaItem = MediaItem.fromUri(mediaModel.pathUri)

                player.setMediaItem(mediaItem)

                player.prepare()

                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.download_new
                }
                tools.statusDownload.setImageResource(downloadImage)



                tools.download.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        // status is downloaded
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        // unable to download status
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

        fun stopPlayer(){
            binding.playerView.player?.stop()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemVideoPreviewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size

}











