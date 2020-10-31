package edu.utap.mapreduce.model

import kotlin.math.abs

class Room(var x: Int, var y: Int) {
    /*
        Currently every room contains one enemy

        TODO: it should be a list if we decide to add more enemies in one room
     */
    private var enemy: Enemy = Enemy(10, 10, 10, 10)

    /*
        Whether the room is visited by the user.
        Currently if visited, then it means that the enemy is already defeated.


     */
    var visited = false

    /*
        Check if the room can reach the other one.
        The room can be reached from itself.
        Currently it will only check that the two rooms are adjacent.

        If we decide to add more movement mechanics to the game, we should change it as well.
     */
    fun canReach(other: Room): Boolean {
        return abs(x - other.x) + abs(y - other.y) <= 1
    }
}
