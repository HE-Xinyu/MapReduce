package edu.utap.mapreduce.model

import kotlin.math.abs

// expect more to come!
enum class RoomKind {
    NORMAL,
    BOSS,
    CHEST,
}

class Room(var x: Int, var y: Int, var kind: RoomKind, var id: Int) {
    /*
        Currently every room contains one enemy

        TODO: it should be a list if we decide to add more enemies in one room
     */
    var enemies: MutableList<Enemy>? = listOf(Enemy(10, 10, 10, 10)).toMutableList()

    /*
        Whether the room is visited by the user.

        Actually `visited` is not the most accurate representation. If a room is visited, it really
        means that the player has finished the required interaction of it, e.g. defeat all the
        enemies, collect an item.
     */
    var visited = false

    fun isAdjacent(other: Room): Boolean {
        return abs(x - other.x) + abs(y - other.y) == 1
    }

    /*
        Check if the room can reach the other one.
        The room can be reached from itself.
        Currently there are two cases when the room is reachable:
        1. player has already visited it
        2. there is a path connecting them

        NOTE: If we decide to add more movement mechanics to the game, we should change it as well.
     */
    fun canReach(other: Room, stage: Stage): Boolean {
        return other.visited || stage.paths[id].contains(other)
    }
}
