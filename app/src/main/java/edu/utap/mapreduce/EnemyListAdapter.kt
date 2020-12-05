package edu.utap.mapreduce

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.mapreduce.model.AwardSampler
import edu.utap.mapreduce.model.BattleResult
import edu.utap.mapreduce.model.BattleSimulator
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.PlayerStatus
import edu.utap.mapreduce.model.Room
import edu.utap.mapreduce.model.RoomKind
import edu.utap.mapreduce.model.Stage

class EnemyListAdapter(
    var player: Player,
    var room: Room,
    var stage: Stage,
    var model: GameViewModel
) : RecyclerView.Adapter<EnemyListAdapter.VH>() {
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.enemyNameV)
        private val logger = GameActivity.logger

        fun bind(pos: Int) {
            val enemy = room.enemies!![pos]
            nameView.text = enemy.name
            nameView.setTextColor(Color.parseColor("#f8f3d4"))
            nameView.textSize = 20F
            itemView.setOnClickListener {
                /*
                    battle happens here.
                 */

                // TODO: design a reward system for beating enemies
                val enemyPos = room.enemies!!.indexOf(enemy)
                val result = BattleSimulator.oneOnOne(player, enemy, stage)

                if (result == BattleResult.WIN) {
                    val award = AwardSampler.sample(player, enemy)
                    logger.log("AWARD: You get the $award")
                    Log.d("aaa", award.toString())
                    award.applyTo(player, stage)

                    room.enemies!!.remove(enemy)
                    if (stage.curStage == Stage.MaxStages && room.kind == RoomKind.BOSS) {
                        player.status = PlayerStatus.WIN
                    } else if (room.enemies!!.size == 0) {
                        // the room is cleared!
                        player.status = PlayerStatus.INTERACT_WITH_STAGE
                        if (room.kind == RoomKind.BOSS) {
                            stage.advance()
                            logger.log("You come to the next stage: ${stage.curStage}")
                            player.initPosition(stage)
                        }
                        model.setStage(stage)
                    }
                    this@EnemyListAdapter.notifyItemRemoved(enemyPos)
                } else {
                    player.status = PlayerStatus.LOSE
                }
                model.setPlayer(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.enemy_row, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        room.enemies?.let {
            holder.bind(position)
        }
    }

    override fun getItemCount(): Int {
        return room.enemies?.size ?: 0
    }
}
