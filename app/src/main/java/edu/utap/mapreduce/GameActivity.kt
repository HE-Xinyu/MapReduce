package edu.utap.mapreduce

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mapreduce.model.BattleResult
import edu.utap.mapreduce.model.BattleSimulator
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.RoomKind
import edu.utap.mapreduce.model.Stage
import kotlinx.android.synthetic.main.activity_game.atkV
import kotlinx.android.synthetic.main.activity_game.defV
import kotlinx.android.synthetic.main.activity_game.hpV
import kotlinx.android.synthetic.main.activity_game.mapContainer
import kotlinx.android.synthetic.main.activity_game.spdV
import kotlinx.android.synthetic.main.activity_game.stageV

class GameActivity : AppCompatActivity() {
    private val model: GameViewModel by viewModels()
    private lateinit var stage: Stage
    private lateinit var player: Player

    // room view id -> room index
    private var viewId2Idx = mutableMapOf<Int, Int>()

    // TODO: should calculate the interval
    companion object {
        private const val RoomDisplaySize = 60
        private const val RoomInterval = 15
    }

    private fun dpToPixel(dp: Double): Double {
        return dp * resources.displayMetrics.density
    }

    private fun onRoomClick(roomView: View) {
        val playerRoom = stage.rooms[player.roomIdx]
        val clickedRoom = stage.rooms[viewId2Idx[roomView.id]!!]

        if (!playerRoom.canReach(clickedRoom)) {
            Toast.makeText(this, "Room unreachable", Toast.LENGTH_SHORT).show()
            return
        }
        if (clickedRoom.visited) {
            Toast.makeText(this, "Room already visited", Toast.LENGTH_SHORT).show()
            return
        }

        clickedRoom.visited = true

        // TODO: battle should happen here
        when (clickedRoom.kind) {
            RoomKind.NORMAL, RoomKind.BOSS -> {
                val result = BattleSimulator.oneOnOne(player, clickedRoom.enemy)
                if (result == BattleResult.LOSE) {
                    endGame(false)
                }
                if (clickedRoom.kind == RoomKind.BOSS) {
                    endGame(true)
                }
            }
        }

        player.roomIdx = viewId2Idx[roomView.id]!!

        model.setPlayer(player)
        model.setStage(stage)
        Log.d("aaa", "clicking button (${roomView.x}, ${roomView.y})")
    }

    private fun endGame(win: Boolean) {
        // TODO
        Toast.makeText(this, "Win: $win", Toast.LENGTH_SHORT).show()
    }

    /*
        Redraw the stage.

        TODO: it is extremely inefficient right now. Sometimes we just want to update the
            player position but it ends up redrawing everything. An adapter can probably help.
            Since we may do some crazy stuff with the map, it's probably better to use the
            most basic AdapterView:
            https://developer.android.com/reference/kotlin/android/widget/AdapterView
     */
    @SuppressLint("SetTextI18n")
    private fun redrawStage() {
        mapContainer.removeAllViews()
        viewId2Idx.clear()
        stage.rooms.forEachIndexed {
            idx, room ->
            val button = Button(this)
            button.layoutParams = FrameLayout.LayoutParams(
                dpToPixel(RoomDisplaySize.toDouble()).toInt(),
                dpToPixel(RoomDisplaySize.toDouble()).toInt()
            )
            if (player.roomIdx == idx) {
                button.text = "(${room.x}, ${room.y}) P"
            } else {
                button.text = "(${room.x}, ${room.y})"
            }
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
            viewId2Idx[button.id] = idx
            mapContainer.addView(button)
        }
    }

    @SuppressLint("SetTextI18n")
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

                // TODO: the end game event should not happen here.
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
                stageV.text = "Stage ${it.curStage}"
                redrawStage()
            }
        )
    }
}
