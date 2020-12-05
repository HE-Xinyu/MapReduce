package edu.utap.mapreduce.model

import android.util.Log
import kotlin.random.Random

class Stage(var curStage: Int) {
    // note: if we decide to change n as player progress, the rooms and paths need to be lateinit.
    var rooms = emptyList<Room>().toMutableList()
    var paths = List(SideLength * SideLength) {
        emptySet<Room>().toMutableSet()
    }

    companion object {
        const val MaxStages = 3
        const val SideLength: Int = 5
        val PathProbs = listOf(0.9, 0.6, 0.3)
    }

    private fun doInit() {
        rooms.clear()
        paths = List(SideLength * SideLength) {
            emptySet<Room>().toMutableSet()
        }

        val numBossRooms = 1
        val numShops = 1
        val numChestRooms = 2
        val numNormalRooms = SideLength * SideLength - numBossRooms - numShops - numChestRooms

        val roomKindList = emptyList<RoomKind>().toMutableList()
        for (i in 0 until numBossRooms) {
            roomKindList.add(RoomKind.BOSS)
        }

        for (i in 0 until numShops) {
            roomKindList.add(RoomKind.SHOP)
        }

        for (i in 0 until numChestRooms) {
            roomKindList.add(RoomKind.CHEST)
        }

        for (i in 0 until numNormalRooms) {
            roomKindList.add(RoomKind.NORMAL)
        }

        roomKindList.shuffle()

        // 1. initialize rooms
        for (i in 0 until SideLength) {
            for (j in 0 until SideLength) {
                val id = i * SideLength + j
                /*
                    The room id is simply the index of it in the stage.
                    If two rooms are merged into one, then they share the same id.
                 */
                rooms.add(Room(i, j, roomKindList[id], id))
            }
        }

        /*
            2. Initialize paths

            the paths should have no direction.

            This will be much more complex than it is right now, after we add more rooms
            and fine-tune everything
         */
        for (thisRoom in rooms) {
            for (otherRoom in rooms) {
                // prevent randomization twice
                if (thisRoom.isAdjacent(otherRoom) && thisRoom.id < otherRoom.id) {
                    // curStage is 1-based
                    if (Random.nextDouble() < PathProbs[curStage - 1]) {
                        paths[thisRoom.id].add(otherRoom)
                        paths[otherRoom.id].add(thisRoom)
                    } else {
                        Log.d("aaa", "No paths: ${thisRoom.id}, ${otherRoom.id}")
                    }
                }
            }
        }
    }

    init {
        doInit()
    }

    fun advance() {
        if (curStage == MaxStages) {
            Log.e("aaa", "reached maximum stage! cannot advance")
            return
        }

        curStage++
//        Toast.makeText(GameActivity(), "You come to the next stage: $curStage", Toast.LENGTH_SHORT).show()
        doInit()
    }
}
