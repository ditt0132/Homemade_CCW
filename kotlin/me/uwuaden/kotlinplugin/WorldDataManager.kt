package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.itemManager.DroppedItem
import me.uwuaden.kotlinplugin.itemManager.itemData.WorldItemData
import me.uwuaden.kotlinplugin.teamSystem.TeamClass
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

class WorldDataManager(var teams: ArrayList<TeamClass> = ArrayList(), val worldTimer: HashMap<World, Long> = HashMap(), val playerKill: HashMap<UUID, Int> = HashMap(), var worldMode: String = "Solo", var gameEndedWorld: Boolean = false, var worldDroppedItemData: WorldItemData = WorldItemData(), var deadPlayer: MutableSet<Player> = mutableSetOf(), var totalPlayer: Int = 50, var avgMMR: Int = 0, var dataInt: Int = 0, var dataLong: Long = 0, var playerItemList: HashMap<UUID, MutableSet<String>> = HashMap(), var worldFolderName: String = "test", var droppedItems: ArrayList<DroppedItem> = ArrayList()) {

}