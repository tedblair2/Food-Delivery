package com.example.fud

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fud.adapter.FoodAdapter
import com.example.fud.databinding.ActivityFoodBinding
import com.example.fud.model.Category
import com.example.fud.model.Favorite
import com.example.fud.model.Food
import com.example.fud.viewmodel.FudViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodBinding
    private lateinit var foodAdapter: FoodAdapter
    private var foodList= arrayListOf<Food>()
    private lateinit var fudViewModel: FudViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.enterTransition=Explode()
        window.exitTransition=Explode()
        val id=intent.getStringExtra("id")!!
        getName(id)
        fudViewModel=ViewModelProvider(this)[FudViewModel::class.java]
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        foodAdapter= FoodAdapter(foodList,this)
        binding.recyclerFood.setHasFixedSize(true)
        binding.recyclerFood.layoutManager=GridLayoutManager(this,2)
        binding.recyclerFood.adapter=foodAdapter
        getFood(id)
        addMenuProvider(object:MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu,menu)
                val search=menu.findItem(R.id.search)
                val searchview=search.actionView as androidx.appcompat.widget.SearchView
                searchview.queryHint="Search..."
                val searchText = searchview.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                searchText.setTextColor(Color.WHITE)
                val closeButton = searchview.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                closeButton.setColorFilter(Color.WHITE)
                searchview.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        searchFood(newText?.lowercase())
                        return true
                    }

                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    android.R.id.home-> {
                        finish()
                        true
                    }else->false
                }
            }

        })
    }
    private fun getName(id:String){
        Firebase.database.getReference("Category").child(id).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val item=snapshot.getValue(Category::class.java)
                title=item?.name
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun searchFood(search: String?) {
        val list= arrayListOf<Food>()
        if (foodList.size>0){
            for (food in foodList){
                if (food.name?.lowercase()!!.contains(search!!)){
                    list.add(food)
                }
            }
        }
        foodAdapter.search(list)
    }

    private fun getFood(id: String) {
        Firebase.database.getReference("Foods").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                for (child in snapshot.children){
                    val item=child.getValue(Food::class.java)!!
                    if (item.menuId==id){
                        foodList.add(item)
                    }
                }
                foodAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FoodActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}