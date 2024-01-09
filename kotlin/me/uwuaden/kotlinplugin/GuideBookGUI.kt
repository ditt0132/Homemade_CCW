package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.itemManager.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object GuideBookGUI {
    fun openGuideBook(player: Player) {
        val inventory = Bukkit.createInventory(null, 54, "§e§lItem Guide Book")
        //종류
        inventory.setItem(1, ItemManager.createNamedItem(Material.IRON_PICKAXE, 1, "필드(기본)", null))
        inventory.setItem(2, ItemManager.createNamedItem(Material.DIAMOND_PICKAXE, 1, "필드(유틸)", null))
        inventory.setItem(3, ItemManager.createNamedItem(Material.CHEST, 1, "보급 아이템", null))
        inventory.setItem(4, ItemManager.createNamedItem(Material.ENCHANTED_GOLDEN_APPLE, 1, "Elit Item", null))


        inventory.setItem(5, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null))
        for (i in 6 .. 8 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.RED_STAINED_GLASS_PANE, 1, "", null))
        }

        inventory.setItem(9, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "가이드 북", listOf
            ("${ChatColor.GRAY}닭갈비에 존재하는 모든 아이템에 대한","${ChatColor.GRAY}상세 설명들이 담겨있습니다!")))

        for (i in 10 .. 18 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
        }
        inventory.setItem(10, ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "", null))

        //기본드랍 - 확정
        inventory.setItem(19, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확정적으로 뜹니다(여러개)")))
        inventory.setItem(20, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
        inventory.setItem(21, ItemManager.createNamedItem(Material.ARROW, 1, "화살", null))
        inventory.setItem(22, ItemManager.createNamedItem(Material.COOKED_BEEF, 1, "스테이크", null))
        for (i in 23 .. 27 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.AIR, 1, "", null))
        }

        //기본드랍 - 확률
        inventory.setItem(28, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "", listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적으로 뜹니다(중복 가능)")))
        inventory.setItem(29, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
        val GuidbookStoneAxe = ItemManager.createNamedItem(Material.STONE_AXE, 1, "돌 도끼", null)
        GuidbookStoneAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(31, GuidbookStoneAxe)
        val GuidbookStonePickAxe = ItemManager.createNamedItem(Material.STONE_PICKAXE, 1, "돌 곡괭이", null)
        GuidbookStonePickAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(32, GuidbookStonePickAxe)
        val GuidbookIronPickaxe = ItemManager.createNamedItem(Material.IRON_AXE, 1, "철 도끼", null)
        GuidbookIronPickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(33, GuidbookIronPickaxe)
        val GuidbookIronPickAxe = ItemManager.createNamedItem(Material.IRON_PICKAXE, 1, "철 곡괭이", null)
        GuidbookIronPickAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(34, GuidbookIronPickAxe)
        inventory.setItem(35, ItemManager.createNamedItem(Material.IRON_CHESTPLATE, 1, "철 갑옷", listOf
            ("${ChatColor.GRAY}다음 중 하나 획득 가능 :"
            ,"${ChatColor.WHITE}철 모자"
            ,"${ChatColor.WHITE}철 흉갑"
            ,"${ChatColor.WHITE}철 바지"
            ,"${ChatColor.WHITE}철 신발")))
        inventory.setItem(36, ItemManager.createNamedItem(Material.DIAMOND_CHESTPLATE, 1, "다이아몬드 갑옷", listOf
            ("${ChatColor.GRAY}다음 중 하나 획득 가능 :"
            ,"${ChatColor.WHITE}다이아몬드 모자"
            ,"${ChatColor.WHITE}다이아몬드 흉갑"
            ,"${ChatColor.WHITE}다이아몬드 바지"
            ,"${ChatColor.WHITE}다이아몬드 신발")))
        inventory.setItem(37, ItemManager.createNamedItem(Material.AIR, 1, "", null))
        inventory.setItem(38, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))
        inventory.setItem(39, ItemManager.createNamedItem(Material.IRON_SWORD, 1, "", null))
        inventory.setItem(40, ItemManager.createNamedItem(Material.DIAMOND_SWORD, 1, "", null))
        for (i in 41 .. 45 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.AIR, 1, "", null))
        }
        for (i in 46 .. 54 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.RED_STAINED_GLASS_PANE, 1, "", null))
        }
        inventory.setItem(50, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null))





























    }
}