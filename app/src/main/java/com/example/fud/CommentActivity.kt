package com.example.fud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fud.adapter.CommentAdapter
import com.example.fud.databinding.ActivityCommentBinding
import com.example.fud.model.Rating
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private val ratings= arrayListOf<Rating>()
    private lateinit var commentAdapter: CommentAdapter
    private var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Comments"
        id=intent.getStringExtra("id")!!
        commentAdapter= CommentAdapter(ratings)
        binding.root.setColorSchemeResources(R.color.purple_700,R.color.teal_200,android.R.color.holo_blue_dark,
            android.R.color.holo_green_dark)
        binding.recyclerComments.setHasFixedSize(true)
        val layoutManager=LinearLayoutManager(this)
        layoutManager.reverseLayout=true
        layoutManager.stackFromEnd=true
        binding.recyclerComments.layoutManager=layoutManager
        binding.recyclerComments.adapter=commentAdapter

        binding.root.setOnRefreshListener {
            getRatings()
        }

        binding.root.post {
            getRatings()
        }
    }

    private fun getRatings() {
        Firebase.database.getReference("Ratings").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ratings.clear()
                for (child in snapshot.children){
                    val item=child.getValue(Rating::class.java)!!
                    if (item.foodId.equals(id)){
                        ratings.add(item)
                    }
                }
                commentAdapter.notifyDataSetChanged()
                binding.root.isRefreshing=false
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}