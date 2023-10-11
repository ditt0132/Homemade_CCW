package me.uwuaden.kotlinplugin.assets

import org.bukkit.inventory.ItemStack

object ItemManipulator {
    fun ItemStack.setCount(amount: Int): ItemStack {
        val cloneItem = clone()
        cloneItem.amount = amount
        return cloneItem
    }
    fun itemName(item: ItemStack): String {
        return item.itemMeta.displayName
    }

}