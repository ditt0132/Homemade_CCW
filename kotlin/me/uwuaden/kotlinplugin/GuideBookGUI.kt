package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.itemManager.ItemManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object GuideBookGUI {
    fun openGuideBook(player: Player) {
        val inventory = Bukkit.createInventory(null, 54, "§e§lItem Guide Book")
        inventory.setItem(0, ItemManager.createNamedItem(Material.RED_STAINED_GLASS_PANE, 1, " ", null))
        player.openInventory(inventory)
    }
}