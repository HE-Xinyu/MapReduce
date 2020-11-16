package edu.utap.mapreduce.model

import java.lang.Exception
import kotlin.math.min
import kotlin.random.Random

val AllItems = setOf(Item1(), Item2())

enum class ItemKind {
    ACTIVATED,
    PASSIVE,
}

enum class RareLevel(val weight: Int) {
    NORMAL(100),
    SPECIAL(20),
    VERY_SPECIAL(5),
    EXTREME(2),
}

abstract class Item(
    val name: String,
    val desc: String,
    val kind: ItemKind,
    val level: RareLevel = RareLevel.NORMAL,
    val recharge: Int = 0
) {
    companion object {
        fun fetchItem(excludedItems: List<Item>): Item? {
            if (excludedItems.size == AllItems.size) {
                return null
            }

            val itemPool = emptyList<Item>().toMutableList()
            var sumOfWeight = 0
            for (item in AllItems) {
                if (!excludedItems.contains(item)) {
                    itemPool.add(item)
                    sumOfWeight += item.level.weight
                }
            }

            val randWeight = Random.nextInt(1, sumOfWeight)
            var cumulatedWeight = 0
            for (item in itemPool) {
                cumulatedWeight += item.level.weight
                if (cumulatedWeight >= randWeight) {
                    return item
                }
            }
            throw Exception("The item selection algorithm is broken")
        }
    }

    var curRecharge = recharge

    // START OF the functions to override in each items

    // called on every start of the battle
    open fun onStartBattle(player: Player, enemy: Enemy, stage: Stage) {}

    // called on every activation of the item.
    // only effective for the activated items.
    open fun doActivate(player: Player, stage: Stage) {}

    // END OF the functions to override in each items

    fun canBeActivated(): Boolean {
        return kind == ItemKind.PASSIVE || curRecharge == recharge
    }

    fun displayRecharge(): String {
        return if (kind == ItemKind.PASSIVE) "PASSIVE" else "$curRecharge/$recharge"
    }

    fun doRecharge() {
        if (kind == ItemKind.ACTIVATED) {
            curRecharge = min(recharge, curRecharge + 1)
        }
    }

    fun onActivate(player: Player, stage: Stage): String {
        if (!canBeActivated()) {
            return "Cannot activate this item! " +
                "The item must be fully recharged and its type must be activated"
        }

        if (player.status != PlayerStatus.INTERACT_WITH_STAGE) {
            return "Player can only activate items when interacting with the stage"
        }

        curRecharge = 0
        doActivate(player, stage)
        return "Success!"
    }

    /*
        The hash of an item is simply the hash of its name.
        It implies that the name should be unique.
     */
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (name != other.name) return false

        return true
    }
}

class Item1 : Item("test item", "test desc", ItemKind.PASSIVE) {
    override fun onStartBattle(player: Player, enemy: Enemy, stage: Stage) {
        super.onStartBattle(player, enemy, stage)
        player.hp += 5
    }
}

class Item2 : Item(
    "test activated item",
    "test activated item desc",
    ItemKind.ACTIVATED,
    RareLevel.NORMAL,
    4
) {
    override fun doActivate(player: Player, stage: Stage) {
        super.doActivate(player, stage)
        player.spd += 5
    }
}
