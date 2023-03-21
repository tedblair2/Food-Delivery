package com.example.fud.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fud.FoodActivity
import com.example.fud.R
import com.example.fud.databinding.MenuItemsBinding
import com.example.fud.model.Category
import com.squareup.picasso.Picasso

class MenuAdapter(private var menuList:ArrayList<Category>, private val context: Context):RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(val binding: MenuItemsBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=MenuItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=menuList[position]
        holder.binding.menuItem.text=item.name
        Picasso.get().load(item.image).placeholder(R.drawable.not_available).into(holder.binding.menuImg)

        holder.itemView.setOnClickListener {
            ViewCompat.setTransitionName(holder.binding.menuItem,"text_${item.name}")
            val options=ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,
                holder.binding.menuItem,"text_${item.name}")
            context.startActivity(Intent(context,FoodActivity::class.java).apply {
                putExtra("id",item.id)
                putExtra("name",item.name)
            },options.toBundle())
        }
    }
    fun search(filteredlist:ArrayList<Category>){
        menuList=filteredlist
        notifyDataSetChanged()
    }
}