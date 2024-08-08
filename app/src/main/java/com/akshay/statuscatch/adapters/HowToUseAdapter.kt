package com.akshay.statuscatch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akshay.statuscatch.databinding.ItemHowToUseBinding
import com.akshay.statuscatch.model.HowToUse

class HowToUseAdapter(private var list: ArrayList<HowToUse>, var context: Context) :
RecyclerView.Adapter<HowToUseAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: ItemHowToUseBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(model: HowToUse){
                binding.apply {
                    tvMain.text = model.heading
                    tvSub.text = model.desc
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHowToUseBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount()= list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(model = list[position])
    }
}