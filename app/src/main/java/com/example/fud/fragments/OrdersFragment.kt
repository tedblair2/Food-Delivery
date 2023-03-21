package com.example.fud.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fud.adapter.OrderAdapter
import com.example.fud.databinding.FragmentOrdersBinding
import com.example.fud.model.Order
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OrdersFragment : Fragment() {
    private var _binding:FragmentOrdersBinding?=null
    private val binding get() = _binding!!
    private var orderlist= arrayListOf<Order>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentOrdersBinding.inflate(inflater,container,false)
        orderAdapter= OrderAdapter(orderlist)
        binding.recyclerOrders.setHasFixedSize(true)
        val layoutManager=LinearLayoutManager(requireContext())
        layoutManager.reverseLayout=true
        layoutManager.stackFromEnd=true
        binding.recyclerOrders.layoutManager=layoutManager
        binding.recyclerOrders.adapter=orderAdapter
        getOrders()
        return binding.root
    }

    private fun getOrders() {
        Firebase.database.getReference("Orders").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                orderlist.clear()
                val userid=Firebase.auth.currentUser!!.uid
                for (child in snapshot.children){
                    val item=child.getValue(Order::class.java)!!
                    if (item.userid==userid){
                        orderlist.add(item)
                    }
                }
                orderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}