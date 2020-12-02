package edu.utap.mapreduce.model

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
            val originItems = player.obtainedItems
            player.obtainedItems.sortBy {
                it.priority
            }

            player.obtainedItems.forEach {
                it.onStartBattle(player, enemy, stage)
            }
            var round = 1
            var result = BattleResult.LOSE
            while (round < MaxRound) {
                when (enemy) {
                    bigDevilArm -> if (round == 2) { thief(player, enemy) }
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
                        player.obtainedItems = originItems
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
                        player.obtainedItems = originItems
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
                    "TOXIN: In the next ${4 - round} rounds, player will reduce 8 hp every time"
                )
            }
        }

        private fun thief(player: Player, enemy: Enemy) {
            player.obtainedItems.remove(player.obtainedItems[0])
            logger.log("THIEF: ${enemy.name} has stolen the ${player.obtainedItems[0]}")
        }
    }
}
