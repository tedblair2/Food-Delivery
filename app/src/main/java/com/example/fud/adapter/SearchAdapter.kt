package com.example.fud.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fud.FoodDetailActivity
import com.example.fud.databinding.SearchLayoutBinding
import com.example.fud.model.Food

class SearchAdapter(private var search:ArrayList<Food>,private val context: Context):RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(val binding:SearchLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=SearchLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return search.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=search[position]
        holder.binding.searchtxt.text=item.name
        holder.binding.icon.setColorFilter(Color.DKGRAY)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,FoodDetailActivity::class.java).apply {
                putExtra("id",item.id)
                putExtra("activity","last")
            })
        }
    }
    fun searchFood(filteredlist:ArrayList<Food>){
        search=filteredlist
        notifyDataSetChanged()
    }
}