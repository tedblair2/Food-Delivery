package com.example.fud.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fud.FoodDetailActivity
import com.example.fud.R
import com.example.fud.databinding.FoodLayoutBinding
import com.example.fud.model.Food
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FoodAdapter(private var foodlist:ArrayList<Food>,
                  private val context: Context):RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(val binding: FoodLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=FoodLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food=foodlist[position]
        holder.binding.menuItem.text=food.name
        Picasso.get().load(food.image).placeholder(R.drawable.not_available).into(holder.binding.menuImg)
        holder.binding.menuImg.setOnClickListener {
            val pairs = arrayOfNulls<android.util.Pair<View, String>>(2)
            pairs[0] = android.util.Pair(holder.binding.menuImg, "foodimage")
            pairs[1] = android.util.Pair(holder.binding.menuItem, "foodname")
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(context as Activity, *pairs)
            context.startActivity(Intent(context,FoodDetailActivity::class.java).apply {
                putExtra("id",food.id)
            },activityOptions.toBundle())
        }
    }
    fun search(filteredList:ArrayList<Food>){
        foodlist=filteredList
        notifyDataSetChanged()
    }
}