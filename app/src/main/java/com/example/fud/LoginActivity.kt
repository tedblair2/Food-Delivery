package com.example.fud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.example.fud.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.editname.requestFocus()
        binding.btnNext.setOnClickListener {
            val phone=binding.editphone.text.toString()
            val name=binding.editname.text.toString()
            if (TextUtils.isEmpty(phone)){
                binding.editphone.error="Please provide phone number"
            }else if(TextUtils.isEmpty(name)){
                binding.editname.error="Please provide your name"
            }else{
                startActivity(Intent(this,OtpVerificationActivity::class.java).apply {
                    putExtra("number",phone)
                    putExtra("name",name)
                })
            }
        }

    }
}