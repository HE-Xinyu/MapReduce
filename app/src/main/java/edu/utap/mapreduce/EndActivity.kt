package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.end.endBackButton
import kotlinx.android.synthetic.main.end.endTV

class EndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.end)

        val changeTextStr = intent.getStringExtra("changeText")
        val endGameTV = findViewById<TextView>(R.id.endTV)
        endGameTV.text = changeTextStr

        endBackButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}
