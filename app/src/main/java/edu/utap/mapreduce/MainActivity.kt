package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.guideButton
import kotlinx.android.synthetic.main.activity_main.startGameButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        guideButton.setOnClickListener {
            val intent2 = Intent(this, Guide::class.java)
            startActivity(intent2)
        }
    }
}
