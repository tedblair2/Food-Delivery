package com.example.fud.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.example.fud.FoodDetailActivity
import com.example.fud.R
import com.example.fud.databinding.BannerLayoutBinding
import com.example.fud.model.Banner
import com.example.fud.model.Category
import com.squareup.picasso.Picasso

class BannerAdapter(private val bannerlist:ArrayList<Banner>):PagerAdapter() {
    override fun getCount(): Int {
        return bannerlist.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object` as CardView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=LayoutInflater.from(container.context).inflate(R.layout.banner_layout,container,false)
        val binding=BannerLayoutBinding.bind(view)
        val ctx=container.context
        val item=bannerlist[position]
        binding.menuItem.text=item.name
        Picasso.get().load(item.image).placeholder(R.drawable.not_available).into(binding.menuImg)
        binding.menuImg.setOnClickListener {
            val pairs= arrayOfNulls<android.util.Pair<View,String>>(2)
            pairs[0]=android.util.Pair(binding.menuImg,"foodimage")
            pairs[1]=android.util.Pair(binding.menuItem,"foodname")
            val options=ActivityOptions.makeSceneTransitionAnimation(ctx as Activity,*pairs)
            ctx.startActivity(Intent(ctx,FoodDetailActivity::class.java).apply {
                putExtra("id",item.foodId)
            },options.toBundle())
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CardView)
    }
}