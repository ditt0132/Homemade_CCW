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
        //아이템 종류
        inventory.setItem(1, ItemManager.createNamedItem(Material.IRON_PICKAXE, 1, "필드(기본)", null))
        inventory.setItem(2, ItemManager.createNamedItem(Material.DIAMOND_PICKAXE, 1, "필드(유틸)", null))
        inventory.setItem(3, ItemManager.createNamedItem(Material.CHEST, 1, "보급 아이템", null))

        for (i in 4 .. 8 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null))
        }

        inventory.setItem(
            9,
            ItemManager.createNamedItem(Material.REDSTONE_TORCH,
                1,
                "가이드 북",
                listOf("${ChatColor.GRAY}닭갈비에 존재하는 모든 아이템에 대한",
                       "${ChatColor.GRAY}상세 설명들이 담겨있습니다!")))

        for (i in 10 .. 18 ){ //2줄 회색 배경 및 아이템 설명 종류 활성 상태 여부
            inventory.setItem(i,
                ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE,
                    1,
                    "",
                    null))
        }
        inventory.setItem(10, ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "", null))

        //기본드랍 - 확정(설명)
        inventory.setItem(
            19,
            ItemManager.createNamedItem(Material.REDSTONE_TORCH,
                1,
                "",
                listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확정적으로 뜹니다(여러개)")))

        inventory.setItem(20, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))

        inventory.setItem(
            21,
            ItemManager.createNamedItem(Material.ARROW,
                1,
                "화살",
                null))

        inventory.setItem(
            22,
            ItemManager.createNamedItem(Material.COOKED_BEEF,
                1,
                "스테이크",
                null))

        for (i in 23 .. 27 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.AIR, 1, "", null))
        }

        //기본드랍 - 확률
        inventory.setItem(
            28,
            ItemManager.createNamedItem(Material.REDSTONE_TORCH,
                1,
                "",
                listOf("${ChatColor.GRAY}다음 아이템들이 필드에 확률적으로 뜹니다(중복 가능)")))

        inventory.setItem(
            29,
            ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE,
                1,
                "",
                null))

        val GuidbookStoneAxe = ItemManager.createNamedItem(
            Material.STONE_AXE,
            1,
            "돌 도끼",
            listOf("도끼는 유리나 문을 좌클릭으로 부술 수 있습니다")
        )
        GuidbookStoneAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(
            31, GuidbookStoneAxe)

        val GuidbookStonePickAxe = ItemManager.createNamedItem(
            Material.STONE_PICKAXE,
            1,
            "돌 곡괭이",
            null)
        GuidbookStonePickAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(
            32, GuidbookStonePickAxe)

        val GuidbookIronPickaxe = ItemManager.createNamedItem(
            Material.IRON_AXE,
            1,
            "철 도끼",
            listOf("도끼는 유리나 문을 좌클릭으로 부술 수 있습니다")
        )
        GuidbookIronPickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(
            33, GuidbookIronPickaxe)

        val GuidbookIronPickAxe = ItemManager.createNamedItem(
            Material.IRON_PICKAXE,
            1,
            "철 곡괭이",
            null)
        GuidbookIronPickAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3)
        inventory.setItem(
            34, GuidbookIronPickAxe)

        inventory.setItem(
            35, ItemManager.createNamedItem(
                Material.IRON_CHESTPLATE,
                1,
                "철 갑옷",
                listOf("${ChatColor.GRAY}다음 중 하나 획득 가능 :"
                ,"${ChatColor.WHITE}철 모자"
                ,"${ChatColor.WHITE}철 흉갑"
                ,"${ChatColor.WHITE}철 바지"
                ,"${ChatColor.WHITE}철 신발")))

        inventory.setItem(
            36,
            ItemManager.createNamedItem(
                Material.DIAMOND_CHESTPLATE,
                1,
                "다이아몬드 갑옷",
                listOf("${ChatColor.GRAY}다음 중 하나 획득 가능 :"
                ,"${ChatColor.WHITE}다이아몬드 모자"
                ,"${ChatColor.WHITE}다이아몬드 흉갑"
                ,"${ChatColor.WHITE}다이아몬드 바지"
                ,"${ChatColor.WHITE}다이아몬드 신발")))

        inventory.setItem(37, ItemManager.createNamedItem(Material.AIR, 1, "", null))
        inventory.setItem(38, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, "", null))

        inventory.setItem(
            39,
            ItemManager.createNamedItem(
            Material.IRON_SWORD,
            1,
            "철 검",
            null))

        inventory.setItem(
            40,
            ItemManager.createNamedItem(
            Material.DIAMOND_SWORD,
            1,
            "다이아몬드 검",
            null))

        val GuidbookBow = ItemManager.createNamedItem(
            Material.BOW,
            1,
            "활",
            listOf("${ChatColor.GRAY}활은 방패를 해제시킬 수 있습니다!"))
                inventory.setItem(
                    41, GuidbookBow)

        val GuidbookPowerBow = ItemManager.createNamedItem(
            Material.IRON_PICKAXE,
            1,
            "활",
            listOf("${ChatColor.GRAY}활은 방패를 해제시킬 수 있습니다!"))
        GuidbookPowerBow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
            inventory.setItem(
                42, GuidbookBow)

        inventory.setItem(
            43,
            ItemManager.createNamedItem(
                Material.GOLDEN_APPLE,
                1,
                "황금사과",
                null))

        for (i in 44 .. 45 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.AIR, 1, "", null))
        }
        for (i in 46 .. 54 ){
            inventory.setItem(i, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, "", null))
        }





























    }
}