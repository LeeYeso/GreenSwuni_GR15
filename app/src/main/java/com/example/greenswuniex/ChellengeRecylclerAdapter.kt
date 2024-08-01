package com.example.greenswuniex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChellengeRecylclerAdapter (private val dataList: List<String>)
    : RecyclerView.Adapter<ChellengeRecylclerAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // 아이템 레이아웃을 인플레이트하여 뷰홀더 생성
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chellenge_recycler_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // 뷰홀더에 데이터 바인딩
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size // 데이터 목록의 크기 반환
    }

    // 뷰홀더 클래스
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView) // 아이템 레이아웃의 텍스트뷰 연결

        fun bind(data: String) {
            textView.text = data // 데이터 설정
        }
    }
}