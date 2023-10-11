package me.uwuaden.kotlinplugin

import me.uwuaden.kotlinplugin.itemManager.itemData.WorldItemData
import me.uwuaden.kotlinplugin.teamSystem.TeamClass
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

class WorldDataManager(var teams: ArrayList<TeamClass> = ArrayList(), val worldTimer: HashMap<World, Long> = HashMap(), val playerKill: HashMap<UUID, Int> = HashMap(), val worldMode: HashMap<World, String> = HashMap(), val GameEndedWorld: ArrayList<World> = ArrayList(), val WorldDroppedItemData: HashMap<World, WorldItemData> = HashMap(), var deadPlayer: MutableSet<Player> = mutableSetOf(), var totalPlayer: Int = 50, var avgMMR: Int = 0) {

}