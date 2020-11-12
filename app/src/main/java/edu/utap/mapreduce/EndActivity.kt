package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.end.endBackButton

class EndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.end)

        val resultBoo = intent.extras.getBoolean("resultInfo")
        val endGameTV = findViewById<TextView>(R.id.endTV)
        if (resultBoo) { endGameTV.text = "You\nWin" } else { endGameTV.text = "Game\n Over" }

        endBackButton.setOnClickListener {
            val newGameIntent = Intent(this, MainActivity::class.java)
            startActivity(newGameIntent)
        }
    }
}
