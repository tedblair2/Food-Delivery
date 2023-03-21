package com.example.fud.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.fud.R
import com.example.fud.SearchActivity
import com.example.fud.adapter.BannerAdapter
import com.example.fud.adapter.MenuAdapter
import com.example.fud.databinding.FragmentMenuBinding
import com.example.fud.model.Banner
import com.example.fud.model.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class MenuFragment : Fragment() {
    private var _binding:FragmentMenuBinding?=null
    private val binding get() = _binding!!
    private val menulist= arrayListOf<Category>()
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private var bannerJob:Job?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentMenuBinding.inflate(inflater,container,false)
        menuAdapter= MenuAdapter(menulist,requireContext())

        val menuhost:MenuHost=requireActivity()
        menuhost.addMenuProvider(menuProvider,viewLifecycleOwner,Lifecycle.State.RESUMED)

        binding.swiperefresh.setColorSchemeResources(R.color.purple_700,R.color.teal_200,android.R.color.holo_blue_dark,
        android.R.color.holo_green_dark)
        binding.recyclerMenu.setHasFixedSize(true)
        binding.recyclerMenu.layoutManager=GridLayoutManager(requireContext(),2)
        binding.recyclerMenu.adapter=menuAdapter
        binding.swiperefresh.setOnRefreshListener {
            getMenu()
        }
        binding.root.post {
            getMenu()
            getBanner()
        }


        return binding.root
    }
    private val menuProvider=object:MenuProvider{
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.home,menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when(menuItem.itemId){
                R.id.search_food->{
                    startActivity(Intent(requireContext(),SearchActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                        requireActivity().finish()
                    })
                    true
                }
                else->false
            }
        }
    }
    private fun getBanner(){
        Firebase.database.getReference("Banner").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bannerlist= arrayListOf<Banner>()
                for (child in snapshot.children){
                    val item=child.getValue(Banner::class.java)!!
                    bannerlist.add(item)
                }
                bannerAdapter= BannerAdapter(bannerlist)
                binding.banner.adapter=bannerAdapter
                binding.banner.clipToPadding=false
                showDots(bannerAdapter.count)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showDots(count: Int) {
        val dots= arrayOfNulls<ImageView>(count)
        for (i in 0 until count){
            dots[i]= ImageView(requireContext())
            dots[i]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it.applicationContext,R.drawable.non_active_dot) })
            val params= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(2,0,2,0)
            binding.dots.addView(dots[i],params)
        }

        dots[0]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it.applicationContext,R.drawable.active_dot) })
        updateViews(count)
        binding.banner.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until count){
                    dots[i]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it.applicationContext,R.drawable.non_active_dot) })
                }
                dots[position]?.setImageDrawable(context?.let { ContextCompat.getDrawable(it.applicationContext,R.drawable.active_dot) })
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }
    private fun updateViews(count: Int){
        bannerJob=CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            val current=binding.banner.currentItem
            val nextItem=if (current==count.minus(1)) 0 else current+1
            binding.banner.setCurrentItem(nextItem,true)
            updateViews(count)
        }
    }

    private fun getMenu() {
        Firebase.database.getReference("Category").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                menulist.clear()
                for (child in snapshot.children){
                    val item=child.getValue(Category::class.java)!!
                    menulist.add(item)
                }
                menuAdapter.notifyDataSetChanged()
                try {
                    binding.root.isRefreshing=false
                }catch (e:NullPointerException){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerJob?.cancel()
        bannerJob=null
        _binding=null
    }
}