package edu.utap.mapreduce.model

val AllItems = setOf<Item>(Item1())

enum class ItemKind {
    ACTIVATED,
    PASSIVE,
}

abstract class Item(val name: String, val desc: String, val kind: ItemKind, val charge: Int = 0) {
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
