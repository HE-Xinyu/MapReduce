package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mapreduce.GameActivity.Companion.PlayerWins
import kotlinx.android.synthetic.main.end.endBackButton
import kotlinx.android.synthetic.main.end.endTV

class EndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.end)

        val playerWins = intent.extras.getBoolean(PlayerWins)
        endTV.text = if (playerWins) { "You\nWin" } else { "Game\n Over" }

        endBackButton.setOnClickListener {
            val newGameIntent = Intent(this, MainActivity::class.java)
            startActivity(newGameIntent)
        }
    }
}
