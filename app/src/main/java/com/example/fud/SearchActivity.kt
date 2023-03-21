package com.example.fud

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fud.adapter.SearchAdapter
import com.example.fud.databinding.ActivitySearchBinding
import com.example.fud.model.Food
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private var searchlist= arrayListOf<Food>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.searchItem.requestFocus()
        val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchItem,InputMethodManager.SHOW_IMPLICIT)
        searchAdapter= SearchAdapter(searchlist,this)
        binding.recyclerSearch.setHasFixedSize(true)
        binding.recyclerSearch.layoutManager=LinearLayoutManager(this)
        binding.recyclerSearch.adapter=searchAdapter
        binding.recyclerSearch.visibility=View.GONE
        getItems()

        binding.searchItem.setOnQueryTextListener(object :OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()){
                    binding.recyclerSearch.visibility= View.GONE
                }else{
                    binding.recyclerSearch.visibility=View.VISIBLE
                    searchFood(newText.lowercase())
                }
                return true
            }

        })
    }

    private fun searchFood(search: String) {
        val list= arrayListOf<Food>()
        if (searchlist.size>0){
            for (item in searchlist){
                if (item.name?.lowercase()!!.contains(search)){
                    list.add(item)
                }
            }
        }
        searchAdapter.searchFood(list)
    }

    private fun getItems() {
        Firebase.database.getReference("Foods").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                searchlist.clear()
                for (child in snapshot.children){
                    val item=child.getValue(Food::class.java)!!
                    searchlist.add(item)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}