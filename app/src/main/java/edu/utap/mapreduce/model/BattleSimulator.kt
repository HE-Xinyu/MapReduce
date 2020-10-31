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
        fun oneOnOne(player: Player, enemy: Enemy): BattleResult {
            var round = 1
            while (round < MaxRound) {
                round++
                val damageToEnemy = max(player.atk - enemy.def, 0)
                val damageToPlayer = max(enemy.atk - player.def, 0)
                if (player.spd >= enemy.spd) {
                    enemy.hp -= damageToEnemy
                    if (enemy.isDead()) {
                        return BattleResult.WIN
                    }
                    player.hp -= damageToPlayer
                    if (player.isDead()) {
                        return BattleResult.LOSE
                    }
                } else {
                    player.hp -= damageToPlayer
                    if (player.isDead()) {
                        return BattleResult.LOSE
                    }
                    enemy.hp -= damageToEnemy
                    if (enemy.isDead()) {
                        return BattleResult.WIN
                    }
                }
            }
            return BattleResult.LOSE
        }
    }
}
