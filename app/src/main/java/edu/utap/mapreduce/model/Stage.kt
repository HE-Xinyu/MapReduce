package edu.utap.mapreduce.model

import android.util.Log
import kotlin.random.Random

class Stage(var curStage: Int) {
    // note: if we decide to change n as player progress, the rooms and paths need to be lateinit.
    private val pathProb = 1.0
    var rooms = emptyList<Room>().toMutableList()
    var paths = List(SideLength * SideLength) {
        emptySet<Room>().toMutableSet()
    }

    companion object {
        const val MaxStages = 3
        const val SideLength: Int = 5
    }

    private fun doInit() {
        rooms.clear()
        paths = List(SideLength * SideLength) {
            emptySet<Room>().toMutableSet()
        }

        // 1. initialize rooms
        for (i in 0 until SideLength) {
            for (j in 0 until SideLength) {
                val kind = if (i == SideLength - 1 && j == SideLength - 1)
                    RoomKind.BOSS else RoomKind.NORMAL
                /*
                    The room id is simply the index of it in the stage.
                    If two rooms are merged into one, then they share the same id.
                 */
                rooms.add(Room(i, j, kind, i * SideLength + j))
            }
        }

        // for testing
        rooms[1].kind = RoomKind.CHEST
        rooms[2].kind = RoomKind.SHOP

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
                    if (Random.nextDouble() < pathProb) {
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
        doInit()
    }
}
