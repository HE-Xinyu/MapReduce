package edu.utap.mapreduce.model

import android.util.Log
import edu.utap.mapreduce.GameActivity
import kotlin.math.max

// maybe unnecessary here?
enum class BattleResult {
    WIN,
    LOSE,
}

class BattleSimulator {
    companion object {
        private const val MaxRound = 1000
        private val logger = GameActivity.logger

        /*
            Simulate 1 vs 1 battle
         */
        fun oneOnOne(player: Player, enemy: Enemy, stage: Stage): BattleResult {
            player.obtainedItems.sortBy {
                it.priority
            }

            player.obtainedItems.forEach {
                it.onStartBattle(player, enemy, stage)
            }

            enemyLevelUp(enemy, stage)

            var round = 1
            var result = BattleResult.LOSE
            while (round < MaxRound) {
                when (enemy) {
                    bigDevilArm -> if (round == 8) { speedUp(enemy) }
                    bigDevilTail -> toxin(player, round)
                    bigDevilHead -> weak(player, enemy)
                }
                round++
                val damageToEnemy = max(player.atk - enemy.def, 0)
                val damageToPlayer = max(enemy.atk - player.def, 0)
                if (player.spd >= enemy.spd) {
                    enemy.hp -= damageToEnemy
                    logger.log(" You caused $damageToEnemy damage to ${enemy.name}")
                    if (enemy.isDead()) {
                        result = BattleResult.WIN
                        logger.log(" You $result the battle")
                        break
                    }
                    player.hp -= damageToPlayer
                    logger.log("${enemy.name} caused $damageToPlayer damage to you")
                    if (player.isDead()) {
                        result = BattleResult.LOSE
                        logger.log(" You $result the battle")
                        break
                    }
                } else {
                    player.hp -= damageToPlayer
                    logger.log("${enemy.name} caused $damageToPlayer damage to you")
                    if (player.isDead()) {
                        result = BattleResult.LOSE
                        logger.log(" You $result the battle")
                        break
                    }
                    enemy.hp -= damageToEnemy
                    logger.log(" You caused $damageToEnemy damage to ${enemy.name}")
                    if (enemy.isDead()) {
                        result = BattleResult.WIN
                        logger.log(" You $result the battle")
                        break
                    }
                }
            }

            player.obtainedItems.forEach {
                it.onEndBattle(player, enemy, stage)
            }

            return result
        }

        private fun weak(player: Player, enemy: Enemy) {
            enemy.atk += (player.def * 0.2).toInt()
            logger.log("WEAK: ${enemy.name} have armor penetration")
        }

        private fun toxin(player: Player, round: Int) {
            if (round < 5) {
                player.hp -= 8
                logger.log(
                    "TOXIN: In the next ${5 - round} rounds, player will reduce 8 hp every time"
                )
            }
        }

        private fun speedUp(enemy: Enemy) {
            enemy.spd += (enemy.spd * 0.5).toInt()
            logger.log("SPEEDED-UP: The speed of ${enemy.name} has increased")
        }

        private fun enemyLevelUp(enemy: Enemy, stage: Stage) {
            when (stage.curStage) {
                2 ->
                    {
                        enemy.hp = (enemy.hp * 1.1).toInt()
                        enemy.atk = (enemy.atk * 1.1).toInt()
                        enemy.def = (enemy.def * 1.1).toInt()
                        enemy.spd = (enemy.spd * 1.1).toInt()
                        Log.d(
                            "ccccc",
                            enemy.name + enemy.hp.toString() +
                                enemy.atk.toString() + enemy.spd.toString()
                        )
                    }
                3 ->
                    {
                        enemy.hp = (enemy.hp * 1.2).toInt()
                        enemy.atk = (enemy.atk * 1.2).toInt()
                        enemy.def = (enemy.def * 1.2).toInt()
                        enemy.spd = (enemy.spd * 1.2).toInt()
                        Log.d(
                            "ccccc",
                            enemy.name + enemy.hp.toString() +
                                enemy.atk.toString() + enemy.spd.toString()
                        )
                    }
                else ->
                    {
                        enemy.hp = (enemy.hp).toInt()
                        enemy.atk = (enemy.atk).toInt()
                        enemy.def = (enemy.def).toInt()
                        enemy.spd = (enemy.spd).toInt()
                        Log.d(
                            "ccccc",
                            enemy.name + enemy.hp.toString() +
                                enemy.atk.toString() + enemy.spd.toString()
                        )
                    }
            }
        }
    }
}
