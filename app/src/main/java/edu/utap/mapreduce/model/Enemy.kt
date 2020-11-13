package edu.utap.mapreduce.model

import edu.utap.mapreduce.IAbleToFight

data class Enemy(
    override var hp: Int,
    var atk: Int,
    var def: Int,
    var spd: Int,
    var name: String,
) : IAbleToFight

val testEnemy = Enemy(10, 10, 10, 10, "test enemy name")

val AllEnemies = listOf(testEnemy)
