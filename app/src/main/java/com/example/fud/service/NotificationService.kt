package com.example.fud.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fud.HomeActivity
import com.example.fud.R
import com.example.fud.model.Order
import com.example.fud.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

private const val channel_id="FuD"
class NotificationService : Service() {
    private val notificationId= Random.nextInt()
    private lateinit var reference:DatabaseReference
    private var currentuser=""

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        reference=FirebaseDatabase.getInstance().getReference("Orders")
        currentuser=FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        reference.addChildEventListener(childEventListener)
        return super.onStartCommand(intent, flags, startId)
    }
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel= NotificationChannel(channel_id,"${R.string.app_name}", NotificationManager.IMPORTANCE_HIGH)
            channel.description="${R.string.app_name}"
            val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val childEventListener=object :ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val order=snapshot.getValue(Order::class.java)
            if (order !=null && order.userid==currentuser){
                showNotification(order)
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }
    private fun getPhone(userid: String): String {
        var phone=""
        Firebase.database.getReference("Users").child(userid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(User::class.java)!!
                phone= user.number!!
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return phone
    }

    private fun showNotification(item: Order) {
        val phone=getPhone(item.userid!!)
        val intent=Intent(baseContext,HomeActivity::class.java)
        intent.putExtra("userphone",phone)
        intent.putExtra("sender","service")
        val pendingIntent=PendingIntent.getActivity(baseContext,0,intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder=NotificationCompat.Builder(applicationContext, channel_id)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_restaurant)
            .setContentTitle("Order Update")
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().
            bigText("Your Order #${item.orderId} has been updated to ${getStatus(item.status!!)}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        val notificationManger=NotificationManagerCompat.from(baseContext)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManger.notify(notificationId,builder)
    }
    private fun getStatus(number:String):String{
        return when (number) {
            "0" -> {
                "Placed"
            }
            "1" -> {
                "On The Way"
            }
            else -> {
                "Shipped"
            }
        }
    }
}