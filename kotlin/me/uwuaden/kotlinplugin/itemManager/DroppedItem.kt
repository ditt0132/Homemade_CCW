package me.uwuaden.kotlinplugin.itemManager

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import java.util.*

class DroppedItem(var uuid: UUID, var loc: Location, var itemGenerated: Boolean = false ,var items: HashMap<Int, ItemStack> = HashMap<Int, ItemStack>(), var isLocated: Boolean = false, var size: Int = 3) {

}