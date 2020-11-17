package edu.utap.mapreduce

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Item
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.PlayerStatus
import edu.utap.mapreduce.model.Room
import edu.utap.mapreduce.model.RoomKind
import edu.utap.mapreduce.model.Stage
import java.math.BigInteger
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
import kotlinx.android.synthetic.main.activity_game.switchV

class GameActivity : AppCompatActivity() {
    private val model: GameViewModel by viewModels()
    private lateinit var stage: Stage
    private lateinit var player: Player
    private lateinit var obtainedItemListAdapter: ObtainedItemListAdapter
    private lateinit var enemyListAdapter: EnemyListAdapter
    private lateinit var chestRoomItemListAdapter: ChestRoomItemListAdapter
    private lateinit var roomDetailV: RecyclerView

    // room view id -> room index
    private var viewId2Idx = mutableMapOf<Int, Int>()

    private lateinit var h: BigInteger
    private lateinit var w: BigInteger

    // TODO: should calculate the interval
    companion object {
        private const val RoomDisplaySize = 60
        private const val RoomInterval = 15
        private val SwitchTextList = listOf("SHOW ROOM DETAIL", "SHOW STAGE")
        const val PlayerWins = "winOrNot"
    }

    private fun dpToPixel(dp: Double): Double {
        return dp * resources.displayMetrics.density
    }

    /*
        Switch the display of main container when the player is interacting with the room.

        It is a little bit hard to read now, because the behavior is strangely controlled by the UI
        i.e. what the button currently displays. The tradeoff is that there is no redundant
        information.
     */
    private fun onSwitchButtonClick(button: View) {
        button as Button
        if (player.status == PlayerStatus.INTERACT_WITH_STAGE) {
            Toast.makeText(
                this,
                "There is no room detail; please interact with the stage",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        when (button.text) {
            SwitchTextList[0] -> drawRoomDetail(stage.rooms[player.roomIdx])
            SwitchTextList[1] -> redrawStage(force = true)
            else -> Log.e("aaa", "Impossible button text ${button.text}")
        }

//        button.text = SwitchTextList[1 - SwitchTextList.indexOf(button.text)]
    }

    private fun onRoomClick(roomView: View) {
        val playerRoom = stage.rooms[player.roomIdx]
        val clickedRoom = stage.rooms[viewId2Idx[roomView.id]!!]

        if (clickedRoom.visited) {
            Toast.makeText(this, "You have visited this room", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: as we have many statuses, we should use a when clause.
        if (player.status == PlayerStatus.INTERACT_WITH_ROOM) {
            Toast.makeText(
                this,
                "Please finish the interaction of the current room",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!playerRoom.canReach(clickedRoom, stage)) {
            if (playerRoom.isAdjacent(clickedRoom)) {
                // use 'paths' to make a room reachable
                if (player.numPaths > 0) {

                    player.numPaths --

                    stage.paths[playerRoom.id].add(clickedRoom)
                    stage.paths[clickedRoom.id].add(playerRoom)

                    Toast.makeText(this, "You can enter the room now!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No more paths", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Room unreachable", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val (success, msg) = clickedRoom.tryEnter(player)
        if (msg.isNotEmpty()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        if (!success) {
            return
        }

        player.roomIdx = viewId2Idx[roomView.id]!!

        val result = clickedRoom.tryQuickAccess(player)
        if (!result.first) {
            Toast.makeText(this, result.second, Toast.LENGTH_SHORT).show()
        } else {
            player.status = PlayerStatus.INTERACT_WITH_ROOM
            drawRoomDetail(clickedRoom)
        }

        clickedRoom.visited = true

        player.roomIdx = viewId2Idx[roomView.id]!!
        player.obtainedItems.forEach {
            it.doRecharge()
        }

        model.setPlayer(player)
        model.setStage(stage)
        Log.d("aaa", "clicking button (${roomView.x}, ${roomView.y})")
    }

    private fun endGame(win: Boolean) {
        val resultIntent = Intent(this, EndActivity::class.java)
        val winResult = Bundle()

        winResult.putBoolean(PlayerWins, win)

        resultIntent.putExtras(winResult)
        startActivity(resultIntent)
    }

    private fun drawRoomDetail(room: Room) {
        switchV.text = SwitchTextList[1]
        mapContainer.removeAllViews()
        // only initialize detail recycler view once to be a little more efficient
        if (!this::roomDetailV.isInitialized) {
            roomDetailV = RecyclerView(this)
            roomDetailV.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            roomDetailV.layoutManager = LinearLayoutManager(this)
        }

        when (room.kind) {
            RoomKind.NORMAL, RoomKind.BOSS -> {
                if (this::enemyListAdapter.isInitialized) {
                    enemyListAdapter.player = player
                    enemyListAdapter.room = room
                    enemyListAdapter.stage = stage
                } else {
                    enemyListAdapter = EnemyListAdapter(player, room, stage, model)
                }
                roomDetailV.adapter = enemyListAdapter
                enemyListAdapter.notifyDataSetChanged()
            }
            RoomKind.CHEST -> {
                val itemList = listOf(Item.fetchItem(player.obtainedItems)!!)
                Log.d("aaa", itemList[0].name)
                if (this::chestRoomItemListAdapter.isInitialized) {
                    chestRoomItemListAdapter.player = player
                    chestRoomItemListAdapter.stage = stage
                    chestRoomItemListAdapter.items = itemList
                } else {
                    chestRoomItemListAdapter = ChestRoomItemListAdapter(
                        player,
                        stage,
                        itemList,
                        model
                    )
                }
                roomDetailV.adapter = chestRoomItemListAdapter
                chestRoomItemListAdapter.notifyDataSetChanged()
            }
            else -> {
            }
        }
        mapContainer.addView(roomDetailV)
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
    private fun redrawStage(force: Boolean = false) {
        if (player.status != PlayerStatus.INTERACT_WITH_STAGE && !force) {
            return
        }

        switchV.text = SwitchTextList[0]
        mapContainer.removeAllViews()
        viewId2Idx.clear()

        val playerRoom = stage.rooms[player.roomIdx]

        stage.rooms.forEachIndexed { idx, room ->
            val button = Button(this)
            button.layoutParams = FrameLayout.LayoutParams(
                dpToPixel(RoomDisplaySize.toDouble()).toInt(),
                dpToPixel(RoomDisplaySize.toDouble()).toInt()
            )

            // get the width and height of mapContainer, center buttons
            mapContainer.viewTreeObserver.addOnGlobalLayoutListener(
                object :
                    OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mapContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        h = mapContainer.height.toBigInteger()
                        w = mapContainer.width.toBigInteger()
                        // h = 1589, w = 1080
                        val paddingX = (w.toInt() / 2 - dpToPixel(180.toDouble())).toInt()
                        val paddingY = (h.toInt() / 2 - dpToPixel(180.toDouble())).toInt()
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

            // draw buttons based on RoomKind
            if (!room.visited) {
                if (playerRoom.canReach(room, stage)) {
                    when (room.kind) {
                        RoomKind.BOSS -> button.setBackgroundResource(R.drawable.boss606024)
                        RoomKind.CHEST -> button.setBackgroundResource(R.drawable.chest606024)
                        RoomKind.NORMAL -> button.setBackgroundResource(R.drawable.n606024)
                    }
                } else { button.setBackgroundColor(Color.parseColor("#ffde7d")) }
            } else {
                button.setBackgroundColor(Color.parseColor("#00b8a9"))
            }

            if (player.roomIdx == idx) {
                button.setBackgroundResource(R.drawable.player)
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

                hpV.text = "HP: ${it.hp}"
                atkV.text = "ATK: ${it.atk}"
                defV.text = "DEF: ${it.def}"
                spdV.text = "SPD: ${it.spd}"

                keysV.text = "Keys: ${it.numKeys}"
                pathsV.text = "Paths: ${it.numPaths}"
                chestsV.text = "Chests: ${it.numChests}"
                coinsV.text = "Coins: ${it.numCoins}"

                if (this::obtainedItemListAdapter.isInitialized) {
                    obtainedItemListAdapter.player = it
                    obtainedItemListAdapter.notifyDataSetChanged()
                } else {
                    obtainedItemListAdapter = ObtainedItemListAdapter(
                        it,
                        model.observeStage().value!!,
                        model
                    )
                    itemsContainer.adapter = obtainedItemListAdapter
                    itemsContainer.layoutManager = LinearLayoutManager(this)
                }

                if (player.status == PlayerStatus.WIN) {
                    endGame(true)
                }
                if (player.status == PlayerStatus.LOSE) {
                    endGame(false)
                }
            }
        )

        model.observeStage().observe(
            this,
            {
                stage = it
                stageV.text = "Stage ${it.curStage}"

                switchV.text = "SHOW ROOM DETAIL"

                if (this::obtainedItemListAdapter.isInitialized) {
                    obtainedItemListAdapter.stage = it
                }

                redrawStage()
            }
        )

        switchV.setOnClickListener {
            onSwitchButtonClick(it)
        }
    }
}
