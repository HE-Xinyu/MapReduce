package edu.utap.mapreduce.model

import android.util.Log
import edu.utap.mapreduce.GameActivity
import kotlin.math.abs
import kotlin.random.Random

// expect more to come!
enum class RoomKind {
    NORMAL,
    BOSS,
    CHEST,
    SHOP,
}

class Room(var x: Int, var y: Int, var kind: RoomKind, var id: Int) {
    var enemies: MutableList<Enemy>? = null
    var isChallenge = false

    fun fillEnemies(player: Player) {
        // we need to pass player into the function because we need to read whether the player
        // has MapReduce, and also change the statsMultiplier.
        // NOTE: the function MUST BE idempotent!
        if (kind == RoomKind.CHEST || kind == RoomKind.SHOP) {
            Log.e("aaa", "you should not fill enemies in this room")
            return
        }

        if (enemies != null) {
            // already filled the enemies.
            return
        }

        val numEnemies = if (kind == RoomKind.BOSS) 1 else Random.nextInt(1, 4)
        enemies = emptyList<Enemy>().toMutableList()

        for (i in 0 until numEnemies) {
            enemies!!.add(sampleEnemy())
        }

        Log.d("aaa", "Original enemy size: ${enemies!!.size}")

        if (isChallenge) {
            Log.d("aaa", "Challenging room")
            enemies!!.add(sampleEnemy(EnemyKind.NORMAL))
        }

        if (player.hasMapReduce) {
            // do map reduce!
            // 1. set the stats multiplier of the player
            player.statsMultiplier = enemies!!.size

            // 2. decide enemy kind by choosing the 'maximum' one
            val kind = enemies!!.map { it.kind }.reduce { acc, enemyKind ->
                if (acc == EnemyKind.BOSS || enemyKind == EnemyKind.BOSS) EnemyKind.BOSS
                else if (acc == EnemyKind.ELITE || enemyKind == EnemyKind.ELITE) EnemyKind.ELITE
                else EnemyKind.NORMAL
            }

            // 3. sum the stats
            val totalHp = enemies!!.sumBy { it.hp }
            val totalAtk = enemies!!.sumBy { it.atk }
            val totalDef = enemies!!.sumBy { it.def }
            val totalSpd = enemies!!.sumBy { it.spd }

            // 4. replace the list with a new enemy
            val resultEnemy = Enemy(
                name = "Merged $kind",
                hp = totalHp,
                atk = totalAtk,
                def = totalDef,
                spd = totalSpd,
                kind = kind,
            )

            GameActivity.logger.log(
                "MapReduce complete! " +
                    "Number of enemies merged: ${enemies!!.size}, " +
                    "Result enemy: $resultEnemy"
            )
            this.enemies = listOf(resultEnemy).toMutableList()
        }
    }

    private fun sampleEnemy(enemyKind: EnemyKind? = null): Enemy {
        if (enemyKind != null) {
            return AllEnemies.filter {
                it.kind == enemyKind
            }.random().copy()
        }

        return when (kind) {
            RoomKind.NORMAL -> {
                AllEnemies.filter {
                    it.kind == EnemyKind.NORMAL || it.kind == EnemyKind.ELITE
                }.random().copy()
            }
            RoomKind.BOSS -> {
                AllEnemies.filter {
                    it.kind == EnemyKind.BOSS
                }.random().copy()
            }
            else -> {
                throw Exception("Try to sample enemy in non-battle rooms")
            }
        }
    }

    /*
        Whether the room is visited by the user.

        Actually `visited` is not the most accurate representation. If a room is visited, it really
        means that the player has finished the required interaction of it, e.g. defeat all the
        enemies, collect an item.
     */
    var visited = false

    fun isAdjacent(other: Room): Boolean {
        return abs(x - other.x) + abs(y - other.y) == 1
    }

    private fun getAdjacentRooms(stage: Stage): List<Room> {
        val dx = listOf(-1, 1, 0, 0)
        val dy = listOf(0, 0, -1, 1)

        val ret = emptyList<Room>().toMutableList()

        for (k in 0 until 4) {
            val nx = this.x + dx[k]
            val ny = this.y + dy[k]
            if (nx >= 0 && ny >= 0 && nx < Stage.SideLength && ny < Stage.SideLength) {
                ret.add(stage.rooms[nx * Stage.SideLength + ny])
            }
        }
        return ret
    }

    /*
        Check if the room can reach the other one.
        The room can be reached from itself.

        Run a BFS (Breadth First Search). We don't want to accidentally build a path when the
        player can instead bypass it by first going to other visited rooms.

        NOTE: If we decide to add more movement mechanics to the game, we should change it as well.
     */
    fun canReach(other: Room, stage: Stage): Pair<Boolean, Boolean> {
        if (this === other) {
            return Pair(true, false)
        }

        var ok = false
        var needPath = false

        // simulate a queue using a list
        val q = listOf(this).toMutableList()
        val enqueued = setOf(this).toMutableSet()

        while (q.isNotEmpty()) {
            val current = q.removeFirst()
            if (current === other) {
                ok = true
                needPath = false
                break
            }

            for (next in current.getAdjacentRooms(stage)) {
                if (enqueued.contains(next)) {
                    continue
                }

                if (stage.paths[current.id].contains(next) && (next.visited || next === other)) {
                    q.add(next)
                    enqueued.add(next)
                } else if (next === other) {
                    ok = true
                    needPath = true
                }
            }
        }
        return Pair(ok, needPath)
    }

    fun tryEnter(player: Player): Pair<Boolean, String> {
        return when (kind) {
            RoomKind.NORMAL, RoomKind.BOSS -> {
                Pair(true, "")
            }
            RoomKind.CHEST, RoomKind.SHOP -> {
                if (player.numKeys > 0) {
                    player.numKeys--
                    Pair(true, "You used a key.")
                } else {
                    Pair(false, "You don't have a key.")
                }
            }
        }
    }

    fun tryQuickAccess(player: Player): Pair<Boolean, String> {
        // NOTE: when the size of item pool is one, should we take the item for the player?
        return when (kind) {
            RoomKind.CHEST -> {
                if (AllItems.size == player.obtainedItems.size) {
                    Pair(false, "You have exhausted the item pool")
                } else {
                    Pair(true, "")
                }
            }
            else -> {
                Pair(true, "")
            }
        }
    }
}
