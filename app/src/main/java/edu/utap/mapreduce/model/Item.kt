package edu.utap.mapreduce.model

import java.lang.Exception
import kotlin.random.Random

val AllItems = setOf<Item>(Item1())

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
    val charge: Int = 0
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

    var curCharge = charge
    open fun onStartBattle(player: Player, enemy: Enemy, stage: Stage) {
    }

    fun canBeUsed(): Boolean {
        return kind == ItemKind.PASSIVE || curCharge == charge
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
