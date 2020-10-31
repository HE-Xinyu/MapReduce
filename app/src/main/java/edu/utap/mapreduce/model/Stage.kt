package edu.utap.mapreduce.model

class Stage(private var curStage: Int) {
    var rooms = emptyList<Room>().toMutableList()
    private val n: Int = 5

    init {
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                rooms.add(Room(i, j))
            }
        }
    }
}
