package edu.utap.mapreduce.model

import edu.utap.mapreduce.IAbleToFight

enum class EnemyKind {
    NORMAL,
    ELITE,
    BOSS,
}

data class Enemy(
    override var hp: Int,
    var atk: Int,
    var def: Int,
    var spd: Int,
    var name: String,
    var kind: EnemyKind = EnemyKind.NORMAL
) : IAbleToFight

val testEnemy = Enemy(10, 10, 10, 10, "test enemy name")

val AllEnemies = listOf(testEnemy)
