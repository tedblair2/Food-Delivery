package com.example.fud

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.fud.databinding.ActivityOtpVerificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import `in`.aabhasjindal.otptextview.OTPListener
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var dialog:ProgressDialog
    private var number=""
    private var name=""
    private var verificationId=""
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        dialog= ProgressDialog(this@OtpVerificationActivity)
        dialog.setMessage("Sending OTP....")
        dialog.setCancelable(false)
        dialog.show()
        auth= FirebaseAuth.getInstance()
        number= intent.getStringExtra("number")!!
        name=intent.getStringExtra("name")!!
        binding.title.text="Verify $number"

        val options=PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        binding.otpview.otpListener=object :OTPListener{
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                binding.progress.visibility= View.VISIBLE
                val credentials=PhoneAuthProvider.getCredential(verificationId,otp)
                auth.signInWithCredential(credentials).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        val user= hashMapOf<String,Any>()
                        user["number"]=number
                        user["name"]=name
                        user["userid"]=Firebase.auth.currentUser!!.uid
                        user["isStaff"]=false
                        Firebase.database.reference.child("Users").child(Firebase.auth.currentUser!!.uid).setValue(user).addOnCompleteListener {add->
                            if (add.isSuccessful){
                                startActivity(Intent(this@OtpVerificationActivity,HomeActivity::class.java).apply {
                                    flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                                binding.progress.visibility=View.GONE
                            }else{
                                binding.progress.visibility=View.GONE
                                Toast.makeText(this@OtpVerificationActivity, add.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        binding.progress.visibility=View.GONE
                        Toast.makeText(this@OtpVerificationActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private val callbacks=object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            dialog.dismiss()
            verificationId=p0
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            binding.otpview.requestFocus()
        }

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
        }

        override fun onVerificationFailed(p0: FirebaseException) {
        }

    }
}