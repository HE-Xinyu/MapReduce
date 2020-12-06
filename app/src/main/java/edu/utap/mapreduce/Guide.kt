package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.guide.guideImageView
import kotlinx.android.synthetic.main.guide.startBInGuide

class Guide : AppCompatActivity() {
    private var imageNum = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guide)

        guideImageView.setOnClickListener {
            imageNum = when (imageNum) {
                1 -> {
                    guideImageView.setImageResource(R.drawable.guide_enemy)
                    2
                }
                2 -> {
                    guideImageView.setImageResource(R.drawable.guide_log)
                    3
                }
                else -> {
                    guideImageView.setImageResource(R.drawable.guide_map)
                    1
                }
            }
        }

        startBInGuide.setOnClickListener {
            val newGameIntent = Intent(this, MainActivity::class.java)
            startActivity(newGameIntent)
        }
    }
}
