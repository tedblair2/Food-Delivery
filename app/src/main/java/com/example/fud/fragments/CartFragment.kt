package com.example.fud.fragments

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fud.R
import com.example.fud.adapter.CartAdapter
import com.example.fud.databinding.AlertCustomBinding
import com.example.fud.databinding.FragmentCartBinding
import com.example.fud.databinding.UpdateCartBinding
import com.example.fud.model.*
import com.example.fud.network.ApiService
import com.example.fud.network.SearchService
import com.example.fud.viewmodel.FudViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val REQUEST_CODE=1084
class CartFragment : Fragment() {
    private var _binding:FragmentCartBinding?=null
    private val binding get() = _binding!!
    private lateinit var fudViewModel: FudViewModel
    private var cartlist= arrayListOf<Cart>()
    private lateinit var cartAdapter: CartAdapter
    private var total=1
    private var token=""
    private var count=1
    private var price=1
    private var sumTotal=1
    private var current_lat=""
    private var current_lon=""
    private var current_name=""
    private var searched_lat=""
    private var searched_lon=""
    private var searched_name=""
    private var location:Location?=null
    private lateinit var locationManager: LocationManager
    private val fine=ACCESS_FINE_LOCATION
    private val coarse=ACCESS_COARSE_LOCATION
    private val permissions= arrayOf(fine,coarse)
    private var isAllowed=false
    private var useCurrent=false
    private var useSearched=false
    private val MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
    private val SCOPES = arrayOf(MESSAGING_SCOPE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentCartBinding.inflate(inflater,container,false)

        CoroutineScope(Dispatchers.IO).launch {
            getAccessToken()
        }
        fudViewModel=ViewModelProvider(this)[FudViewModel::class.java]
        binding.recyclerCart.setHasFixedSize(true)
        binding.recyclerCart.layoutManager=LinearLayoutManager(requireContext())
        current_lat=""
        current_lon=""
        current_name=""
        searched_lon=""
        searched_lat=""
        searched_name=""
        fudViewModel.cart.observe(viewLifecycleOwner) { list ->
            cartAdapter = CartAdapter(list)
            cartlist = list as ArrayList<Cart>
            binding.recyclerCart.adapter = cartAdapter
            cartAdapter.notifyDataSetChanged()
            if (list.isEmpty()) {
                binding.total.text = "Ksh.0"
            } else {
                total = getSum(list)
                binding.total.text = "Ksh.$total"
            }
        }
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        binding.order.setOnClickListener{
            if (cartlist.isNotEmpty()){
                showAlertDialog()
            }else{
                Snackbar.make(binding.recyclerCart,"Add Items to cart first!",Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.card)
                    .show()
            }
        }
        return binding.root
    }
    private fun getAccessToken(){
        try{
//            val inputstream=requireActivity().assets.open("secret_key_from_database.json")
//            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", inputstream.toString())
//            val credentials=GoogleCredentials.fromStream(inputstream).createScoped(listOf(MESSAGING_SCOPE))
//            credentials.refreshIfExpired()
//            token=credentials.accessToken.tokenValue

            Log.d("CartFragment",token)
        }catch (e:Exception){
            Log.d("CartFragment",e.message.toString())
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (menuInfo==null) return
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when(item.title){
            "Update"->{
                updateItem(item.order)
                true
            }
            "Delete"->{
                deleteItem(item.order)
                true
            }
            else-> super.onContextItemSelected(item)
        }
    }
    private fun deleteItem(position:Int){
        val item=cartlist[position]
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setIcon(R.drawable.baseline_delete_24)
            .setMessage("Are you sure you want remove ${item.productName} from cart?")
            .setPositiveButton("Delete"){_,_->
                fudViewModel.deleteCart(item)
                cartAdapter.notifyItemRemoved(position)
                Toast.makeText(requireContext(), "Deleted ${item.productName} from cart", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateItem(position: Int) {
        val item=cartlist[position]
        getFoodPrice(item.productId)
        count=item.quantity
        sumTotal=item.price
        val view=LayoutInflater.from(requireContext()).inflate(R.layout.update_cart,null)
        val alertbinding=UpdateCartBinding.bind(view)
        alertbinding.icon.setColorFilter(Color.BLACK)
        alertbinding.add.setColorFilter(ContextCompat.getColor(requireContext(),R.color.background2))
        alertbinding.reduce.setColorFilter(ContextCompat.getColor(requireContext(),R.color.background2))
        alertbinding.foodname.text=item.productName
        alertbinding.foodprice.text="Ksh.${item.price}"
        alertbinding.counter.text=item.quantity.toString()
        alertbinding.add.setOnClickListener {
            count++
            sumTotal=count * price
            alertbinding.counter.text=count.toString()
            alertbinding.foodprice.text="Ksh.$sumTotal"

        }
        alertbinding.reduce.setOnClickListener {
            if (count>1){
                count--
            }
            sumTotal=count * price
            alertbinding.counter.text=count.toString()
            alertbinding.foodprice.text="Ksh.$sumTotal"
        }
        AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Update"){_,_->
                val cart=Cart(item.id,item.productId,item.productName,count,item.discount,sumTotal)
                fudViewModel.updateCart(cart)
                Toast.makeText(requireContext(), "Update successful", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun getFoodPrice(productId: String) {
        Firebase.database.getReference("Foods").child(productId).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val item=snapshot.getValue(Food::class.java)!!
                price=item.price?.toInt()?:0
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showAlertDialog() {
        var lat=""
        var lon=""
        var name=""
        val view=LayoutInflater.from(requireContext()).inflate(R.layout.alert_custom,null)
        val alertbinding=AlertCustomBinding.bind(view)
        val alertDialog=AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        alertbinding.icon.setColorFilter(Color.BLACK)
        val fade=Fade().apply {
            duration=500
        }
        TransitionManager.beginDelayedTransition(alertbinding.root,fade)
        alertbinding.searchAuto.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getResults(p0.toString(),alertbinding)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        alertbinding.userChoice.setOnCheckedChangeListener { radioGroup, id ->
            when(id){
                R.id.search_address-> {
                    alertbinding.middle.visibility=View.VISIBLE
                    useSearched=true
                    useCurrent=false
                }
                R.id.current_address-> {
                    alertbinding.middle.visibility=View.GONE
                    useSearched=false
                    useCurrent=true
                    val imm=requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(alertbinding.searchAuto.windowToken,0)
                    getCurrentAddress(alertDialog)
                }
            }
        }
        alertbinding.send.setOnClickListener {
            val userid=Firebase.auth.currentUser!!.uid
            val database=Firebase.database.getReference("Orders")
            val orderId=database.push().key!!
            if (useSearched){
                lat=searched_lat
                lon=searched_lon
                name=searched_name
            }else if (useCurrent){
                lat=current_lat
                lon=current_lon
                name=current_name
            }
            if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon)){
                Toast.makeText(requireContext(), "Address needed", Toast.LENGTH_SHORT).show()
            }else{
                val address= Address(name,lat,lon)
                val map= hashMapOf<String,Any>()
                map["userid"]=userid
                map["address"]=address
                map["total"]=total.toString()
                map["orderId"]=orderId
                map["status"]="0"
                map["time"]=System.currentTimeMillis()
                map["cartlist"]=cartlist
                database.child(orderId).setValue(map).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        fudViewModel.deleteAll()
                        cartAdapter.notifyDataSetChanged()
                        useCurrent=false
                        useSearched=false
                        sendNotification(orderId)
                        alertDialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                        useCurrent=false
                        useSearched=false
                        alertDialog.dismiss()
                    }
                }
            }

        }
        alertbinding.cancel.setOnClickListener {
            useCurrent=false
            useSearched=false
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun getCurrentAddress(dialog: AlertDialog) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if (permissions.all { ContextCompat.checkSelfPermission(requireContext(),it) != PackageManager.PERMISSION_GRANTED }) {
                dialog.dismiss()
                ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CODE)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location !=null){
                current_lat=location?.latitude!!.toString()
                current_lon=location?.longitude!!.toString()
                current_name="Current Address"
            }
        }else{
            Toast.makeText(requireContext(), "Please turn on you location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE->{
                isAllowed = grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                        grantResults[1]==PackageManager.PERMISSION_GRANTED
            }
        }
    }
    private val locationListener=object :LocationListener{
        override fun onLocationChanged(location: Location) {
            current_lat=location.latitude.toString()
            current_lon=location.longitude.toString()
            current_name="Current Address"
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
        }

    }

    private fun getResults(query:String,alertCustomBinding: AlertCustomBinding){
        alertCustomBinding.progress.visibility=View.VISIBLE
        SearchService().searchLocation(query).enqueue(object:Callback<List<Address>>{
            override fun onResponse(call: Call<List<Address>>, response: Response<List<Address>>) {
                if (!response.isSuccessful) {
                    alertCustomBinding.progress.visibility = View.GONE
                    Log.d("CartFragment", "response code ${response.code()}")
                    return
                }
                if (response.body() != null) {
                    val autoadapter =
                        ArrayAdapter(requireContext(), R.layout.drop_down, response.body()!!)
                    (alertCustomBinding.searchAuto as? AutoCompleteTextView)?.apply {
                        setAdapter(autoadapter)
                        alertCustomBinding.progress.visibility = View.GONE
                        setOnItemClickListener { adapterView, view, i, l ->
                            val item = autoadapter.getItem(i) as? Address
                            searched_lat = item?.lat.toString()
                            searched_lon = item?.lon.toString()
                            searched_name = item?.display_name.toString()
                            Toast.makeText(requireContext(), "Latitude:$searched_lat Longitude:$searched_lon", Toast.LENGTH_SHORT).show()
                        }
                    }
                    autoadapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Address>>, t: Throwable) {
                alertCustomBinding.progress.visibility=View.GONE
                Log.d("CartFragment",t.message.toString())
            }

        })
    }
    private fun sendNotification(orderId: String) {
        Firebase.database.getReference("Users").orderByChild("isStaff").equalTo(true)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children){
                        val item=child.getValue(User::class.java)!!
                        val data= mutableMapOf<String,String>()
                        data["title"]="New Order"
                        data["body"]="New Order #$orderId"
                        val message=Message(item.token!!,data)
                        val payload=SendMessageRequest(false,message)
                        ApiService().sendNotification("Bearer $token",payload).enqueue(object:Callback<Message>{
                            override fun onResponse(
                                call: Call<Message>,
                                response: Response<Message>
                            ) {
                                if (response.isSuccessful){
                                    val resp=response.body()
                                    if (resp != null){
                                        Log.d("CartFragment","Message body ${response.body().toString()}")
                                        Toast.makeText(requireContext(), "Order put Successfully", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(requireContext(), "Unexpected error from server", Toast.LENGTH_LONG).show()
                                    }
                                }else{
                                    Log.d("CartFragment", "Error body ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<Message>, t: Throwable) {
                                Log.d("CartFragment",t.message.toString())
                            }

                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun getSum(list: List<Cart>?):Int {
        var total=0
        for (item in list!!){
            total +=item.price
        }
        return total
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager.removeUpdates(locationListener)
        _binding=null
    }
}