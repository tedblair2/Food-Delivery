package com.example.fud.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fud.databinding.CommentLayoutBinding
import com.example.fud.model.Rating
import com.example.fud.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CommentAdapter(private val ratings:ArrayList<Rating>):RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(val binding:CommentLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=CommentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ratings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating=ratings[position]
        holder.binding.rating.rating=rating.rating!!
        holder.binding.commnet.text=rating.comment
        getUsername(rating.userid!!,holder)
    }

    private fun getUsername(userid: String, holder: CommentAdapter.ViewHolder) {
        Firebase.database.getReference("Users").child(userid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(User::class.java)!!
                holder.binding.username.text=user.name
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}