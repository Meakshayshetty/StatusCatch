package com.akshay.statuscatch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akshay.statuscatch.databinding.ItemHowToUseBinding
import com.akshay.statuscatch.model.HowToUse

class HowToUseAdapter(private var list: ArrayList<HowToUse>, var context: Context) :
    RecyclerView.Adapter<HowToUseAdapter.ViewHolder>() {

    // position of currently expanded item, -1 when none
    private var expandedPosition = -1

    inner class ViewHolder(var binding: ItemHowToUseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: HowToUse, position: Int) {
            binding.apply {
                // set chevron drawable programmatically (ensures resource is available at runtime)
                try { ivDropdown.setImageResource(com.akshay.statuscatch.R.drawable.ic_dropdown) } catch (_: Exception) {}
                tvMain.text = model.heading
                tvSub.text = model.desc

                // Set initial states
                val isExpanded = position == expandedPosition
                tvSub.visibility = if (isExpanded) View.VISIBLE else View.GONE
                // dropdown: 0 = pointing down; 180 = pointing up
                ivDropdown.rotation = if (isExpanded) 180f else 0f

                // Ensure tvSub alpha/translation match visibility state to allow animation
                tvSub.alpha = if (isExpanded) 1f else 0f
                tvSub.translationY = if (isExpanded) 0f else -20f

                // Toggle expand/collapse when the header card is clicked
                cardHeader.setOnClickListener {
                    val prevExpanded = expandedPosition
                    val willExpand = !isExpanded
                    expandedPosition = if (willExpand) position else -1

                    // Refresh previously expanded item
                    if (prevExpanded >= 0) {
                        notifyItemChanged(prevExpanded)
                    }
                    // Refresh this item to show new state
                    notifyItemChanged(position)
                }
            }
        }

        // Override onViewAttachedToWindow to perform smooth animation when view becomes visible or updated
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHowToUseBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(model = list[position], position)
        // perform animation based on expanded state when binding completes
        val isExpanded = position == expandedPosition
        holder.binding.apply {
            if (isExpanded) {
                // animate expand
                tvSub.visibility = View.VISIBLE
                tvSub.animate().cancel()
                tvSub.animate().alpha(1f).translationY(0f).setDuration(200).start()
                ivDropdown.animate().rotation(180f).setDuration(200).start()
            } else {
                // animate collapse
                tvSub.animate().cancel()
                tvSub.animate().alpha(0f).translationY(-20f).setDuration(200).withEndAction {
                    tvSub.visibility = View.GONE
                }.start()
                ivDropdown.animate().rotation(0f).setDuration(200).start()
            }
        }
    }
}