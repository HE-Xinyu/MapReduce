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
import edu.utap.mapreduce.model.PlayerStatus
import edu.utap.mapreduce.model.RareLevel
import edu.utap.mapreduce.model.ShopItem
import edu.utap.mapreduce.model.ShopItemKind
import edu.utap.mapreduce.model.Stage

class ShopItemListAdapter(
    var player: Player,
    var stage: Stage,
    val model: GameViewModel,
    var shopItems: MutableList<ShopItem>
) :
    RecyclerView.Adapter<ShopItemListAdapter.VH>() {
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView = itemView.findViewById<TextView>(R.id.shopItemNameV)
        private val priceView = itemView.findViewById<TextView>(R.id.shopItemPriceV)

        fun bind(pos: Int) {
            val shopItem = shopItems[pos]
            nameView.text = shopItem.showName()
            if (shopItem.kind == ShopItemKind.ITEM) {
                when ((shopItem.amountOrItem as Item).level) {
                    RareLevel.SPECIAL -> nameView.setTextColor(Color.parseColor("#00b8a9"))
                    RareLevel.VERY_SPECIAL -> nameView.setTextColor(Color.parseColor("#da9ff9"))
                    RareLevel.LEGENDARY -> nameView.setTextColor(Color.parseColor("#ffda77"))
                    else -> nameView.setTextColor(Color.parseColor("#f8f3d4"))
                }
            } else { nameView.setTextColor(Color.parseColor("#f8f3d4")) }
//            when(Item){
//                RareLevel.SPECIAL -> nameView.setTextColor(Color.parseColor("#00b8a9"))
//                RareLevel.VERY_SPECIAL -> nameView.setTextColor(Color.parseColor("#da9ff9"))
//                RareLevel.LEGENDARY -> nameView.setTextColor(Color.parseColor("#ffda77"))
//                else -> nameView.setTextColor(Color.parseColor("#f8f3d4"))
//            }
//            nameView.setTextColor(Color.parseColor("#f8f3d4"))
            nameView.textSize = 20F
            priceView.text = shopItem.getPrice().toString()
            priceView.setTextColor(Color.parseColor("#f8f3d4"))
            priceView.textSize = 20F

            itemView.setOnClickListener {
                val price = shopItem.getPrice()
                if (player.numCoins >= price) {
                    player.numCoins -= price
                    when (shopItem.kind) {
                        ShopItemKind.ITEM -> {
                            val item = shopItem.amountOrItem as Item
                            player.obtainedItems.add(item)
                            item.onObtained(player, stage)
                        }
                        ShopItemKind.KEYS -> {
                            player.numKeys += shopItem.amountOrItem as Int
                        }
                        ShopItemKind.PATHS -> {
                            player.numPaths += shopItem.amountOrItem as Int
                        }
                        ShopItemKind.HP -> {
                            player.hp += shopItem.amountOrItem as Int
                        }
                    }
                    val correctPos = shopItems.indexOf(shopItem)
                    shopItems.remove(shopItem)
                    this@ShopItemListAdapter.notifyItemRemoved(correctPos)
                    if (shopItems.isEmpty()) {
                        // player bought everything in the shop.
                        player.status = PlayerStatus.INTERACT_WITH_STAGE
                    }

                    model.setPlayer(player)
                    model.setStage(stage)
                } else {
                    Toast.makeText(
                        it.context,
                        "You don't have enough coins",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.shop_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return shopItems.size
    }
}
