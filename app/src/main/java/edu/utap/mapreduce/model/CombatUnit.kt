package edu.utap.mapreduce.model

abstract class CombatUnit(
    var hp: Int,
    var atk: Int,
    var def: Int,
    var spd: Int,
) {
    fun isDead(): Boolean {
        return hp <= 0
    }
}
