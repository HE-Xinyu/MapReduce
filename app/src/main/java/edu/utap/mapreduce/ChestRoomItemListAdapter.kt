package edu.utap.mapreduce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.utap.mapreduce.model.GameViewModel
import edu.utap.mapreduce.model.Item
import edu.utap.mapreduce.model.Player
import edu.utap.mapreduce.model.PlayerStatus
import edu.utap.mapreduce.model.Stage

class ChestRoomItemListAdapter(
    var player: Player,
    var stage: Stage,
    var items: List<Item>,
    val model: GameViewModel
) : RecyclerView.Adapter<ChestRoomItemListAdapter.VH>() {
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.itemNameV)

        fun bind(item: Item) {
            nameView.text = item.name

            itemView.setOnLongClickListener {
                Toast.makeText(it.context, item.desc, Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener {
                player.obtainedItems.add(item)
                player.status = PlayerStatus.INTERACT_WITH_STAGE
                model.setPlayer(player)
                model.setStage(stage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}