package me.uwuaden.kotlinplugin.quickSlot

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*


private fun swapPlayerItem(player: Player, item: ItemStack) {
    var item1: Pair<Int, ItemStack>? = null
    val item2: Pair<Int, ItemStack> = Pair(player.inventory.heldItemSlot, player.inventory.itemInMainHand.clone())
    for (slot in 0 until 45) {
        val getItem = player.inventory.getItem(slot)
        if (getItem != null && getItem.itemMeta.displayName == item.itemMeta.displayName && getItem.type == item.type) {
            item1 = Pair(slot, getItem.clone())
        }
    }
    if (item1 != null) {
        player.inventory.setItem(item1.first, item2.second)
        player.inventory.setItemInMainHand(item1.second)
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F)
    }
}

class QuickSlotEvent: Listener {
    companion object {
        var playerQuickSlot = HashMap<UUID, PlayerQuickSlotData>()
    }

    @EventHandler
    fun onQuickSlotInvClick(e: InventoryClickEvent) {
        val clickedInventory: Inventory = e.clickedInventory ?: return
        val holder: InventoryHolder? = clickedInventory.holder
        if (holder !is QuickSlotInvHolder) return
        if (e.view.topInventory.holder is QuickSlotInvHolder) e.isCancelled = true
        if (e.clickedInventory == e.view.topInventory) {
            val player = e.view.player as Player
            e.isCancelled = true

            if (!(e.slot in 9..17 || e.slot in 27..35)) return

            var slotIndex = e.slot

            if (e.slot in 9..17) slotIndex -= 9
            else slotIndex -= 18

            val data = QuickSlotManager.initData(player.uniqueId)

            if (data.slotData.containsKey(slotIndex)) {
                swapPlayerItem(player, data.slotData[slotIndex]!!)
                player.inventory.close()
            } else {
                player.sendMessage("${ChatColor.RED}아이템이 지정되지 않았습니다.")
                player.sendMessage("${ChatColor.RED}/닭갈비 퀵슬롯 $slotIndex 명령어로 지정해주세요.")
            }
        }
    }

    @EventHandler
    fun onSwap(e: PlayerSwapHandItemsEvent) {
        if (e.player.isSneaking) {
            e.isCancelled = true
            e.player.openInventory(QuickSlotManager.inv(e.player))
        }
    }
}