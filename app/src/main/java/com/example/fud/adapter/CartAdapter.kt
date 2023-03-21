package com.example.fud.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fud.R
import com.example.fud.databinding.CartLayoutBinding
import com.example.fud.model.Cart
import com.example.fud.model.Food
import com.example.fud.viewmodel.FudViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class CartAdapter(private val cartlist:List<Cart>):RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(val binding:CartLayoutBinding):RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener{

            init {
                binding.root.setOnCreateContextMenuListener(this)
            }

        override fun onCreateContextMenu(
            p0: ContextMenu?,
            p1: View?,
            p2: ContextMenu.ContextMenuInfo?
        ) {
            p0?.setHeaderTitle("Select Action")
            p0?.add(0,0,adapterPosition,"Update")
            p0?.add(0,1,adapterPosition,"Delete")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=CartLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=cartlist[position]
        val price= item.price - item.discount
        holder.binding.foodname.text=item.productName
        holder.binding.foodprice.text="Ksh.$price"
        holder.binding.quantity.text=item.quantity.toString()
        Firebase.database.getReference("Foods").child(item.productId).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val food=snapshot.getValue(Food::class.java)
                Picasso.get().load(food?.image).placeholder(R.drawable.not_available).into(holder.binding.foodimg)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}