package com.akshay.statuscatch.adapters

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akshay.statuscatch.R
import com.akshay.statuscatch.databinding.DialogGuideBinding
import com.akshay.statuscatch.databinding.ItemSettingsBinding
import com.akshay.statuscatch.model.SettingsModel


class SettingsAdapter(private var list: ArrayList<SettingsModel>, var context: Context) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ItemSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SettingsModel, position: Int) {
            binding.apply {
                img.setImageResource(model.image)
                settingsTitle.text = model.title
                settingsDesc.text = model.desc

                root.setOnClickListener {
                    when (position) {
                        0 -> {
                            // how to use 1st item
                            val dialog = Dialog(context)
                            val dialogBinding =
                                DialogGuideBinding.inflate((context as Activity).layoutInflater)
                            dialogBinding.okayBtn.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.setContentView(dialogBinding.root)

                            dialog.window?.setLayout(
                                ActionBar.LayoutParams.MATCH_PARENT,
                                ActionBar.LayoutParams.WRAP_CONTENT
                            )

                            dialog.show()
                        }

                        2 -> {
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://atrii.dev")).apply {
                                context.startActivity(this)
                            }

                        }

                        3 -> {
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.app_name))
                            putExtra(Intent.EXTRA_TEXT,"My App is soo cool please download it :https://play.google.com/store/apps/details?id=${context.packageName}")
                            context.startActivity(this)
                        }
                        }

                        4 -> {
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
                            ).apply {
                                context.startActivity(this)
                            }

                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSettingsBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(model = list[position], position)
    }
}