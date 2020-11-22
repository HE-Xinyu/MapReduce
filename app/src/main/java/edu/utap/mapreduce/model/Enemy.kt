package edu.utap.mapreduce.model

import edu.utap.mapreduce.IAbleToFight

data class Enemy(
    override var hp: Int,
    var atk: Int,
    var def: Int,
    var spd: Int,
    var name: String,
) : IAbleToFight
// assume player(10, 5, 5, 5)
val testEnemy = Enemy(10, 10, 10, 10, "test enemy name")
val littleBear = Enemy(6, 3, 1, 5, "Little Bear")
val rotHound = Enemy(4, 4, 1, 5, "Rot Hound")
val poppingCandy = Enemy(2, 8, 1, 4, "Popping candy")
val raven = Enemy(6, 8, 2, 8, "Raven")
val tramp = Enemy(10, 5, 3, 5, "Tramp")
val Cerberus = Enemy(6, 6, 2, 8, "Cerberus")
val hardStone = Enemy(8, 7, 14, 3, "Hard Stone")
val hedgehog = Enemy(12, 12, 6, 6, "Hedgehog")

val tombstone = Enemy(20, 0, 0, 0, "Tombstone")
val berserker = Enemy(5, 18, 28, 20, "Berserker")
val motherBear = Enemy(30, 16, 5, 6, "Mother bear")
val wall = Enemy(35, 10, 8, 4, "Wall")

// with toxin
val Slime = Enemy(16, 7, 0, 6, "Slime")
val Naga = Enemy(30, 10, 8, 14, "Naga")

// If round>4, then nestling will grow up and become loombird
val nestling = Enemy(15, 5, 0, 30, "Nestling")
val loomBird = Enemy(32, 13, 2, 15, "Loombird")

val hugeHedgehog = Enemy(48, 14, 14, 9, "Huge Hedgehog")
val terrifiedElephant = Enemy(45, 16, 10, 8, "Terrified elephant")
val superSlime = Enemy(60, 17, 0, 4, "Super Slime")
val void = Enemy(52, 20, 9, 13, "Void")
val bigDevilArm = Enemy(80, 27, 0, 2, "Big devil's arm")
val Asura = Enemy(100, 15, 10, 1, "Asura")

val AllEnemies = listOf(
    testEnemy, littleBear, rotHound, poppingCandy, raven, tramp,
    Cerberus, tombstone, hardStone, hedgehog, motherBear, berserker,
    wall, Slime, Naga, nestling, loomBird, hugeHedgehog, superSlime,
    void, bigDevilArm, Asura
)
val easyEnemies = listOf(
    testEnemy, littleBear, rotHound, poppingCandy, raven, tramp,
    Cerberus, hardStone, hedgehog
)
val middleEnemies = listOf(tombstone, motherBear, berserker, wall, Slime, Naga, nestling)
val hardEnemies = listOf(
    loomBird,
    hugeHedgehog,
    terrifiedElephant,
    superSlime,
    void,
    bigDevilArm,
    Asura
)
