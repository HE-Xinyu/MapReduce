package edu.utap.mapreduce.model

import edu.utap.mapreduce.IAbleToFight
import kotlin.random.Random

enum class PlayerStatus {
    WIN,
    LOSE,
    INTERACT_WITH_STAGE,
    INTERACT_WITH_ROOM,
    INTERACT_WITH_ITEM,
}

class Player(
    override var hp: Int,
    var atk: Int,
    var def: Int,
    var spd: Int,
) : IAbleToFight {
    var roomIdx = -1
    /*
        The items owned by the player.
     */
    var obtainedItems = emptyList<Item>().toMutableList()

    var numKeys = 0
    var numPaths = 0
    var numCoins = 0
    var numChests = 0

    var status = PlayerStatus.INTERACT_WITH_STAGE

    var currentActivatedItem: Item? = null

    /*
        Abilities that can be changed by items
     */
    var canSeeChestRoom = false
    var canSeeShop = false
    var canSeeBossRoom = false
    var hasMapReduce = false

    var statsMultiplier = 1

    fun initPosition(stage: Stage) {
        while (true) {
            roomIdx = Random.nextInt(Stage.SideLength * Stage.SideLength)
            if (stage.rooms[roomIdx].kind == RoomKind.NORMAL) {
                stage.rooms[roomIdx].visited = true
                return
            }
        }
    }

    fun beginStatsBoost() {
        hp *= statsMultiplier
        atk *= statsMultiplier
        def *= statsMultiplier
        spd *= statsMultiplier
    }

    fun endStatsBoost() {
        hp /= statsMultiplier
        atk /= statsMultiplier
        def /= statsMultiplier
        spd /= statsMultiplier
    }
}
