package me.uwuaden.kotlinplugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuideBookEvent: Listener {
    @EventHandler
    fun onGuideBookInvClick(e: InventoryClickEvent) {
        val clickedInv = e.clickedInventory ?: return
        val slot = e.slot
        if (e.view.title == "§e§lItem Guide Book") {
            e.isCancelled = true
            //if로 slot 찾아서 이벤트 알아서 추가.
        }
    }
}