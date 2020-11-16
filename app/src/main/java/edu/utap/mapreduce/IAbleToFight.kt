package edu.utap.mapreduce

interface IAbleToFight {
    var hp: Int

    fun isDead(): Boolean {
        return hp < 0
    }
}
