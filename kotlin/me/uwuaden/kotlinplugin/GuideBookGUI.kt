package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.assets.CustomItemData
import me.uwuaden.kotlinplugin.assets.ItemManipulator.addEnchant
import me.uwuaden.kotlinplugin.assets.ItemManipulator.addName
import me.uwuaden.kotlinplugin.assets.ItemManipulator.setName
import me.uwuaden.kotlinplugin.itemManager.ItemManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

object GuideBookGUI {
    fun openFileDropInv(player: Player) {
        val inventory = Bukkit.createInventory(null, 54, "§e§lItem Guide Book")
        //아이템 종류
        for (i in 0 until 9) inventory.setItem(i, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, " ", null))
        for (i in 9 until 18) inventory.setItem(i, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ", null))
        for (i in 0..2) inventory.setItem(i*9+26, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ", null))
        for (z in 0..3) for (i in 18..19) inventory.setItem(i+z*9, ItemManager.createNamedItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ", null))
        for (i in 45 until 54) inventory.setItem(i, ItemManager.createNamedItem(Material.WHITE_STAINED_GLASS_PANE, 1, " ", null))

        inventory.setItem(0, ItemManager.createNamedItem(Material.IRON_PICKAXE, 1, "필드(기본)", null))
        inventory.setItem(1, ItemManager.createNamedItem(Material.DIAMOND_PICKAXE, 1, "필드(유틸)", null))
        inventory.setItem(2, ItemManager.createNamedItem(Material.CHEST, 1, "보급 아이템", null))


        inventory.setItem(53, ItemManager.createNamedItem(Material.REDSTONE_TORCH, 1, "§a가이드 북", listOf("§7닭갈비에 존재하는 모든 아이템에 대한", "§7상세 설명들이 담겨있습니다!")))

        //inventory.setItem(10, ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, " ", null))  //설명 종류 활성 상태 여부 -> 인첸트로 대체

        for (i in 18..19) inventory.setItem(i, ItemManager.createNamedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "§e기본 드롭", listOf("§7다음 아이템 여러개가 필드에 등장합니다.")))

        inventory.setItem(20, ItemManager.createNamedItem(Material.ARROW, 1, "§f화살", null))

        inventory.setItem(21, ItemManager.createNamedItem(Material.COOKED_BEEF, 1, "§f스테이크", null))

        //기본드랍 - 확률
        for (i in listOf(27, 28, 36, 37)) {
            inventory.setItem(i, ItemManager.createNamedItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "§e확정 드롭", listOf("§7다음 아이템 중 하나가 필드에 등장합니다.", "§7이름이 노락색이거나 노란색 별이 표기된 경우, 더욱 낮은 확률로 등장합니다.")))
        }


        inventory.setItem(29, ItemManager.createNamedItem(Material.IRON_AXE, 1, "§f도끼", listOf("§7도끼는 유리나 문을 좌클릭으로 부술 수 있습니다", " ", "§7다음 중 하나 획득 가능:", "  §f돌 도끼", "  §e철 도끼")).addEnchant(Enchantment.DIG_SPEED, 3))

        inventory.setItem(30, ItemManager.createNamedItem(Material.IRON_PICKAXE, 1, "§f곡괭이", listOf("§7다음 중 하나 획득 가능:", "  §f돌 곡괭이", "  §e철 곡괭이")).addEnchant(Enchantment.DIG_SPEED, 3))

        inventory.setItem(31, CustomItemData.getShield().setName("§f방패"))

        inventory.setItem(32, CustomItemData.getEnchantedShield().setName("§e인첸트된 방패"))

        inventory.setItem(33, ItemManager.createNamedItem(Material.IRON_CHESTPLATE, 1, "§f철 갑옷", listOf("§7다음 중 하나 획득 가능:", "  §f철 모자", "  §f철 흉갑", "  §f철 바지", "  §f철 신발")))

        inventory.setItem(34, ItemManager.createNamedItem(Material.DIAMOND_CHESTPLATE, 1, "§e다이아몬드 갑옷", listOf("§7다음 중 하나 획득 가능:", "  §e다이아몬드 모자", "  §e다이아몬드 흉갑", "  §e다이아몬드 바지", "  §e다이아몬드 신발")))

        inventory.setItem(
            38, ItemManager.createNamedItem(
            Material.IRON_SWORD,
            1,
            "§f철 검",
            null))

        inventory.setItem(
            39,
            ItemManager.createNamedItem(
            Material.DIAMOND_SWORD,
            1,
            "§e다이아몬드 검",
            null))

        inventory.setItem(40, ItemManager.createNamedItem(Material.BOW, 1, "§f활", listOf("§7활은 방패를 해제시킬 수 있습니다!")))

        inventory.setItem(41, ItemManager.createNamedItem(Material.BOW, 1, "§e활 (힘)", listOf("§7활은 방패를 해제시킬 수 있습니다!")).addEnchant(Enchantment.ARROW_DAMAGE, 1))

        inventory.setItem(42, ItemManager.createNamedItem(Material.GOLDEN_APPLE, 1, "§f황금사과", null))
        inventory.setItem(43, CustomItemData.getGoldenCarrot().addName("§e*"))

        player.openInventory(inventory)
    }
}