package me.uwuaden.kotlinplugin.itemManager

import me.uwuaden.kotlinplugin.Main.Companion.currentInv
import me.uwuaden.kotlinplugin.Main.Companion.droppedItems
import me.uwuaden.kotlinplugin.Main.Companion.inventoryData
import me.uwuaden.kotlinplugin.Main.Companion.isOpening
import me.uwuaden.kotlinplugin.Main.Companion.plugin
import me.uwuaden.kotlinplugin.Main.Companion.scheduler
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*
import kotlin.math.sqrt

private fun distance(loc1: Location, loc2: Location): Double {
    val dx = loc2.x - loc1.x
    val dy = loc2.y - loc1.y
    val dz = loc2.z - loc1.z

    return sqrt(dx * dx + dy * dy + dz * dz)
}


class OpenItemEvent: Listener {



    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (!e.player.openInventory.topInventory.isEmpty) return
        if (e.player.gameMode == GameMode.SPECTATOR) return


        var droppedItemUid: UUID? = null

        scheduler.runTaskAsynchronously(plugin, Runnable {
            try {
                forEach@ for (i in 0..40) {
                    val playerHeadLoc = e.player.eyeLocation.clone()
                    val dir = playerHeadLoc.direction

                    val loc = playerHeadLoc.add(dir.multiply(0.1 * i))

                    for (droppedItem in droppedItems.filter {
                        it.loc.world == loc.world }.sortedBy { distance(it.loc, loc) }) {
                        if (distance(droppedItem.loc, loc) <= 0.5) {
                            droppedItemUid = droppedItem.uuid
                            break@forEach
                        }
                    }
                }

                scheduler.scheduleSyncDelayedTask(plugin, {
                    if (droppedItemUid != null) {
                        ItemManager.openDroppedItem(e.player, droppedItemUid!!)
                        e.isCancelled = true
                    }
                }, 1)
            } catch (e: Exception) {}
        })
    }

    @EventHandler
    fun close(e: InventoryCloseEvent) {
        if (!(ChatColor.stripColor(e.view.title)?: return).contains("⚠")) return
        ItemManager.updateInventory(e.view)



        val uuid = currentInv[e.view.player.uniqueId] ?: return
        val droppedItem = ItemManager.getDroppedItem(uuid) ?: return
        isOpening.remove(uuid)
        ItemManager.autoDelete(droppedItem)
        currentInv.remove(e.view.player.uniqueId)

        inventoryData.remove(e.player.uniqueId)
    }

}