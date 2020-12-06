package edu.utap.mapreduce

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Item
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.RareLevel
import edu.utap.mapreduce.model.Stage

class ObtainedItemListAdapter(var player: Player, var stage: Stage, val model: GameViewModel) :
    RecyclerView.Adapter<ObtainedItemListAdapter.VH>() {
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.itemNameV)
        private val rechargeView = itemView.findViewById<TextView>(R.id.itemRechargeV)

        fun bind(item: Item) {
            nameView.text = item.name
            when (item.level) {
                RareLevel.NORMAL -> nameView.setTextColor(Color.parseColor("#808080"))
                RareLevel.SPECIAL -> nameView.setTextColor(Color.parseColor("#00b8a9"))
                RareLevel.VERY_SPECIAL -> nameView.setTextColor(Color.parseColor("#da9ff9"))
                RareLevel.LEGENDARY -> nameView.setTextColor(Color.parseColor("#ffda77"))
                else -> {}
            }
            rechargeView.text = item.displayRecharge()

            itemView.setOnLongClickListener {
                Toast.makeText(it.context, item.desc, Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener {
                val msg = item.onActivate(player, stage)
                if (msg.isNotEmpty()) {
                    Toast.makeText(it.context, msg, Toast.LENGTH_SHORT).show()
                }
                model.setPlayer(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(player.obtainedItems[position])
    }

    override fun getItemCount(): Int {
        return player.obtainedItems.size
    }
}
