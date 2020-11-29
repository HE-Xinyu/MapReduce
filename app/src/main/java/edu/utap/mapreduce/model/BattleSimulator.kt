package edu.utap.mapreduce.model

import kotlin.math.max

// maybe unnecessary here?
enum class BattleResult {
    WIN,
    LOSE,
}

class BattleSimulator {
    companion object {
        private const val MaxRound = 1000

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
            var round = 1
            var result = BattleResult.LOSE
            while (round < MaxRound) {
                round++
                val damageToEnemy = max(player.atk - enemy.def, 0)
                val damageToPlayer = max(enemy.atk - player.def, 0)
                if (player.spd >= enemy.spd) {
                    enemy.hp -= damageToEnemy
                    if (enemy.isDead()) {
                        result = BattleResult.WIN
                        break
                    }
                    player.hp -= damageToPlayer
                    if (player.isDead()) {
                        result = BattleResult.LOSE
                        break
                    }
                } else {
                    player.hp -= damageToPlayer
                    if (player.isDead()) {
                        result = BattleResult.LOSE
                        break
                    }
                    enemy.hp -= damageToEnemy
                    if (enemy.isDead()) {
                        result = BattleResult.WIN
                        break
                    }
                }
            }

            player.obtainedItems.forEach {
                it.onEndBattle(player, enemy, stage)
            }

            return result
        }
    }
}
