package com.example.greenswuniex

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class ChallengeListAdapter(
    private val context: Context,
    private val items: MutableList<ChallengeListItem>,
    private val isHomeFragment: Boolean
) : RecyclerView.Adapter<ChallengeListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkButton: ImageButton = view.findViewById(R.id.item_check_btn)
        val categoryTextView: TextView = view.findViewById(R.id.item_category_tv)
        val nameTextView: TextView = view.findViewById(R.id.item_list_tv)
        val container: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.challenge_list_item, parent, false)
        if (isHomeFragment) {
            view.background = context.getDrawable(R.drawable.border_layout)
//            view.setBackgroundColor(context.resources.getColor(android.R.color.white,null))
        } else {
            view.background = context.getDrawable(R.drawable.border_layout_yellow)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.checkButton.setImageResource(if (item.challenge_onCheck) R.drawable.challenge_check else R.drawable.challenge_no_check)
        holder.categoryTextView.text = item.challenge_category
        holder.nameTextView.text = item.challenge_name

        // 체크했을 경우 다시 체크 불가능
        holder.checkButton.isEnabled = !item.challenge_onCheck

        holder.checkButton.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.select_dialog, null)
            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.dialog_button_yes).setOnClickListener {
                item.challenge_onCheck = !item.challenge_onCheck
                notifyItemChanged(position)
                saveItemListToSharedPreferences()
                dialogBuilder.dismiss()
            }

            dialogView.findViewById<Button>(R.id.dialog_button_no).setOnClickListener {
                dialogBuilder.dismiss()
            }

            dialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun saveItemListToSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("challenge_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(items)
        editor.putString("item_list", json)
        editor.apply()
    }
}
