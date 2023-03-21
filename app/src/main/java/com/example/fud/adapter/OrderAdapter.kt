package com.example.fud.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fud.databinding.OrderLayoutBinding
import com.example.fud.model.Order
import com.example.fud.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class OrderAdapter(private val orderlist:ArrayList<Order>):RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(val binding:OrderLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=OrderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=orderlist[position]
        getId(item.orderId!!,holder)
        holder.binding.address.text=item.address?.display_name
        holder.binding.ship.text=getStatus(item.status!!)
        getPhone(item.userid!!,holder)
        getTime(item.time!!,holder)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(time: Long, holder: OrderAdapter.ViewHolder) {
        val date=Date(time)
        val format=SimpleDateFormat("dd.MM.yyyy HH:mm")
        val ordertime=format.format(date)
        holder.binding.time.text=ordertime
    }
    private fun getId(id:String,holder: ViewHolder){
        val regex=Regex("[^a-zA-Z0-9]")
        val result=regex.replace(id,"")
        holder.binding.orderId.text="#$result"
    }

    private fun getPhone(userid: String, holder: ViewHolder) {
        Firebase.database.getReference("Users").child(userid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(User::class.java)!!
                holder.binding.phone.text=user.number
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getStatus(number:String):String{
        return when (number) {
            "0" -> {
                "PLACED"
            }
            "1" -> {
                "ON THE WAY"
            }
            else -> {
                "SHIPPED"
            }
        }
    }
}