package edu.utap.mapreduce.model

import kotlin.random.Random

enum class ShopItemKind {
    HP,
    PATHS,
    KEYS,
    ITEM,
}

class ShopItem(var kind: ShopItemKind, var amountOrItem: Any) {
    companion object {
        const val HpPrice = 1
        const val PathPrice = 10
        const val KeyPrice = 5
        val ItemPrice = mapOf(
            RareLevel.NORMAL to 20,
            RareLevel.SPECIAL to 40,
            RareLevel.VERY_SPECIAL to 80,
            RareLevel.LEGENDARY to 100
        )

        /*
            Sample a list of shop items.

            The length of shop items is passed in 'n', and the player is to determine remaining
            activated/passive items.

            In each shop, there is at most one activated/passive item for player to buy.
            If the player has exhausted the item pool, then there will be only basic stuff for
            buying.
         */
        fun sample(n: Int, player: Player): List<ShopItem> {
            val ret = emptyList<ShopItem>().toMutableList()

            var buyableItem = Item.fetchItem(player.obtainedItems)

            for (i in 0 until n) {
                val kind = ShopItemKind.values().filterNot {
                    it == ShopItemKind.ITEM && buyableItem == null
                }.random()
                val shopItem = when (kind) {
                    ShopItemKind.HP -> {
                        ShopItem(kind, Random.nextInt(5, 11))
                    }
                    ShopItemKind.PATHS -> {
                        ShopItem(kind, Random.nextInt(1, 3))
                    }
                    ShopItemKind.KEYS -> {
                        ShopItem(kind, Random.nextInt(1, 3))
                    }
                    ShopItemKind.ITEM -> {
                        ShopItem(kind, buyableItem!!)
                    }
                }
                ret.add(shopItem)
                if (kind == ShopItemKind.ITEM) {
                    buyableItem = null
                }
            }

            return ret
        }
    }
    fun showName(): String {
        return when (kind) {
            ShopItemKind.ITEM -> {
                (amountOrItem as Item).name
            }
            else -> {
                "$kind $amountOrItem"
            }
        }
    }

    fun getPrice(): Int {
        return when (kind) {
            ShopItemKind.ITEM -> {
                ItemPrice[(amountOrItem as Item).level] ?: error("Rare level not found")
            }
            ShopItemKind.HP -> {
                HpPrice * (amountOrItem as Int)
            }
            ShopItemKind.KEYS -> {
                KeyPrice * (amountOrItem as Int)
            }
            ShopItemKind.PATHS -> {
                PathPrice * (amountOrItem as Int)
            }
        }
    }
}
