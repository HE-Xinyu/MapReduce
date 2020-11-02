package edu.utap.mapreduce.model

import kotlin.random.Random

class Stage(var curStage: Int) {
    // note: if we decide to change n as player progress, the rooms and paths need to be lateinit.
    private val n: Int = 5
    private val pathProb = 0.9
    var rooms = emptyList<Room>().toMutableList()
    var paths = List(n * n) {
        emptySet<Room>().toMutableSet()
    }

    companion object {
        const val MaxStages = 3
    }

    init {
        // 1. initialize rooms
        for (i in 0 until n) {
            for (j in 0 until n) {
                val kind = if (i == n - 1 && j == n - 1) RoomKind.BOSS else RoomKind.NORMAL
                /*
                    The room id is simply the index of it in the stage.
                    If two rooms are merged into one, then they share the same id.
                 */
                rooms.add(Room(i, j, kind, i * n + j))
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
                if (thisRoom.isAdjacent(otherRoom)) {
                    if (Random.nextDouble() < pathProb) {
                        paths[thisRoom.id].add(otherRoom)
                        paths[otherRoom.id].add(thisRoom)
                    }
                }
            }
        }
    }
}
