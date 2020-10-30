package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mapreduce.model.GameViewModel
import kotlinx.android.synthetic.main.activity_game.atkV
import kotlinx.android.synthetic.main.activity_game.defV
import kotlinx.android.synthetic.main.activity_game.hpV
import kotlinx.android.synthetic.main.activity_game.mapContainer
import kotlinx.android.synthetic.main.activity_game.spdV

class GameActivity : AppCompatActivity() {
    private val model: GameViewModel by viewModels()
    private val RoomDisplaySize = 60
    private val RoomInterval = 15

    private fun dpToPixel(dp: Double): Double {
        return dp * resources.displayMetrics.density
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model.observePlayer().observe(
            this,
            {
                hpV.text = it.hp.toString()
                atkV.text = it.atk.toString()
                defV.text = it.def.toString()
                spdV.text = it.spd.toString()

//                wwww
                if (it.hp <= 0){
                    val intent = Intent(this, EndActivity::class.java)
                    startActivity(intent)
                }
            }
        )

        model.observeMap().observe(
            this,
            {
                it.forEach {
                    room ->
                    val button = Button(this)
                    button.layoutParams = FrameLayout.LayoutParams(
                        dpToPixel(RoomDisplaySize.toDouble()).toInt(),
                        dpToPixel(RoomDisplaySize.toDouble()).toInt()
                    )
                    button.text = "(${room.x}, ${room.y})"
                    button.x = mapContainer.x + room.x * (
                        dpToPixel(
                            (RoomDisplaySize + RoomInterval).toDouble()
                        ).toInt()
                        )
                    button.y = mapContainer.y + room.y * (
                        dpToPixel(
                            (RoomDisplaySize + RoomInterval).toDouble()
                        ).toInt()
                        )
                    button.setOnClickListener {
                        Log.d("aaa", "clicking button (${room.x}, ${room.y})")
                    }
                    mapContainer.addView(button)
                }
            }
        )
    }
}
