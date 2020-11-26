package edu.utap.mapreduce.model

import android.util.Log
import kotlin.random.Random

class AwardSampler {
    companion object {
        private fun isLucky(): Boolean {
            return Random.nextDouble() > 0.9
        }

        private fun sampleSmall(): Award {
            val award = Award()
            return when (Random.nextInt(0, 10)) {
                in 0..3 -> {
                    award.addHp(5)
                }
                in 4..6 -> {
                    award.addCoins(5)
                }
                7 -> {
                    award.addKeys(1)
                }
                8 -> {
                    award.addPaths(1)
                }
                9 -> {
                    award.addChests(1)
                }
                else -> {
                    // IMPOSSIBLE
                    Log.d("aaa", "IMPOSSIBLE Award Sample Small")
                    award
                }
            }
        }

        private fun sampleMedium(): Award {
            val award = sampleSmall()
            return when (Random.nextInt(0, 3)) {
                0 -> {
                    award.addAtk(1)
                }
                1 -> {
                    award.addDef(1)
                }
                2 -> {
                    award.addSpd(1)
                }
                else -> {
                    // IMPOSSIBLE
                    Log.d("aaa", "IMPOSSIBLE Award Sample Medium")
                    award
                }
            }
        }

        private fun sampleLarge(player: Player): Award {
            val item = Item.fetchItem(player.obtainedItems)
            val award = sampleMedium()
            item?.let {
                return award.setItem(it)
            }

            // player has exhausted the item pool. give another medium award instead.
            return award.addAward(sampleMedium())
        }

        fun sample(player: Player, enemy: Enemy): Award {
            return when (enemy.kind) {
                EnemyKind.NORMAL -> {
                    if (isLucky()) {
                        sampleMedium()
                    } else {
                        sampleSmall()
                    }
                }
                EnemyKind.ELITE -> {
                    if (isLucky()) {
                        sampleLarge(player)
                    } else {
                        sampleMedium()
                    }
                }
                EnemyKind.BOSS -> {
                    sampleLarge(player)
                }
            }
        }

        fun sampleChest(player: Player): Award {
            return if (isLucky()) sampleLarge(player) else sampleMedium()
        }
    }
}
