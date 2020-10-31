package edu.utap.mapreduce

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.Stage
import kotlinx.android.synthetic.main.activity_game.atkV
import kotlinx.android.synthetic.main.activity_game.defV
import kotlinx.android.synthetic.main.activity_game.hpV
import kotlinx.android.synthetic.main.activity_game.mapContainer
import kotlinx.android.synthetic.main.activity_game.spdV

class GameActivity : AppCompatActivity() {
    private val model: GameViewModel by viewModels()
    private lateinit var stage: Stage
    private lateinit var player: Player

    // TODO: should calculate the interval
    companion object {
        private const val RoomDisplaySize = 60
        private const val RoomInterval = 15
    }

    private fun dpToPixel(dp: Double): Double {
        return dp * resources.displayMetrics.density
    }

    private fun onRoomClick(roomView: View) {

        Log.d("aaa", "clicking button (${roomView.x}, ${roomView.y})")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model.observePlayer().observe(
            this,
            {
                player = it
                hpV.text = it.hp.toString()
                atkV.text = it.atk.toString()
                defV.text = it.def.toString()
                spdV.text = it.spd.toString()

                if (it.hp <= 0) {
                    val intent = Intent(this, EndActivity::class.java)
                    startActivity(intent)
                }
            }
        )

        model.observeStage().observe(
            this,
            {
                stage = it
                it.rooms.forEach {
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
                        roomView ->
                        onRoomClick(roomView)
                    }
                    // use generateViewId() to avoid conflicts (hopefully)
                    button.id = View.generateViewId()
                    mapContainer.addView(button)
                }
            }
        )
    }
}
