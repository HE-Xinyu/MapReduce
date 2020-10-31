package edu.utap.mapreduce.model

class Player(
    hp: Int,
    atk: Int,
    def: Int,
    spd: Int,
) : CombatUnit(hp, atk, def, spd) {
    var roomIdx = -1
}
