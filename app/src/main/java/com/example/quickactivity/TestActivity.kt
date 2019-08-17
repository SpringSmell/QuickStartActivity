package com.example.quickactivity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.quick.startactivity.StartActivity

class TestActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        titleTv.text = intent.getStringExtra("TITLE")
        titleTv.setOnClickListener {
            setResult(RESULT_OK, StartActivity.Builder().addParams("data", "这是返回值").build())
            onBackPressed()
        }
    }

    companion object {
        fun startActivity(context: Activity, title: String): StartActivity.Builder {
            return StartActivity.Builder(context, TestActivity::class.java)
                .addParams("TITLE", title)
        }
    }
}