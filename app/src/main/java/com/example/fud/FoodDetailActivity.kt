package com.example.fud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.fud.databinding.ActivityFoodDetailBinding
import com.example.fud.databinding.RatingDialogBinding
import com.example.fud.model.Cart
import com.example.fud.model.Food
import com.example.fud.model.Rating
import com.example.fud.viewmodel.FudViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailBinding
    private lateinit var ratingDialogBinding: RatingDialogBinding
    private lateinit var fudViewModel: FudViewModel
    private var id=""
    private var count=1
    private var price=1
    private var total=1
    private var productname=""
    private var productId=""
    private var discount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        fudViewModel=ViewModelProvider(this)[FudViewModel::class.java]
        id=intent.getStringExtra("id")!!
        getFoodDetails()
        binding.counter.text=count.toString()
        binding.collapsetoolbar.setExpandedTitleTextAppearance(R.style.ExtendendToolbar)
        binding.collapsetoolbar.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar)
        binding.reduce.setOnClickListener {
            if (count>1){
                count--
            }
            total=price*count
            binding.counter.text=count.toString()
            binding.foodprice.text="Ksh.${total}"
        }
        binding.add.setOnClickListener {
            count++
            total=price*count
            binding.counter.text=count.toString()
            binding.foodprice.text="Ksh.${total}"
        }
        binding.basket.setOnClickListener {
            val cart=Cart(0,productId,productname,count,discount,total)
            fudViewModel.addCart(cart)
            Toast.makeText(this, "Added to Cart successfully", Toast.LENGTH_SHORT).show()
        }

        binding.rate.setOnClickListener {
            showRatingDialog()
        }
        getRating()
        binding.comments.setOnClickListener {
            startActivity(Intent(this,CommentActivity::class.java).apply {
                putExtra("id",id)
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val parent=intent.getStringExtra("activity")
        if (parent != null && parent=="last"){
            startActivity(Intent(this,HomeActivity::class.java))
        }
    }

    private fun getRating() {
        val list= arrayListOf<Rating>()
        Firebase.database.getReference("Ratings").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children){
                    val item=child.getValue(Rating::class.java)!!
                    if (item.userid==Firebase.auth.currentUser!!.uid && item.foodId==id){
                        list.add(item)
                    }
                }
                if (list.isNotEmpty()){
                    val rating=list.last().rating
                    binding.ratings.rating=rating!!
                }else{
                    binding.ratings.rating=0f
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun showRatingDialog() {
        var ratingNUmber=1f
        val view=LayoutInflater.from(this).inflate(R.layout.rating_dialog,null)
        ratingDialogBinding= RatingDialogBinding.bind(view)
        AlertDialog.Builder(this)
            .setView(view)
            .setTitle("Rate this Food")
            .setIcon(R.drawable.baseline_star_rate_24)
            .setMessage("Please provide a rating for this food")
            .setPositiveButton("Submit"){dialog,_->
                val comment=ratingDialogBinding.comment.text.toString()
                if (TextUtils.isEmpty(comment)){
                    ratingDialogBinding.comment.error="Please provide a comment"
                }else{
                    val ref=Firebase.database.getReference("Ratings")
                    val ratingId=ref.push().key!!
                    val rating=Rating(ratingNUmber,Firebase.auth.currentUser!!.uid,comment,ratingId,id)
                    Firebase.database.getReference("Ratings").child(ratingId).setValue(rating).addOnCompleteListener { task->
                            if (task.isSuccessful){
                                Toast.makeText(this, "Thank you for submitting your rating.", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()
        ratingDialogBinding.alertRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingNUmber=rating
            val ratingText = when (rating) {
                in 0.0..1.0 -> "Very Bad"
                in 1.1..2.0 -> "Not Good"
                in 2.1..3.0 -> "OK"
                in 3.1..4.0 -> "Good"
                in 4.1..5.0 -> "Excellent"
                else -> ""
            }
            ratingDialogBinding.ratingtxt.text=ratingText
        }
    }

    private fun getFoodDetails() {
        Firebase.database.getReference("Foods").child(id).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val item=snapshot.getValue(Food::class.java)!!
                binding.foodprice.text="Ksh.${item.price}"
                price= item.price?.toInt() ?:0
                total=item.price?.toInt() ?:0
                binding.descrition.text =item.description
                binding.foodname.text=item.name
                productname=item.name!!
                productId=item.id!!
                discount=item.discount!!.toInt()
                binding.collapsetoolbar.title=item.name
                Picasso.get().load(item.image).placeholder(R.drawable.not_available).into(binding.foodImg)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}