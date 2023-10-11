package me.uwuaden.kotlinplugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class SpecEvent: Listener {
    @EventHandler
    fun specInvClick(e: InventoryClickEvent) {
        if (e.view.title.contains("(viewer)")) {
            e.isCancelled = true
        }
    }
}