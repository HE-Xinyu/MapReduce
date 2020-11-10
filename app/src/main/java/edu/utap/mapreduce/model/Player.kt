package edu.utap.mapreduce.model

enum class PlayerStatus {
    WIN,
    LOSE,
    INTERACT_WITH_STAGE,
    INTERACT_WITH_ROOM,
    INTERACT_WITH_ITEM,
}

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

    var status = PlayerStatus.INTERACT_WITH_STAGE

    // TODO: need a field to indicate that the player is interacting in a room,
    //       and hence his/her items cannot be activated.

    // TODO: need a field to indicate the item that the player is activating,
    //      and hence the click behavior of a room may change.
}
