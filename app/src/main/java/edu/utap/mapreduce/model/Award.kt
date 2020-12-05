package edu.utap.mapreduce.model

import android.util.Log

class Award {
    private var _item: Item? = null
    private var _hp = 0
    private var _atk = 0
    private var _def = 0
    private var _spd = 0
    private var _keys = 0
    private var _paths = 0
    private var _coins = 0
    private var _chests = 0

    fun setItem(item: Item): Award {
        _item = item
        return this
    }

    // NOTE: can probably save some code using reflection.
    fun addHp(hp: Int): Award {
        _hp += hp
        return this
    }

    fun addAtk(atk: Int): Award {
        _atk += atk
        return this
    }

    fun addDef(def: Int): Award {
        _def += def
        return this
    }

    fun addSpd(spd: Int): Award {
        _spd += spd
        return this
    }

    fun addKeys(keys: Int): Award {
        _keys += keys
        return this
    }

    fun addPaths(paths: Int): Award {
        _paths += paths
        return this
    }

    fun addCoins(coins: Int): Award {
        _coins += coins
        return this
    }

    fun addChests(chests: Int): Award {
        _chests += chests
        return this
    }

    fun addAward(other: Award): Award {
        // override current item if applicable
        other._item?.let {
            _item = it
        }

        _hp += other._hp
        _atk += other._atk
        _def += other._def
        _spd += other._spd
        _keys += other._keys
        _paths += other._paths
        _coins += other._coins
        _chests += other._chests

        return this
    }

    fun applyTo(player: Player, stage: Stage) {
        _item?.let {
            if (player.obtainedItems.contains(it)) {
                Log.e("aaa", "Player has already obtained this item!")
            } else {
                player.obtainedItems.add(it)
                it.onObtained(player, stage)
            }
        }

        player.hp += _hp
        player.atk += _atk
        player.def += _def
        player.spd += _spd
        player.numKeys += _keys
        player.numPaths += _paths
        player.numCoins += _coins
        player.numChests += _chests
    }

    override fun toString(): String {
        val awardsList = emptyList<String>().toMutableList()
        _item?.let {
            awardsList.add("item: ${it.name}")
        }

        if (_hp > 0) {
            awardsList.add("hp: $_hp")
        }
        if (_atk > 0) {
            awardsList.add("atk: $_atk")
        }
        if (_def > 0) {
            awardsList.add("def: $_def")
        }
        if (_spd > 0) {
            awardsList.add("spd: $_spd")
        }
        if (_keys > 0) {
            awardsList.add("keys: $_keys")
        }
        if (_paths > 0) {
            awardsList.add("paths: $_paths")
        }
        if (_chests > 0) {
            awardsList.add("chests: $_chests")
        }
        if (_coins > 0) {
            awardsList.add("coins: $_coins")
        }
        return awardsList.joinToString()
    }
}
