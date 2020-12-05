package edu.utap.mapreduce.model

import java.lang.Exception
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random

val AllItems = setOf(
    Protector(),
    HealthRecover(),
    SwordRepair(),
    ShieldRepair(),
    ShoesRepair(),
    BronzeShield(),
    BronzeSword(),
    SilverShield(),
    SilverSword(),
    GoldenShield(),
    GoldenSword(),
    StrawShoes(),
    RunningShoes(),
    BossRoomCompass(),
    ChestRoomCompass(),
    GreatCompass(),
    ShopCompass(),
)

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
    val recharge: Int = 0,
    // item with lower priority will activate before a higher one
    val priority: Int = 100,
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

        fun fetchItems(excludedItems: List<Item>, n: Int): List<Item>? {
            if (excludedItems.size == AllItems.size) {
                return null
            }

            val n = min(n, AllItems.size - excludedItems.size)

            val ret = emptyList<Item>().toMutableList()
            val excludedItems = excludedItems.toMutableList()

            for (i in 0 until n) {
                val item = fetchItem(excludedItems)!!
                ret.add(item)
                excludedItems.add(item)
            }
            return ret
        }
    }

    var curRecharge = recharge

    // START OF the functions to override in each item

    // called on every start of battles
    open fun onStartBattle(player: Player, enemy: Enemy, stage: Stage) {}

    // called on every end of battles
    open fun onEndBattle(player: Player, enemy: Enemy, stage: Stage) {}

    // called when the item is obtained by the player (i.e. only ONCE)
    open fun onObtained(player: Player, stage: Stage) {}

    // called on every activation of the item.
    // only effective for the activated items.
    open fun doActivate(player: Player, stage: Stage): String { return "" }

    open fun doRoomSelected(player: Player, stage: Stage, roomIdx: Int): String { return "" }

    // END OF the functions to override in each item

    fun canBeActivated(): Boolean {
        return kind == ItemKind.ACTIVATED && curRecharge == recharge
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
        player.currentActivatedItem = this

        return doActivate(player, stage)
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

class Protector :
    Item("Protector", "Block the first 10 HP damage of each battle", ItemKind.PASSIVE) {
    private var hpBeforeStart by Delegates.notNull<Int>()
    override fun onStartBattle(player: Player, enemy: Enemy, stage: Stage) {
        super.onStartBattle(player, enemy, stage)
        hpBeforeStart = player.hp
        player.hp += 10
    }

    override fun onEndBattle(player: Player, enemy: Enemy, stage: Stage) {
        player.hp = min(hpBeforeStart, player.hp)
    }
}

class HealthRecover : Item(
    "Health Recover",
    "Gain 5 HP at the end of each battle",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL,
    0,
    200
) {
    override fun onEndBattle(player: Player, enemy: Enemy, stage: Stage) {
        player.hp += 5
    }
}

class SwordRepair : Item(
    "Sword Repair",
    "Gain 2 ATK at the end of each battle",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onEndBattle(player: Player, enemy: Enemy, stage: Stage) {
        player.atk += 2
    }
}

class ShieldRepair : Item(
    "Shield Repair",
    "Gain 2 DEF at the end of each battle",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onEndBattle(player: Player, enemy: Enemy, stage: Stage) {
        player.def += 2
    }
}

class ShoesRepair : Item(
    "Shoes Repair",
    "Gain 2 SPD at the end of each battle",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onEndBattle(player: Player, enemy: Enemy, stage: Stage) {
        player.spd += 2
    }
}

class BronzeSword : Item("Bronze Sword", "Gain 5 ATK when obtained this item", ItemKind.PASSIVE) {
    override fun onObtained(player: Player, stage: Stage) {
        player.atk += 5
    }
}

class SilverSword : Item(
    "Silver Sword",
    "Gain 10 ATK when obtained this item",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.atk += 10
    }
}

class GoldenSword : Item(
    "Golden Sword",
    "Gain 20 ATK when obtained this item",
    ItemKind.PASSIVE,
    RareLevel.VERY_SPECIAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.atk += 20
    }
}

class BronzeShield : Item("Bronze Shield", "Gain 5 DEF when obtained this item", ItemKind.PASSIVE) {
    override fun onObtained(player: Player, stage: Stage) {
        player.def += 5
    }
}

class SilverShield : Item(
    "Silver Shield",
    "Gain 10 DEF when obtained this item",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.def += 10
    }
}

class GoldenShield : Item(
    "Golden Shield",
    "Gain 20 DEF when obtained this item",
    ItemKind.PASSIVE,
    RareLevel.VERY_SPECIAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.def += 20
    }
}

class StrawShoes : Item("Straw Shoes", "Gain 5 SPD when obtained this item", ItemKind.PASSIVE) {
    override fun onObtained(player: Player, stage: Stage) {
        player.spd += 5
    }
}

class RunningShoes : Item(
    "Running Shoes",
    "Gain 10 SPD when obtained this item",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.spd += 10
    }
}

class ChestRoomCompass : Item(
    "Chest Room Compass",
    "Review the position of chest rooms",
    ItemKind.PASSIVE,
    RareLevel.NORMAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.canSeeChestRoom = true
    }
}

class ShopCompass : Item(
    "Shop Compass",
    "Review the position of shops",
    ItemKind.PASSIVE,
    RareLevel.NORMAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.canSeeShop = true
    }
}

class BossRoomCompass : Item(
    "Boss Room Compass",
    "Review the position of boss room",
    ItemKind.PASSIVE,
    RareLevel.NORMAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.canSeeBossRoom = true
    }
}

class GreatCompass : Item(
    "Great Compass",
    "Reveal the position of boss room, shops and chest rooms",
    ItemKind.PASSIVE,
    RareLevel.SPECIAL
) {
    override fun onObtained(player: Player, stage: Stage) {
        player.canSeeBossRoom = true
        player.canSeeShop = true
        player.canSeeChestRoom = true
    }
}

class ChestFanatic : Item(
    "Chest Fanatic",
    "Convert a room to a chest room",
    ItemKind.ACTIVATED,
    RareLevel.VERY_SPECIAL,
    6
) {
    override fun doActivate(player: Player, stage: Stage): String {
        player.status = PlayerStatus.INTERACT_WITH_ITEM

        return "Please select a room to convert (it cannot be a boss room)"
    }

    override fun doRoomSelected(player: Player, stage: Stage, roomIdx: Int): String {
        val room = stage.rooms[roomIdx]
        if (room.kind == RoomKind.BOSS) {
            return "You cannot convert a boss room to a chest room"
        }

        player.status = PlayerStatus.INTERACT_WITH_STAGE
        player.currentActivatedItem = null
        room.kind = RoomKind.CHEST
        return "Success!"
    }
}
