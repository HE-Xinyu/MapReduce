package edu.utap.mapreduce.model

class Stage(var curStage: Int) {
    var rooms = emptyList<Room>().toMutableList()
    private val n: Int = 5

    init {
        for (i in 0 until n) {
            for (j in 0 until n) {
                val kind = if (i == n - 1 && j == n - 1) RoomKind.BOSS else RoomKind.NORMAL
                rooms.add(Room(i, j, kind))
            }
        }
    }
}
