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
    var kind: EnemyKind = EnemyKind.NORMAL,
    var canPenetrate: Boolean = false,
    var canSpeedUp: Boolean = false,
    var isToxic: Boolean = false,
) : IAbleToFight
// assume player(100, 50, 50, 50)
val elf = Enemy(10, 10, 10, 10, "Elf", EnemyKind.NORMAL)
val littleBear = Enemy(20, 51, 10, 25, "Little Bear", EnemyKind.NORMAL)
val rotHound = Enemy(40, 51, 10, 25, "Rot Hound", EnemyKind.NORMAL)
val poppingCandy = Enemy(20, 55, 10, 20, "Popping candy", EnemyKind.NORMAL)
val raven = Enemy(60, 53, 20, 40, "Raven", EnemyKind.NORMAL)
val tramp = Enemy(100, 50, 30, 50, "Tramp", EnemyKind.NORMAL)
val Cerberus = Enemy(60, 30, 20, 40, "Cerberus", EnemyKind.NORMAL)
val hardStone = Enemy(80, 30, 14, 30, "Hard Stone", EnemyKind.NORMAL)
val hedgehog = Enemy(60, 52, 40, 30, "Hedgehog", EnemyKind.NORMAL)
val tombstone = Enemy(200, 0, 10, 0, "Tombstone", EnemyKind.NORMAL, isToxic = true)
val void = Enemy(52, 20, 45, 50, "Void", EnemyKind.NORMAL)
val Slime = Enemy(115, 52, 0, 60, "Slime", EnemyKind.NORMAL)

val berserker = Enemy(50, 55, 30, 200, "Berserker", EnemyKind.ELITE)
val motherBear = Enemy(30, 57, 45, 55, "Mother bear", EnemyKind.ELITE)
val wall = Enemy(130, 54, 40, 40, "Wall", EnemyKind.ELITE)
val Naga = Enemy(90, 56, 40, 14, "Naga", EnemyKind.ELITE, isToxic = true)
val nestling = Enemy(150, 52, 0, 30, "Nestling", EnemyKind.ELITE)
val loomBird = Enemy(32, 90, 20, 15, "Loombird", EnemyKind.ELITE)
val hugeHedgehog = Enemy(48, 90, 14, 45, "Huge Hedgehog", EnemyKind.ELITE)
val terrifiedElephant = Enemy(200, 45, 10, 40, "Terrified elephant", EnemyKind.ELITE)
val superSlime = Enemy(600, 51, 0, 40, "Super Slime", EnemyKind.ELITE)
val Asura = Enemy(10, 110, 10, 10, "Asura", EnemyKind.ELITE)

val bigDevilArm = Enemy(200, 70, 35, 55, "Big devil's arm", EnemyKind.BOSS, canSpeedUp = true)
val bigDevilTail = Enemy(210, 63, 50, 46, "Big devil's tail", EnemyKind.BOSS, isToxic = true)
val bigDevilHead = Enemy(180, 65, 40, 40, "Big devil's head", EnemyKind.BOSS, canPenetrate = true)

val AllEnemies = listOf(
    elf, littleBear, rotHound, poppingCandy, raven, tramp,
    Cerberus, tombstone, hardStone, hedgehog, motherBear, berserker,
    wall, Slime, Naga, nestling, loomBird, hugeHedgehog, terrifiedElephant,
    superSlime, void, Asura,
    bigDevilArm, bigDevilTail, bigDevilHead
)
