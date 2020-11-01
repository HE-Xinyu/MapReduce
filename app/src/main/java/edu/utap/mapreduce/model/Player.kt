package edu.utap.mapreduce.model

class Player(
    hp: Int,
    atk: Int,
    def: Int,
    spd: Int,
) : CombatUnit(hp, atk, def, spd) {
    var roomIdx = -1
    /*
        The items owned by the player.
     */
    var obtainedItems = emptyList<Item>().toMutableList()

    var numKeys = 99
    var numPaths = 99
    var numCoins = 99
    var numChests = 99
}
