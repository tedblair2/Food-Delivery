package com.example.fud

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fud.databinding.ActivityHomeBinding
import com.example.fud.fragments.CartFragment
import com.example.fud.fragments.MenuFragment
import com.example.fud.fragments.OrdersFragment
import com.example.fud.service.NotificationService
import com.example.fud.viewmodel.FudViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val REQUEST_PERMISSION=2000
class HomeActivity : AppCompatActivity(){
    private lateinit var binding:ActivityHomeBinding
    private val permission=POST_NOTIFICATIONS
    private var isAllowed=false
    private lateinit var fudViewModel: FudViewModel
    private val TAG="HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestNotify()
        fudViewModel=ViewModelProvider(this)[FudViewModel::class.java]
        fudViewModel.cart.observe(this){list->
            val menuitem=binding.bottomView.menu.findItem(R.id.cart)
            val badge=binding.bottomView.getOrCreateBadge(menuitem.itemId)
            if (list.isNotEmpty()){
                badge.isVisible=true
                badge.number=list.size
                badge.backgroundColor=ContextCompat.getColor(this,R.color.background2)
            }else{
                badge.isVisible=false
            }
        }
        val sender=intent.getStringExtra("sender")
        if (sender != null && sender == "service"){
            loadFragment("Orders",OrdersFragment())
        }else{
            loadFragment("Menu",MenuFragment())
        }
        startService(Intent(this,NotificationService::class.java))
        binding.bottomView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.menu-> loadFragment("Menu",MenuFragment())
                R.id.orders->loadFragment("Orders",OrdersFragment())
                R.id.cart->loadFragment("Cart",CartFragment())
            }
            true
        }
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            Firebase.database.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("token")
//                .setValue(token)
//        })

    }
    private fun loadFragment(title:String,fragment: Fragment){
        setTitle(title)
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment).commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_PERMISSION->{
                if ((grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)){
                    isAllowed=true
                }else{
                    isAllowed=false
                    requestNotify()
                }
            }
        }
    }
    private fun requestNotify(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
            AlertDialog.Builder(this)
                .setTitle("Request Permission")
                .setMessage("Allow access to post notifications")
                .setPositiveButton("Ok"){_,_->
                    ActivityCompat.requestPermissions(baseContext as Activity, arrayOf(permission), REQUEST_PERMISSION)
                }
                .setNegativeButton("Cancel"){dialog,_->
                    Toast.makeText(baseContext, "Access denied.Cannot show notifications", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }.show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_PERMISSION)
        }
    }
    private fun signOut() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out")
            .setPositiveButton("Yes"){_,_->
                startActivity(Intent(this,LoginActivity::class.java).apply {
                    flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                Firebase.auth.signOut()
            }
            .setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }.create()
            .show()
    }
}