package me.uwuaden.kotlinplugin.assets

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object ItemManipulator {
    fun ItemStack.setCount(amount: Int): ItemStack {
        val cloneItem = clone()
        cloneItem.amount = amount
        return cloneItem
    }
    fun ItemStack.enchant(enchantment: Enchantment, level: Int): ItemStack {
        val cloneItem = clone()
        cloneItem.addEnchantment(enchantment, level)
        return cloneItem
    }
    fun ItemStack.getName(): String {
        return this.itemMeta.displayName
    }
    fun itemName(item: ItemStack): String {
        return item.itemMeta.displayName
    }


}