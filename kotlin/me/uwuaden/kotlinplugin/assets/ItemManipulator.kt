package me.uwuaden.kotlinplugin.assets

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

object ItemManipulator {


    fun ItemStack.setCount(amount: Int): ItemStack {
        val cloneItem = clone()
        cloneItem.amount = amount
        return cloneItem
    }
    fun ItemStack.setName(name: String): ItemStack {
        val cloneItem = clone()
        val meta = cloneItem.itemMeta
        meta.setDisplayName(name)
        cloneItem.itemMeta = meta
        return cloneItem
    }
    fun ItemStack.addName(name: String): ItemStack {
        val cloneItem = clone()
        val meta = cloneItem.itemMeta
        meta.setDisplayName(meta.displayName + name)
        cloneItem.itemMeta = meta
        return cloneItem
    }
    fun ItemStack.addEnchant(enchantment: Enchantment, level: Int): ItemStack {
        val cloneItem = clone()
        cloneItem.addEnchantment(enchantment, level)
        return cloneItem
    }
    fun ItemStack.addUnsafeEnchant(enchantment: Enchantment, level: Int): ItemStack {
        val cloneItem = clone()
        val meta = cloneItem.itemMeta
        meta.addEnchant(enchantment, level, true)
        cloneItem.itemMeta = meta
        return cloneItem
    }
    fun ItemStack.getName(): String {
        return this.itemMeta.displayName
    }
    fun itemName(item: ItemStack): String {
        return item.itemMeta.displayName
    }
    fun ItemStack.addAttribute(attribute: Attribute, double: Double, operation: AttributeModifier.Operation, equipmentSlot: EquipmentSlot): ItemStack {
        val item = this.clone()
        val meta = item.itemMeta
        meta.addAttributeModifier(attribute, AttributeModifier(UUID.randomUUID(), "", double, operation, equipmentSlot))
        item.itemMeta = meta
        return item
    }
    fun ItemStack.clearLore(): ItemStack {
        val item = this.clone()
        val meta = item.itemMeta
        meta.lore?.clear()
        item.itemMeta = meta
        return item
    }
    fun ItemStack.addLores(lore: List<String>): ItemStack {
        val item = this.clone()
        if (item.itemMeta.lore == null) {
            val meta = item.itemMeta
            meta.lore = lore
            item.itemMeta = meta
            return item
        }
        val meta = item.itemMeta
        val newLore = meta.lore!! + lore
        meta.lore = newLore
        item.itemMeta = meta
        return item
    }
    fun ItemStack.addCustomModelData(id: Int): ItemStack {
        val item = this.clone()
        val meta = item.itemMeta
        meta.setCustomModelData(id)
        item.itemMeta = meta
        return item
    }

}