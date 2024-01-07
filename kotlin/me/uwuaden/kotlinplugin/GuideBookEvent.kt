package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.itemManager.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuideBookEvent: Listener {
    @EventHandler
    fun onGuideBookInvClick(e: InventoryClickEvent) {
        val inventory = Bukkit.createInventory(null, 54, "§e§lItem Guide Book")
        val clickedInv = e.clickedInventory ?: return
        val slot = e.slot
        if (e.view.title == "§e§lItem Guide Book") {
            e.isCancelled = true
            //if로 slot 찾아서 이벤트 알아서 추가.
        }

        if (e.isRightClick && e.isLeftClick) {
            if (e.slot == 2) {
                inventory.setItem(10, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
                inventory.setItem(11, ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "", null))
                inventory.setItem(19, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적로 뜹니다)")))

                inventory.setItem(21, ItemManager.createNamedItem(Material.DARK_OAK_BUTTON, 1, "영역 수류탄", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적로 뜹니다)")))
                inventory.setItem(22, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적로 뜹니다)")))
                inventory.setItem(23, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적로 뜹니다)")))
                inventory.setItem(24, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적로 뜹니다)")))





            }


        }


    }
}



