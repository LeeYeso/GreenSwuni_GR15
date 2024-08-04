package com.example.greenswuniex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChallengeListAdapter(private val context: Context, private val items: List<ChallengeListItem>) : RecyclerView.Adapter<ChallengeListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkButton: ImageButton = view.findViewById(R.id.item_check_btn)
        val categoryTextView: TextView = view.findViewById(R.id.item_category_tv)
        val nameTextView: TextView = view.findViewById(R.id.item_list_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.challenge_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.checkButton.setImageResource(if (item.challenge_onCheck) R.drawable.challenge_check else R.drawable.challenge_no_check)
        holder.categoryTextView.text = item.challenge_category
        holder.nameTextView.text = item.challenge_name

        holder.checkButton.setOnClickListener {
            item.challenge_onCheck = !item.challenge_onCheck
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}