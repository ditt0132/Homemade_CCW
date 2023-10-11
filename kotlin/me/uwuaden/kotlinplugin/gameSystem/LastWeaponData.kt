package me.uwuaden.kotlinplugin.gameSystem

import me.uwuaden.kotlinplugin.Main.Companion.lastWeapon
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class LastWeaponData (var item: ItemStack, var effTimeMilli: Long) {
    fun remove() {
        lastWeapon.filterNot { it.value == this }
    }
    fun set(attacker: Player) {
        lastWeapon[attacker] = this
    }
}