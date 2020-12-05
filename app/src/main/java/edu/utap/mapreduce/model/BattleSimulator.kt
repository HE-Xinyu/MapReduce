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

            player.beginStatsBoost()
            while (round < MaxRound) {
                if (enemy.canPenetrate) {
                    penetrate(player, enemy, round)
                }
                if (enemy.isToxic) {
                    doToxic(player, round)
                }
                if (enemy.canSpeedUp && round == 8) {
                    speedUp(enemy)
                }
                round++
                val damageToEnemy = max(player.atk - enemy.def, 0)
                val damageToPlayer = max(enemy.atk - player.def, 0)
                if (player.spd >= enemy.spd) {
                    enemy.hp -= damageToEnemy
                    if (enemy.isDead()) {
                        result = BattleResult.WIN
                        logger.log(
                            "     Every round you caused $damageToEnemy damage to ${enemy.name}"
                        )
                        logger.log(
                            "     Every round ${enemy.name} caused $damageToPlayer damage to you"
                        )
                        logger.log("You $result the battle")
                        break
                    }
                    player.hp -= damageToPlayer

                    if (player.isDead()) {
                        result = BattleResult.LOSE
                        logger.log("You $result the battle")
                        break
                    }
                } else {
                    player.hp -= damageToPlayer

                    if (player.isDead()) {
                        result = BattleResult.LOSE
                        logger.log(
                            "     Every round ${enemy.name} caused $damageToPlayer damage to you"
                        )
                        logger.log(
                            "     Every round You caused $damageToEnemy damage to ${enemy.name}"
                        )
                        logger.log("You $result the battle")
                        break
                    }
                    enemy.hp -= damageToEnemy

                    if (enemy.isDead()) {
                        result = BattleResult.WIN
                        logger.log("You $result the battle")
                        break
                    }
                }
            }
            player.endStatsBoost()
            Log.d("aaa", logger.show().toString())

            player.obtainedItems.forEach {
                it.onEndBattle(player, enemy, stage)
            }

            return result
        }

        private fun penetrate(player: Player, enemy: Enemy, round: Int) {
            if (round < 2) {
                enemy.atk += (player.def * 0.2).toInt()
                logger.log("     WEAK: ${enemy.name} have armor penetration")
            }
        }

        private fun doToxic(player: Player, round: Int) {
            if (round < 4) {
                player.hp -= 4
                logger.log(
                    "     TOXIN: In the next ${4 - round} rounds, " +
                        "player will reduce 4 hp every round"
                )
            }
        }

        private fun speedUp(enemy: Enemy) {
            enemy.spd += (enemy.spd * 0.5).toInt()
            logger.log("     SPEEDED-UP: The speed of ${enemy.name} has increased")
        }

        private fun enemyLevelUp(enemy: Enemy, stage: Stage) {
            when (stage.curStage) {
                2 ->
                    {
                        enemy.hp = (enemy.hp * 1.1).toInt()
                        enemy.atk = (enemy.atk * 1.1).toInt()
                        enemy.def = (enemy.def * 1.1).toInt()
                        enemy.spd = (enemy.spd * 1.1).toInt()
                    }
                3 ->
                    {
                        enemy.hp = (enemy.hp * 1.2).toInt()
                        enemy.atk = (enemy.atk * 1.2).toInt()
                        enemy.def = (enemy.def * 1.2).toInt()
                        enemy.spd = (enemy.spd * 1.2).toInt()
                    }
                else ->
                    {
                        enemy.hp = (enemy.hp * 0.9).toInt()
                        enemy.atk = (enemy.atk * 0.9).toInt()
                        enemy.def = (enemy.def * 0.9).toInt()
                        enemy.spd = (enemy.spd * 0.9).toInt()
                    }
            }
        }
    }
}
