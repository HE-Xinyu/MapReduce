package edu.utap.mapreduce.model

import edu.utap.mapreduce.IAbleToFight

enum class PlayerStatus {
    WIN,
    LOSE,
    INTERACT_WITH_STAGE,
    INTERACT_WITH_ROOM,
    INTERACT_WITH_ITEM,
}

class Player(
    override var hp: Int,
    var atk: Int,
    var def: Int,
    var spd: Int,
) : IAbleToFight {
    var roomIdx = -1
    /*
        The items owned by the player.
     */
    var obtainedItems = emptyList<Item>().toMutableList()

    var numKeys = 0
    var numPaths = 0
    var numCoins = 0
    var numChests = 0

    var status = PlayerStatus.INTERACT_WITH_STAGE

    // TODO: need a field to indicate that the player is interacting in a room,
    //       and hence his/her items cannot be activated.

    // TODO: need a field to indicate the item that the player is activating,
    //      and hence the click behavior of a room may change.
}
