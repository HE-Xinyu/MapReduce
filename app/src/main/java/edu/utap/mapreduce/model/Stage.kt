package edu.utap.mapreduce.model

import android.util.Log
import kotlin.random.Random

class Stage(var curStage: Int) {
    // note: if we decide to change n as player progress, the rooms and paths need to be lateinit.
    private val pathProb = 1.0
    var rooms = emptyList<Room>().toMutableList()
    var paths = List(side_length * side_length) {
        emptySet<Room>().toMutableSet()
    }

    companion object {
        const val MaxStages = 3
        const val side_length: Int = 5
    }

    private fun doInit() {
        rooms.clear()
        paths = List(side_length * side_length) {
            emptySet<Room>().toMutableSet()
        }

        // 1. initialize rooms
        for (i in 0 until side_length) {
            for (j in 0 until side_length) {
                val kind = if (i == side_length - 1 && j == side_length - 1)
                    RoomKind.BOSS else RoomKind.NORMAL
                /*
                    The room id is simply the index of it in the stage.
                    If two rooms are merged into one, then they share the same id.
                 */
                rooms.add(Room(i, j, kind, i * side_length + j))
            }
        }

        // for testing
        rooms[1].kind = RoomKind.CHEST

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
