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
// assume player(100, 50, 50, 50)
val testEnemy = Enemy(10, 10, 10, 10, "test enemy name")
val littleBear = Enemy(60, 30, 10, 50, "Little Bear")
val rotHound = Enemy(40, 40, 10, 50, "Rot Hound")
val poppingCandy = Enemy(20, 80, 10, 40, "Popping candy")
val raven = Enemy(60, 80, 20, 80, "Raven")
val tramp = Enemy(100, 50, 30, 50, "Tramp")
val Cerberus = Enemy(60, 60, 20, 80, "Cerberus")
val hardStone = Enemy(80, 70, 140, 30, "Hard Stone")
val hedgehog = Enemy(120, 120, 60, 60, "Hedgehog")

val tombstone = Enemy(200, 0, 0, 0, "Tombstone")
val berserker = Enemy(50, 180, 280, 200, "Berserker")
val motherBear = Enemy(300, 160, 50, 60, "Mother bear")
val wall = Enemy(350, 100, 80, 40, "Wall")

// with toxin
val Slime = Enemy(160, 70, 0, 60, "Slime")
val Naga = Enemy(300, 100, 80, 140, "Naga")

// If round>4, then nestling will grow up and become loombird
val nestling = Enemy(150, 50, 0, 300, "Nestling")
val loomBird = Enemy(320, 130, 20, 150, "Loombird")

val hugeHedgehog = Enemy(480, 140, 140, 90, "Huge Hedgehog")
val terrifiedElephant = Enemy(450, 160, 100, 80, "Terrified elephant")
val superSlime = Enemy(600, 170, 0, 40, "Super Slime")
val void = Enemy(520, 200, 90, 130, "Void")
val Asura = Enemy(1000, 150, 100, 10, "Asura")

val bigDevilArm = Enemy(2600, 270, 540, 20, "Big devil's arm")
val bigDevilTail = Enemy(2200, 600, 270, 460, "Big devil's tail")
val bigDevilHead = Enemy(1800, 480, 200, 300, "Big devil's head")

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
    Asura
)
val bossEnemies = listOf(bigDevilArm, bigDevilTail, bigDevilHead)
