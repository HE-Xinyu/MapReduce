package edu.utap.mapreduce

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.mapreduce.model.BattleResult
import edu.utap.mapreduce.model.BattleSimulator
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Item
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.RoomKind
import edu.utap.mapreduce.model.Stage
import kotlinx.android.synthetic.main.activity_game.atkV
import kotlinx.android.synthetic.main.activity_game.chestsV
import kotlinx.android.synthetic.main.activity_game.coinsV
import kotlinx.android.synthetic.main.activity_game.defV
import kotlinx.android.synthetic.main.activity_game.hpV
import kotlinx.android.synthetic.main.activity_game.itemsContainer
import kotlinx.android.synthetic.main.activity_game.keysV
import kotlinx.android.synthetic.main.activity_game.mapContainer
import kotlinx.android.synthetic.main.activity_game.pathsV
import kotlinx.android.synthetic.main.activity_game.spdV
import kotlinx.android.synthetic.main.activity_game.stageV

class GameActivity : AppCompatActivity() {
    private val model: GameViewModel by viewModels()
    private lateinit var stage: Stage
    private lateinit var player: Player
    private lateinit var itemListAdapter: ItemListAdapter

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

        if (!playerRoom.canReach(clickedRoom, stage)) {
            if (playerRoom.isAdjacent(clickedRoom)) {
                // use 'paths' to make a room reachable
                if (player.numPaths > 0) {
                    // TODO 测试
                    player.numPaths -= 1
                    stage.paths[playerRoom.id].add(clickedRoom)
                    stage.paths[clickedRoom.id].add(playerRoom)
                } else {
                    Toast.makeText(this, "No more paths", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Room unreachable", Toast.LENGTH_SHORT).show()
            }
            return
        }

        if (!clickedRoom.visited) {
            clickedRoom.visited = true
            roomView.setBackgroundResource(R.drawable.n606024)

            when (clickedRoom.kind) {
                RoomKind.NORMAL, RoomKind.BOSS -> {
                    val result = BattleSimulator.oneOnOne(player, clickedRoom.enemy!!, stage)
                    if (result == BattleResult.LOSE) {
                        endGame(false)
                    }
                    if (clickedRoom.kind == RoomKind.BOSS) {
                        if (stage.curStage == Stage.MaxStages) {
                            endGame(true)
                        } else {
                            // player gets past the current stage, advance to the next one.
                            stage = Stage(stage.curStage + 1)
                        }
                    }
                }
                RoomKind.CHEST -> {
                    if (player.numKeys == 0) {
                        Toast.makeText(this, "You don't have a key.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "You used a key.", Toast.LENGTH_SHORT).show()
                        player.numKeys--

                        val item = Item.fetchItem(player.obtainedItems)
                        if (item != null) {
                            player.obtainedItems.add(item)
                        } else {
                            Toast.makeText(
                                this,
                                "You have exhausted the item pool",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Unhandled room kind ${clickedRoom.kind}",
                        Toast.LENGTH_SHORT
                    ).show()
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
        // drew paths
        // TODO：画线
        val hlineView = View(this)
        hlineView.setBackgroundColor(Color.parseColor("#000000"))
        hlineView.layoutParams = FrameLayout.LayoutParams(
            dpToPixel(RoomInterval.toDouble()).toInt(),
            dpToPixel(1.toDouble()).toInt()
        )
        val vlineView = View(this)
        vlineView.setBackgroundColor(Color.parseColor("#000000"))
        vlineView.layoutParams = FrameLayout.LayoutParams(
            dpToPixel(1.toDouble()).toInt(),
            dpToPixel(RoomInterval.toDouble()).toInt()
        )

//        fun drawPaths() {
//            val hlineView = View(this)
//            hlineView.setBackgroundColor(Color.parseColor("#000000"))
//            val vlineView = View(this)
//            vlineView.setBackgroundColor(Color.parseColor("#000000"))
//        }

        stage.rooms.forEachIndexed { idx, room ->
            val button = Button(this)
            button.layoutParams = FrameLayout.LayoutParams(
                dpToPixel(RoomDisplaySize.toDouble()).toInt(),
                dpToPixel(RoomDisplaySize.toDouble()).toInt()
            )
            // www
//            button.setBackgroundResource(R.drawable.n606024)
            if (player.roomIdx == idx) {
                button.setBackgroundResource(R.drawable.player)
            } else {
                button.text = "" // 其实我觉得这行就可以删了?
            }
            // draw paths
            val playerRoom = stage.rooms[player.roomIdx]
            val newRoom = stage.rooms[idx]
//            if (playerRoom.canReach(clickedRoom, stage)) {
//                mapContainer.addView(hlineView)
//                mapContainer.addView(vlineView)
//            }

            // get the width and height of mapContainer, center buttons
            mapContainer.viewTreeObserver.addOnGlobalLayoutListener(
                object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mapContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val h = mapContainer.height
                        val w = mapContainer.width
                        // h = 1589, w = 1080
                        val paddingX = (w / 2 - dpToPixel(180.toDouble())).toInt()
                        val paddingY = (h / 2 - dpToPixel(180.toDouble())).toInt()
                        button.x = paddingX + mapContainer.x + room.x * (
                            dpToPixel(
                                (RoomDisplaySize + RoomInterval).toDouble()
                            ).toInt()
                            )
                        button.y = paddingY + mapContainer.y + room.y * (
                            dpToPixel(
                                (RoomDisplaySize + RoomInterval).toDouble()
                            ).toInt()
                            )
                        button.setOnClickListener { roomView ->
                            onRoomClick(roomView)
                        }
                    }
                }
            )

//            // set background color of buttons
//            when (room.kind) {
//                RoomKind.BOSS -> button.setBackgroundColor(Color.parseColor("#f6416c"))
//                RoomKind.CHEST -> button.setBackgroundColor(Color.parseColor("#ffde7d"))
//                else -> button.setBackgroundColor(Color.parseColor("#f8f3d4"))
//            }
            // set background color of buttons
            when (room.kind) {
                RoomKind.BOSS -> button.setBackgroundResource(R.drawable.boss606024)
                RoomKind.CHEST -> button.setBackgroundResource(R.drawable.chest606024)
                else -> button.setBackgroundResource(R.drawable.lock606024)
            }
            if (playerRoom.canReach(newRoom, stage)) {
                button.setBackgroundResource(R.drawable.n606024)
            }
            if (newRoom.visited) {
                button.setBackgroundResource(R.drawable.n606024)
            }
            // use generateViewId() to avoid conflicts (hopefully)
            button.id = View.generateViewId()
            viewId2Idx[button.id] = idx
            mapContainer.addView(button)

//            if (playerRoom.canReach(newRoom, stage)) {
//                if ( room.x < 5) {
//                    Log.d("cccc", "xiao yu 5")
//                    mapContainer.addView(hlineView)
//                    hlineView.x = button.x + dpToPixel(RoomDisplaySize.toDouble()).toInt()
//                    hlineView.y = button.y + dpToPixel(RoomDisplaySize.toDouble()).toInt() / 2
//                }
// //                if ( room.x < 5){
// //                    mapContainer.addView(vlineView)
// //                    vlineView.x = button.x + dpToPixel(RoomDisplaySize.toDouble()).toInt() / 2
// //                    vlineView.y = button.y + dpToPixel(RoomDisplaySize.toDouble()).toInt()
// //                }
//            }
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
                hpV.text = "HP: ${it.hp}"
                atkV.text = "ATK: ${it.atk}"
                defV.text = "DEF: ${it.def}"
                spdV.text = "SPD: ${it.spd}"

                keysV.text = "Keys: ${it.numKeys}"
                pathsV.text = "Paths: ${it.numPaths}"
                chestsV.text = "Chests: ${it.numChests}"
                coinsV.text = "Coins: ${it.numCoins}"

                if (this::itemListAdapter.isInitialized) {
                    itemListAdapter.player = it
                    itemListAdapter.notifyDataSetChanged()
                } else {
                    itemListAdapter = ItemListAdapter(it)
                    itemsContainer.adapter = itemListAdapter
                    itemsContainer.layoutManager = LinearLayoutManager(this)
                }

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
