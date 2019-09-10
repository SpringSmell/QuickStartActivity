package com.example.quickactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.quick.startactivity.StartActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        titleTv.text = "点击去上个页面"
        titleTv.setOnClickListener {
            TestActivity.startActivity(this, "点击返回")
                .action { resultCode, data ->
                    Toast.makeText(this, data!!.getStringExtra("data"), Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        StartActivity.onActivityResult(requestCode, resultCode, data)
    }
}
